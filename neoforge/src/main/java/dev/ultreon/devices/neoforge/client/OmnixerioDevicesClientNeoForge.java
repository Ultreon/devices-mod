package dev.ultreon.devices.neoforge.client;

import dev.ultreon.devices.ClientModEvents;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.client.OmnixerioDevicesClient;
import dev.ultreon.devices.core.Laptop;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;

public class OmnixerioDevicesClientNeoForge extends OmnixerioDevicesClient {
    @Override
    public List<Application> getApplications() {
        return ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
    }

    @Override
    protected void setRegisteredRenders(Map<String, IPrint.Renderer> map) {
        ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, map, "registeredRenders");
    }

    @Override
    protected Map<String, IPrint.Renderer> getRegisteredRenders() {
        return ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
    }

    public void init(IEventBus modEventBus) {
        ClientModEvents.clientSetup();
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent evt) {
        ClientModEvents.registerRenderers();
        ClientModEvents.registerItemProperties();
    }
}
