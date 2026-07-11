package dev.ultreon.devices.fabric.client;

import dev.ultreon.devices.ClientModEvents;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.client.OmnixerioDevicesClient;
import dev.ultreon.devices.core.Laptop;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;
import java.util.Map;

public class OmnixerioDevicesClientFabric extends OmnixerioDevicesClient {
    @Override
    public List<Application> getApplications() {
        return Laptop.getApplicationsForFabric();
    }

    @Override
    protected void setRegisteredRenders(Map<String, IPrint.Renderer> map) {
        PrintingManager.setRegisteredRenders(map);
    }

    @Override
    protected Map<String, IPrint.Renderer> getRegisteredRenders() {
        return PrintingManager.getRegisteredRenders();
    }
}
