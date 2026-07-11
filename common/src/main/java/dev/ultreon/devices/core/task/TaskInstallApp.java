package dev.ultreon.devices.core.task;

import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.object.AppInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author MrCrayfish
 */
public class TaskInstallApp extends Task {
    private String appId;
    private BlockPos laptopPos;
    private boolean install;

    public TaskInstallApp() {
        super("install_app");
    }

    public TaskInstallApp(AppInfo info, BlockPos laptopPos, boolean install) {
        this();
        this.appId = info.getFormattedId();
        this.laptopPos = laptopPos;
        this.install = install;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putString("appId", appId);
        tag.putLong("pos", laptopPos.asLong());
        tag.putBoolean("install", install);
        DebugLog.log("Prep message " + appId + ", " + laptopPos.toString() + ", " + install);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        DebugLog.log("Proc message " + tag.getString("appId") + ", " +  BlockPos.of(tag.getLong("pos").orElseThrow()) + ", " + tag.getBoolean("install"));
        String appId = tag.getString("appId").orElseThrow();
        DebugLog.log(level.getBlockState(BlockPos.of(tag.getLong("pos").orElseThrow())).getBlock());
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos").orElseThrow())).getBlockEntity(BlockPos.of(tag.getLong("pos").orElseThrow()), LevelChunk.EntityCreationType.IMMEDIATE);
        DebugLog.log(tileEntity);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            CompoundTag systemData = laptop.getSystemData();
            ListTag list = systemData.getList("InstalledApps").orElseThrow();

            if (tag.getBoolean("install").orElseThrow()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.getString(i).equals(appId)) {
                        OmnixerioDevices.LOGGER.warn("Found duplicate, noping out");
                        return;
                    }
                }
                list.add(StringTag.valueOf(appId));
                this.setSuccessful();
            } else {
                list.removeIf(appTag -> {
                    if (appTag.asString().orElseThrow().equals(appId)) {
                        this.setSuccessful();
                        return true;
                    } else {
                        return false;
                    }
                });
            }
            systemData.put("InstalledApps", list);
        }
        if (!this.isSucessful()) {
            OmnixerioDevices.LOGGER.info("Installing {} unsuccessful", appId);
        }
    }


    @Override
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
