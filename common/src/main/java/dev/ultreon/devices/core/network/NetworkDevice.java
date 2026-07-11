package dev.ultreon.devices.core.network;

import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.core.Device;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class NetworkDevice extends Device {
    private NetworkDevice() {
        super();
    }

    public NetworkDevice(NetworkDeviceBlockEntity device) {
        super(device);
    }

    public NetworkDevice(@NotNull UUID id, @NotNull String name, @NotNull Router router) {
        super(id, name);
    }

    public boolean isConnected(Level level) {
        if (pos == null) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof NetworkDeviceBlockEntity device) {
            Router router = device.getRouter();
            return router != null && router.getId().equals(this.getId());
        }
        return false;
    }

    @Nullable
    @Override
    public NetworkDeviceBlockEntity getDevice(@NotNull Level level) {
        if (pos == null)
            return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof NetworkDeviceBlockEntity device) {
            return device;
        }
        return null;
    }

    @Override
    public CompoundTag toTag(boolean includePos) {
        CompoundTag tag = super.toTag(includePos);
        if (includePos && pos != null) {
            tag.putLong("pos", pos.asLong());
        }
        return tag;
    }

    public static @Nullable NetworkDevice fromTag(CompoundTag tag) {
        NetworkDevice device = new NetworkDevice();
        Optional<String> id = tag.getString("id");
        if (id.isEmpty()) return null;
        device.id = UUID.fromString(id.get());

        Optional<String> name = tag.getString("name");
        if (name.isEmpty()) return null;
        device.name = name.get();

        if (tag.contains("pos")) {
            Optional<Long> pos1 = tag.getLong("pos");
            if (pos1.isEmpty()) return null;
            device.pos = BlockPos.of(pos1.get());
        }
        return device;
    }

    public static NetworkDevice load(ValueInput in) {
        NetworkDevice device = new NetworkDevice();
        Optional<String> id = in.getString("id");
        if (id.isEmpty()) return null;
        device.id = UUID.fromString(id.get());

        Optional<String> name = in.getString("name");
        if (name.isEmpty()) return null;
        device.name = name.get();

        Optional<Long> pos1 = in.getLong("pos");
        if (pos1.isEmpty()) return device;
        device.pos = BlockPos.of(pos1.get());
        return device;
    }
}
