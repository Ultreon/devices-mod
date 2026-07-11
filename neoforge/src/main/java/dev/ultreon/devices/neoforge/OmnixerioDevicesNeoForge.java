package dev.ultreon.devices.neoforge;

import com.mojang.logging.LogUtils;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.ultreon.devices.*;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.init.ModStats;
import dev.ultreon.devices.init.RegistrationHandler;
import dev.ultreon.devices.neoforge.client.OmnixerioDevicesClientNeoForge;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public final class OmnixerioDevicesNeoForge {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final OmnixerioDevices instance;

    public IEventBus modEventBus;

    public OmnixerioDevicesNeoForge(IEventBus modEventBus, ModContainer container) throws LaunchException {
        super();

        this.modEventBus = modEventBus;
        this.modEventBus.register(BuiltinAppsRegistration.class);

        OmnixerioDevices.preInit();

        // Common side stuff
        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
        container.registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        LOGGER.info("Registering common setup handler, and load complete handler.");
        this.modEventBus.addListener(this::fmlCommonSetup);
        this.modEventBus.addListener(this::fmlLoadComplete);

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        this.modEventBus.addListener(this::fmlServerSetup);

        // Client side stuff
        if (FMLEnvironment.getDist().isClient()) {
            OmnixerioDevicesClientNeoForge client = new OmnixerioDevicesClientNeoForge();
            client.init(modEventBus);
            if (!DatagenModLoader.isRunningDataGen()) {
                LOGGER.info("Registering the reload listener.");
            }
        }

        // Register ourselves for server and other game events we are interested in
        LOGGER.info("Registering mod class to forge events.");
        instance = new OmnixerioDevices() {
            @Override
            public int getBurnTime(ItemStack stack, RecipeType<?> type, HolderLookup.Provider provider) {
                return FuelRegistry.get(stack, type, FuelValues.vanillaBurnTimes(provider, FeatureFlagSet.of()));
            }

            @Override
            protected void registerApplicationEvent() {
                OmnixerioDevicesNeoForge.this.modEventBus.post(new NeoForgeApplicationRegistration());
            }
        };
    }

    private void fmlCommonSetup(FMLCommonSetupEvent t) {
        this.instance.init();
    }

    private void fmlLoadComplete(FMLLoadCompleteEvent t) {
        this.instance.loadComplete();
        ModStats.init();
    }

    private void fmlServerSetup(FMLDedicatedServerSetupEvent t) {
        this.instance.serverSetup();
    }
}
