package dev.ultreon.devices.programs.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TaskUpdateApplicationData extends Task {
    private int x, y, z;
    private String appId;
    private CompoundTag data;

    public TaskUpdateApplicationData() {
        super("update_application_data");
    }

    public TaskUpdateApplicationData(int x, int y, int z, @NotNull String appId, @NotNull CompoundTag data) {
        this();
        this.x = x;
        this.y = y;
        this.z = z;
        this.appId = appId;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.store("Pos", BlockPos.CODEC, new BlockPos(this.x, this.y, this.z));
        tag.putString("appId", this.appId);
        tag.put("appData", this.data);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        Optional<BlockPos> pos = tag.read("Pos", BlockPos.CODEC);
        Optional<String> appId = tag.getString("appId");
        Optional<CompoundTag> appData = tag.read("appData", CompoundTag.CODEC);
        if (pos.isEmpty() || appId.isEmpty() || appData.isEmpty()) return;
        BlockEntity tileEntity = level.getBlockEntity(pos.get());
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            laptop.setApplicationData(appId.get(), appData.get());
        }
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
