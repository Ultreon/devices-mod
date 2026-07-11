package com.jab125.testmod;

import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.fabric.BuiltinAppsRegistration;
import dev.ultreon.devices.fabric.FabricApplicationRegistration;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.Identifier;

public class ClientInit implements FabricApplicationRegistration, ClientModInitializer {
    @Override
    public void registerApplications() {
        ApplicationManager.registerApplication(new Identifier("devices-testmod", "test-app"), () -> TestApp::new, false);
    }

    @Override
    public void onInitializeClient() {

    }
}
