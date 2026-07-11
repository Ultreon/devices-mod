package dev.ultreon.devices.core.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class Router {
    private final Map<UUID, NetworkDevice> networkDevices = new HashMap<>();

    private int timer;
    private UUID routerId;
    private BlockPos pos;

    public Router(BlockPos pos) {
        this.pos = pos;
    }

    public void tick(Level level) {
        if (++timer >= DeviceConfig.BEACON_INTERVAL.get()) {
            sendBeacon(level);
            timer = 0;
        }
    }

    public boolean addDevice(UUID id, String name) {
        if (networkDevices.size() >= DeviceConfig.MAX_DEVICES.get()) {
            return networkDevices.containsKey(id);
        }
        if (!networkDevices.containsKey(id)) {
            networkDevices.put(id, new NetworkDevice(id, name, this));
        }
        timer = DeviceConfig.BEACON_INTERVAL.get();
        return true;
    }

    public boolean addDevice(NetworkDeviceBlockEntity device) {
        if (networkDevices.size() >= DeviceConfig.MAX_DEVICES.get()) {
            return networkDevices.containsKey(device.getId());
        }
        if (!networkDevices.containsKey(device.getId())) {
            networkDevices.put(device.getId(), new NetworkDevice(device));
        }
        return true;
    }

    public boolean isDeviceRegistered(NetworkDeviceBlockEntity device) {
        return networkDevices.containsKey(device.getId());
    }

    public boolean isDeviceConnected(NetworkDeviceBlockEntity device) {
        return isDeviceRegistered(device) && networkDevices.get(device.getId()).getPos() != null;
    }

    public void removeDevice(NetworkDeviceBlockEntity device) {
        networkDevices.remove(device.getId());
    }

    @Nullable
    public NetworkDeviceBlockEntity getDevice(Level level, UUID id) {
        return networkDevices.containsKey(id) ? networkDevices.get(id).getDevice(level) : null;
    }

    public Collection<NetworkDevice> getNetworkDevices() {
        return networkDevices.values();
    }

    public Collection<NetworkDevice> getConnectedDevices(Level level) {
        sendBeacon(level);
        return networkDevices.values().stream().filter(device -> device.getPos() != null).toList();
    }

    public Collection<NetworkDevice> getConnectedDevices(final Level level, BlockEntityType<?> targetType) {
        final Predicate<NetworkDevice> deviceType = networkDevice -> {
            if (networkDevice.getPos() == null)
                return false;

            BlockEntity blockEntity = level.getBlockEntity(networkDevice.getPos());
            return blockEntity instanceof NetworkDeviceBlockEntity device && targetType.equals(device.getType());

        };
        return getConnectedDevices(level).stream().filter(deviceType).toList();
    }

    public Collection<NetworkDevice> getConnectedDevices(final Level level, TagKey<BlockEntityType<?>> targetType) {
        final Predicate<NetworkDevice> deviceType = networkDevice -> {
            if (networkDevice.getPos() == null)
                return false;

            BlockEntity blockEntity = level.getBlockEntity(networkDevice.getPos());

            if (blockEntity instanceof NetworkDeviceBlockEntity device) {
                return BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(blockEntity.getType()).is(targetType);
            }
            return false;
        };
        return getConnectedDevices(level).stream().filter(deviceType).toList();
    }

    private void sendBeacon(Level level) {
        if (level.isClientSide())
            return;

        networkDevices.forEach((uuid, device) -> device.setPos(null));
        int range = DeviceConfig.SIGNAL_RANGE.get();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockEntity blockEntity = level.getBlockEntity(currentPos);
                    if (blockEntity instanceof NetworkDeviceBlockEntity device) {
                        if (!networkDevices.containsKey(device.getId()))
                            continue;
                        if (device.receiveBeacon(this)) {
                            networkDevices.get(device.getId()).setPos(currentPos);
                        }
                    }
                }
            }
        }
    }

    public UUID getId() {
        if (routerId == null) {
            routerId = UUID.randomUUID();
        }
        return routerId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public CompoundTag toTag(boolean includePos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getId().toString());

        ListTag deviceList = new ListTag();
        networkDevices.forEach((id, device) -> {
            deviceList.add(device.toTag(includePos));
        });
        tag.put("network_devices", deviceList);

        return tag;
    }

    public static Router load(BlockPos pos, ValueInput in) {
        Router router = new Router(pos);
        Optional<String> string = in.getString("id");
        if (string.isEmpty()) return null;
        router.routerId = UUID.fromString(string.get());
        Optional<ValueInput.ValueInputList> optionalDevices = in.childrenList("network_devices");
        if (optionalDevices.isEmpty()) return router;
        ValueInput.ValueInputList devices = optionalDevices.get();
        for (ValueInput device : devices) {
            NetworkDevice device1 = NetworkDevice.load(device);
            if (device1 != null) {
                router.networkDevices.put(device1.getId(), device1);
            }
        }

        return router;
    }

    public static Router fromTag(BlockPos pos, CompoundTag tag) {
        Router router = new Router(pos);
        Optional<String> string = tag.getString("id");
        if (string.isEmpty()) return null;
        router.routerId = UUID.fromString(string.get());

        Optional<ListTag> deviceList = tag.getList("network_devices");
        if (deviceList.isEmpty()) return router;
        for (Tag tag1 : deviceList.get()) {
            Optional<CompoundTag> compound = tag1.asCompound();
            if (compound.isEmpty()) continue;
            NetworkDevice device = NetworkDevice.fromTag(compound.get());
            if (device == null) continue;
            router.networkDevices.put(device.getId(), device);
        }
        return router;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Router router))
            return false;
        return router.getId().equals(getId());
    }
}
