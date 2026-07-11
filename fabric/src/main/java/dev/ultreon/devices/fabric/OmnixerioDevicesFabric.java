package dev.ultreon.devices.fabric;

import dev.architectury.platform.Platform;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.utils.Env;
import dev.ultreon.devices.ClientModEvents;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.fabric.client.OmnixerioDevicesClientFabric;
import dev.ultreon.devices.init.RegistrationHandler;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.neoforged.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OmnixerioDevicesFabric extends OmnixerioDevices implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigRegistry.INSTANCE.register(OmnixerioDevices.MOD_ID, ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        if (Platform.getEnvironment() == Env.CLIENT) {
            new OmnixerioDevicesClientFabric();

            ClientModEvents.clientSetup();
            ClientModEvents.registerRenderers();
            ClientModEvents.registerItemProperties();
        }

        this.init();

        RegistrationHandler.register();

    }

    @Override
    public int getBurnTime(ItemStack stack, RecipeType<?> type, HolderLookup.Provider provider) {
        return FuelRegistry.get(stack, type, FuelValues.vanillaBurnTimes(provider, FeatureFlagSet.of()));
    }

    @Override
    protected void registerApplicationEvent() {
        var eve = FabricLoader.getInstance().getEntrypointContainers(OmnixerioDevices.MOD_ID + ":application_registration", FabricApplicationRegistration.class);
        EntrypointContainer<FabricApplicationRegistration> builtin = null;
        for (EntrypointContainer<FabricApplicationRegistration> fabricApplicationRegistrationEntrypointContainer : eve) {
            if (fabricApplicationRegistrationEntrypointContainer.getProvider().getMetadata().getId().equals(OmnixerioDevices.MOD_ID)) {
                builtin = fabricApplicationRegistrationEntrypointContainer;
            }
        }
        assert builtin != null;
        builtin.getEntrypoint().registerApplications();
        (eve = new ArrayList<>(eve)).remove(builtin);
        for (EntrypointContainer<FabricApplicationRegistration> fabricApplicationRegistrationEntrypointContainer : eve) {
            fabricApplicationRegistrationEntrypointContainer.getEntrypoint().registerApplications();
        }
    }
}