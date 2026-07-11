package dev.ultreon.devices.core;

import dev.ultreon.devices.MoreCodecs;
import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Device {
    protected UUID id;
    protected String name;
    protected BlockPos pos;

    protected Device() {

    }

    public Device(@NotNull DeviceBlockEntity device) {
        this.id = device.getId();
        update(device);
    }

    public Device(@NotNull UUID id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public BlockPos getPos() {
        return pos;
    }

    public void setPos(@Nullable BlockPos pos) {
        this.pos = pos;
    }

    public void update(@NotNull DeviceBlockEntity device) {
        name = device.getCustomName();
        pos = device.getBlockPos();
    }

    @Nullable
    public DeviceBlockEntity getDevice(@NotNull Level level) {
        if (pos == null)
            return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DeviceBlockEntity deviceBlockEntity) {
            if (deviceBlockEntity.getId().equals(getId())) {
                return deviceBlockEntity;
            }
        }

        return null;
    }

    public CompoundTag toTag(boolean includePos) {
        CompoundTag tag = new CompoundTag();
        tag.store("id", MoreCodecs.UUID, id);
        tag.putString("name", getName());
        if (includePos) {
            tag.store("pos", BlockPos.CODEC, pos);
        }
        return tag;
    }

    public void save(ValueOutput out) {
        save(out, true);
    }

    public void save(ValueOutput out, boolean includePos) {
        out.store("id", MoreCodecs.UUID, id);
        out.putString("name", getName());
        if (includePos) {
            out.store("pos", BlockPos.CODEC, pos);
        }
    }

    public static Device fromTag(CompoundTag tag) {
        Device device = new Device();
        Optional<UUID> optionalId = tag.read("id", MoreCodecs.UUID);
        Optional<String> optionalName = tag.getString("name");
        Optional<BlockPos> optionalPos = tag.read("pos", BlockPos.CODEC);

        if (optionalId.isEmpty() || optionalName.isEmpty()) return null;

        UUID id = optionalId.get();
        String name = optionalName.get();

        device.id = id;
        device.name = name;

        optionalPos.ifPresent(blockPos -> device.pos = blockPos);

        return device;
    }

    public static Device load(ValueInput in) {
        Device device = new Device();
        Optional<UUID> optionalId = in.read("id", MoreCodecs.UUID);
        Optional<String> optionalName = in.getString("name");
        Optional<BlockPos> optionalPos = in.read("pos", BlockPos.CODEC);

        if (optionalId.isEmpty() || optionalName.isEmpty()) return null;

        UUID id = optionalId.get();
        String name = optionalName.get();

        device.id = id;
        device.name = name;

        optionalPos.ifPresent(tag -> device.pos = optionalPos.get());

        return device;
    }
}
