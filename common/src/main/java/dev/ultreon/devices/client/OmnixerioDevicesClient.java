package dev.ultreon.devices.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.system.SystemApp;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static dev.ultreon.devices.OmnixerioDevices.LOGGER;

public abstract class OmnixerioDevicesClient {
    static List<AppInfo> allowedApps = new ArrayList<>();
    protected final List<Application> applications = new ArrayList<>();

    private static OmnixerioDevicesClient instance;

    public static OmnixerioDevicesClient getInstance() {
        return instance;
    }

    protected OmnixerioDevicesClient() {
        instance = this;
    }

    private static void setupClientEvents() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((_ -> {
            LOGGER.debug("Client disconnected from server");

            allowedApps = null;
            DeviceConfig.restore();
        }));
    }


    public static boolean hasAllowedApplications() {
        return allowedApps != null;
    }

    public static List<AppInfo> getAllowedApplications() {
        if (allowedApps == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(allowedApps);
    }

    @NotNull
    private static AppInfo generateAppInfo(Identifier identifier, Class<? extends Application> clazz) {
        LOGGER.debug("Generating app info for {}", identifier.toString());

        return new AppInfo(identifier, SystemApp.class.isAssignableFrom(clazz));
    }

    public static void setAllowedApps(List<AppInfo> allowedApps) {
        OmnixerioDevicesClient.allowedApps = allowedApps;
    }

    public abstract List<Application> getApplications();

    public boolean registerPrint(Identifier identifier, Class<? extends IPrint> classPrint) {
        LOGGER.debug("Registering print: {}", identifier.toString());

        try {
            Constructor<? extends IPrint> constructor = classPrint.getConstructor();
            IPrint print = constructor.newInstance();
            Class<? extends IPrint.Renderer> classRenderer = print.getRenderer();
            try {
                IPrint.Renderer renderer = classRenderer.getConstructor().newInstance();
                Map<String, IPrint.Renderer> idToRenderer = getRegisteredRenders(); //ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
                if (idToRenderer == null) {
                    idToRenderer = new HashMap<>();
                    setRegisteredRenders(idToRenderer);
                    //ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, idToRenderer, "registeredRenders");
                }
                idToRenderer.put(identifier.toString(), renderer);
            } catch (InstantiationException e) {
                LOGGER.error("The print renderer '{}' is missing an empty constructor and could not be registered!", classRenderer.getName());
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("The print '{}' is missing an empty constructor and could not be registered!", classPrint.getName());
        }
        return false;
    }
    protected abstract void setRegisteredRenders(Map<String, IPrint.Renderer> map);

    protected abstract Map<String, IPrint.Renderer> getRegisteredRenders();

    public void initApps(OmnixerioDevices.ApplicationSupplier app, AtomicReference<Application> application, Identifier identifier) {
        Application appl = app.get().get();
        List<Application> apps = getApplications(); /*ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");*/
        assert apps != null;
        apps.add(appl);

        appl.setInfo(generateAppInfo(identifier, appl.getClass()));

        application.set(appl);
    }

    /**
     * DO NOT CALL: FOR INTERNAL USE ONLY
     */
    @Nullable
    @ApiStatus.Internal
    public Application registerApplication(Identifier identifier, OmnixerioDevices.ApplicationSupplier app) {
        if ("minecraft".equals(identifier.getNamespace())) {
            throw new IllegalArgumentException("Identifier cannot be \"minecraft\"!");
        }

        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }

        if (app.isSystem()) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }

        AtomicReference<Application> application = new AtomicReference<>(null);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> OmnixerioDevicesClient.getInstance().initApps(app, application, identifier));
        return application.get();
    }

}
