package dev.ultreon.devices;

import com.google.common.base.Suppliers;
import com.google.gson.*;
import com.mojang.serialization.Lifecycle;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.utils.OnlineRequest;
import dev.ultreon.devices.core.client.ClientNotification;
import dev.ultreon.devices.core.io.task.*;
import dev.ultreon.devices.core.network.task.TaskConnect;
import dev.ultreon.devices.core.network.task.TaskGetDevices;
import dev.ultreon.devices.core.network.task.TaskPing;
import dev.ultreon.devices.core.print.task.TaskPrint;
import dev.ultreon.devices.core.task.TaskInstallApp;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.event.WorldDataHandler;
import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.devices.network.clientbound.S2CSyncApplicationsPacket;
import dev.ultreon.devices.network.clientbound.S2CSyncConfigPacket;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.devices.programs.IconsApp;
import dev.ultreon.devices.programs.PixelPainterApp;
import dev.ultreon.devices.programs.TestApp;
import dev.ultreon.devices.programs.auction.task.TaskAddAuction;
import dev.ultreon.devices.programs.auction.task.TaskBuyItem;
import dev.ultreon.devices.programs.auction.task.TaskGetAuctions;
import dev.ultreon.devices.programs.debug.TextAreaApp;
import dev.ultreon.devices.programs.email.task.*;
import dev.ultreon.devices.programs.example.ExampleApp;
import dev.ultreon.devices.programs.example.task.TaskNotificationTest;
import dev.ultreon.devices.programs.system.task.*;
import dev.ultreon.devices.util.SiteRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class OmnixerioDevices {
    public static final boolean DEVELOPER_MODE = Platform.isDevelopmentEnvironment();
    public static final String MOD_ID = "omnixerio_devices";
    public static final Logger LOGGER = LoggerFactory.getLogger("OmnixerioDevices");

    public static final DeferredSupplier<CreativeModeTab> TAB_DEVICE = DeviceTab.create();
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));
    public static final List<SiteRegistration> SITE_REGISTRATIONS = new ProtectedArrayList<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final DevicesEarlyConfig EARLY_CONFIG = DevicesEarlyConfig.load();
    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();
    private static final String GITWEB_REGISTER_URL = "https://ultreon.gitlab.io/gitweb/site_register.json";
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final SiteRegisterStack SITE_REGISTER_STACK = new SiteRegisterStack();

    //---- Registry : Start ----//
    public static MappedRegistry<TrayItem> trayItemRegistry = new MappedRegistry<>(ResourceKey.createRegistryKey(id("tray_item")), Lifecycle.stable());
    //---- Registry : End ----//

    private static OmnixerioDevices instance;

    private static MinecraftServer server;
    private static TestManager tests;
    private static List<AppInfo> allowedApps;

    protected OmnixerioDevices() {
        OmnixerioDevices.instance = this;
    }

    public static OmnixerioDevices getInstance() {
        return instance;
    }

    public static boolean hasAllowedApplications() {
        return allowedApps != null;
    }

    public static List<AppInfo> getAllowedApplications() {
        return allowedApps;
    }

    public void init() {
        registerApplications();

        if (ArchitecturyTarget.getCurrentTarget().equals("fabric")) {
            preInit();
            serverSetup();
        }

        WorldDataHandler.init();

        // STOPSHIP: 3/11/2022 should be moved to dedicated testmod
        final var property = System.getProperty("ultreon.omnixerio_devices.tests");
        tests = new TestManager();
        if (property != null) {
            String[] split = property.split(",");
            tests.load(Set.of(split));
        }

        //LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        LOGGER.info("Doing some common setup.");

        PacketHandler.init();

        EnvExecutor.runInEnv(Env.CLIENT, () -> OmnixerioDevices::setupSiteRegistrations);

        setupEvents();

        if (!Platform.isNeoForge()) {
            loadComplete();
        }
    }

    public static void preInit() {
        if (DEVELOPER_MODE && !Platform.isDevelopmentEnvironment()) {
            throw new LaunchException();
        }

        DeviceConfig.init();
    }


    public static boolean isDevelopmentPreview() {
        return IS_DEV_PREVIEW;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static TestManager getTests() {
        return tests;
    }

    public void serverSetup() {
        LOGGER.info("Doing some server setup.");
    }

    public void loadComplete() {
        LOGGER.info("Doing some load complete handling.");
    }

    public void registerApplications() {
        // Applications (Both)

        registerApplicationEvent();

        // Core
        TaskManager.registerTask(TaskUpdateApplicationData::new);
        TaskManager.registerTask(TaskPrint::new);
        TaskManager.registerTask(TaskUpdateSystemData::new);
        TaskManager.registerTask(TaskConnect::new);
        TaskManager.registerTask(TaskPing::new);
        TaskManager.registerTask(TaskGetDevices::new);

        // File browser
        TaskManager.registerTask(TaskSendAction::new);
        TaskManager.registerTask(TaskSetupFileBrowser::new);
        TaskManager.registerTask(TaskGetFiles::new);
        TaskManager.registerTask(TaskGetStructure::new);
        TaskManager.registerTask(TaskGetMainDrive::new);

        // App Store
        TaskManager.registerTask(TaskInstallApp::new);

        // Ender Mail
        TaskManager.registerTask(TaskUpdateInbox::new);
        TaskManager.registerTask(TaskSendEmail::new);
        TaskManager.registerTask(TaskCheckEmailAccount::new);
        TaskManager.registerTask(TaskRegisterEmailAccount::new);
        TaskManager.registerTask(TaskDeleteEmail::new);
        TaskManager.registerTask(TaskViewEmail::new);

        if (Platform.isDevelopmentEnvironment() || OmnixerioDevices.EARLY_CONFIG.enableBetaApps) {
            // Auction
            TaskManager.registerTask(TaskAddAuction::new);
            TaskManager.registerTask(TaskGetAuctions::new);
            TaskManager.registerTask(TaskBuyItem::new);

            // Bank
            TaskManager.registerTask(TaskDeposit::new);
            TaskManager.registerTask(TaskWithdraw::new);
            TaskManager.registerTask(TaskGetBalance::new);
            TaskManager.registerTask(TaskPay::new);
            TaskManager.registerTask(TaskAdd::new);
            TaskManager.registerTask(TaskRemove::new);
        }

        if (Platform.isDevelopmentEnvironment() || OmnixerioDevices.EARLY_CONFIG.enableDebugApps) {
            // Applications (Developers)
            ApplicationManager.registerApplication(id("example"), () -> ExampleApp::new, false);
            ApplicationManager.registerApplication(id("icons"), () -> IconsApp::new, false);
            ApplicationManager.registerApplication(id("text_area"), () -> TextAreaApp::new, false);
            ApplicationManager.registerApplication(id("test"), () -> TestApp::new, false);

            TaskManager.registerTask(TaskNotificationTest::new);
        }

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> PrintingManager.registerPrint(id("picture"), PixelPainterApp.PicturePrint.class));
    }

    public abstract int getBurnTime(ItemStack stack, RecipeType<?> type, HolderLookup.Provider provider);

    protected abstract void registerApplicationEvent();

    public static String getModVersion() {
        return Platform.getMod(MOD_ID).getVersion();
    }

    public interface ApplicationSupplier {

        /**
         * Gets a result.
         *
         * @return a result
         */
        Supplier<Application> get();

        boolean isSystem();
    }

    public static void showNotification(CompoundTag tag) {
        LOGGER.debug("Showing notification");

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ClientNotification notification = ClientNotification.loadFromTag(tag);
            notification.push();
        });
    }
    @Deprecated
    public static Identifier res(String path) {
        return id(path);
    }

    private static void setupEvents() {
        LifecycleEvent.SERVER_STARTING.register((instance -> server = instance));
        LifecycleEvent.SERVER_STOPPED.register(_ -> server = null);

        PlayerEvent.PLAYER_JOIN.register((player -> {
            LOGGER.info("Player logged in: {}", player.getName());

            if (allowedApps != null) {
                PacketHandler.sendToClient(S2CSyncApplicationsPacket.create(allowedApps), player);
            }
            PacketHandler.sendToClient(new S2CSyncConfigPacket(DeviceConfig.writeSyncTag()), player);
        }));
    }

    private static void setupSiteRegistrations() {
        setupSiteRegistration(GITWEB_REGISTER_URL);
    }

    private static CompletableFuture<Void> setupSiteRegistration(String url) {
        SITE_REGISTER_STACK.push();

        enum Type {
            SITE_REGISTER, REGISTRATION
        }

        CompletableFuture<Void> future = new CompletableFuture<>();

        OnlineRequest.getInstance().make(url, (success, response) -> CompletableFuture.runAsync(() -> {
            if (success) {
                //Minecraft.getInstance().doRunTask(() -> {
                JsonElement root = JsonParser.parseString(response);
                DebugLog.log("root = " + root);
                JsonArray rootArray = root.getAsJsonArray();
                for (JsonElement rootRegister : rootArray) {
                    DebugLog.log("rootRegister = " + rootRegister);
                    JsonObject elem = rootRegister.getAsJsonObject();
                    var registrant = elem.get("registrant") != null ? elem.get("registrant").getAsString() : null;
                    var type = Type.REGISTRATION;
                    JsonElement typeElem;
                    if ((typeElem = elem.get("type")) != null && typeElem.isJsonPrimitive() && typeElem.getAsJsonPrimitive().isString()) {
                        switch (typeElem.getAsString()) {
                            case "registration" -> {
                            }
                            case "site-register" -> type = Type.SITE_REGISTER;
                            default -> {
                                LOGGER.error("Invalid element type: {}", typeElem.getAsString());
                                continue;
                            }
                        }
                    }

                    switch (type) {
                        case REGISTRATION -> {
                            @SuppressWarnings("all") //no
                            var dev = elem.get("dev") != null ? elem.get("dev").getAsBoolean() : false;
                            var site = elem.get("site").getAsString();
                            if (dev && !IS_DEV_PREVIEW) {
                                continue;
                            }
                            for (JsonElement registration : elem.get("registrations").getAsJsonArray()) {
                                var a = registration.getAsJsonObject().keySet();
                                var d = registration.getAsJsonObject();
                                for (String string : a) {
                                    var registrationType = d.get(string).getAsString();
                                    SITE_REGISTRATIONS.add(new SiteRegistration(registrant, string, registrationType, site));
                                }
                            }
                        }
                        case SITE_REGISTER -> {
                            if (!elem.has("register") || !elem.get("register").isJsonPrimitive() || !elem.get("register").getAsJsonPrimitive().isString()) {
                                continue;
                            }
                            var registerUrl = elem.get("register").getAsString();
                            try {
                                var registerFuture = setupSiteRegistration(registerUrl);
                                registerFuture.join();
                            } catch (Exception e) {
                                LOGGER.error("Error when loading site register: {}", registerUrl);
                            }
                        }
                    }
                }
            } else {
                LOGGER.error("Error occurred when loading site registrations at: {}", url);
                future.complete(null);
                return;
            }
            future.complete(null);
            SITE_REGISTER_STACK.pop();
        }));

        return future;
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }

    private static class ProtectedArrayList<T> extends ArrayList<T> {
        private final StackWalker stackWalker = StackWalker.getInstance(EnumSet.of(StackWalker.Option.RETAIN_CLASS_REFERENCE));
        private boolean frozen = false;

        private void freeze() {
            frozen = true;
        }

        private void freezeCheck() {
            if (frozen) throw new IllegalStateException("Already frozen!");
        }

        @Override
        public boolean add(T t) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.add(t);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.addAll(c);
        }

        @Override
        public void add(int index, T element) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.add(index, element);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean remove(Object o) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeAll(c);
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeIf(filter);
        }

        @Override
        public T remove(int index) {
            freezeCheck();
            if (stackWalker.getCallerClass() != OmnixerioDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(index);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private static class SiteRegisterStack extends Stack<Object> {
        public Object push() {
            return super.push(new Object());
        }

        @Override
        public synchronized Object pop() {
            Object pop = super.pop();
            if (isEmpty()) {
                ((ProtectedArrayList<SiteRegistration>) SITE_REGISTRATIONS).freeze();
            }
            return pop;
        }
    }


}