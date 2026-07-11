package dev.ultreon.devices.network.clientbound;

import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.object.AppInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrCrayfish
 */
public record S2CSyncApplicationsPacket(
        List<Identifier> allowedApps
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CSyncApplicationsPacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevices.id("clientbound/sync_applications"));
    public static final StreamCodec<ByteBuf, S2CSyncApplicationsPacket> CODEC = StreamCodec.of((buf, packet) -> {
        ByteBufCodecs.VAR_INT.encode(buf, packet.allowedApps.size());
        for (Identifier allowedApp : packet.allowedApps) {
            Identifier.STREAM_CODEC.encode(buf, allowedApp);
        }
    }, buf -> {
        List<Identifier> allowedApps = new ArrayList<>();
        int size = ByteBufCodecs.VAR_INT.decode(buf);
        for (int i = 0; i < size; i++) {
            allowedApps.add(Identifier.STREAM_CODEC.decode(buf));
        }
        return new S2CSyncApplicationsPacket(allowedApps);
    });

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static S2CSyncApplicationsPacket create(List<AppInfo> allowedApps) {
        return new S2CSyncApplicationsPacket(allowedApps.stream().map(AppInfo::getAppId).toList());
    }

    public List<AppInfo> getAllowedApps() {
        List<AppInfo> list = new ArrayList<>();
        for (Identifier appId : allowedApps) {
            AppInfo application = ApplicationManager.getApplication(appId);
            if (application != null) {
                list.add(application);
            } else {
                OmnixerioDevices.LOGGER.warn("Application {} not found!", appId);
            }
        }
        return list;
    }
}
