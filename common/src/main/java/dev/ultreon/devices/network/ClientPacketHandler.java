package dev.ultreon.devices.network;

import dev.architectury.networking.NetworkManager;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.client.OmnixerioDevicesClient;
import dev.ultreon.devices.network.clientbound.*;

import java.util.Objects;

public class ClientPacketHandler {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CNotificationPacket.TYPE, S2CNotificationPacket.CODEC, ClientPacketHandler::onNotification);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSyncConfigPacket.TYPE, S2CSyncConfigPacket.CODEC, ClientPacketHandler::onSyncConfig);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSyncApplicationsPacket.TYPE, S2CSyncApplicationsPacket.CODEC, ClientPacketHandler::onSyncApplications);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CResponsePacket.TYPE, S2CResponsePacket.CODEC, ClientPacketHandler::onTaskResponse);
    }

    private static void onNotification(S2CNotificationPacket value, NetworkManager.PacketContext context) {
        OmnixerioDevices.showNotification(value.notificationTag());
    }

    private static void onSyncConfig(S2CSyncConfigPacket value, NetworkManager.PacketContext context) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(value.configTag()));
    }

    private static void onTaskResponse(S2CResponsePacket value, NetworkManager.PacketContext context) {
        int id = value.id();
        boolean successful = value.successful();

        Task request = TaskManager.getTaskAndRemove(id);
        if (successful) request.setSuccessful();

        request.processResponse(value.responseData());
        request.callback(value.responseData());
    }

    private static void onSyncApplications(S2CSyncApplicationsPacket value, NetworkManager.PacketContext context) {
        OmnixerioDevicesClient.setAllowedApps(value.getAllowedApps());
    }
}
