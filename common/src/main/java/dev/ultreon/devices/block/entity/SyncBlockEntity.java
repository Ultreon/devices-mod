package dev.ultreon.devices.block.entity;

import com.mojang.logging.LogUtils;
import dev.architectury.injectables.annotations.PlatformOnly;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.annotations.PlatformOverride;
import dev.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;

public abstract class SyncBlockEntity extends BlockEntity {
    protected CompoundTag pipeline = new CompoundTag();
    private static final Logger LOGGER = LogUtils.getLogger();

    public SyncBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    public void sync() {
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    // from SignBlockEntity
    protected void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @PlatformOnly("forge")
    @PlatformOverride("forge")
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER);
        if (level != null) {
            this.loadAdditional(TagValueInput.create(scopedCollector, level.registryAccess(), Objects.requireNonNull(pkt.getTag())));
        } else {
            OmnixerioDevices.LOGGER.error("Level is null during data packet load for block entity " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType()));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        if (!pipeline.isEmpty()) {
            CompoundTag updateTag = pipeline;
            pipeline = new CompoundTag();
            return updateTag;
        }
        return saveSyncTag();
    }

    public abstract CompoundTag saveSyncTag();

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    public CompoundTag getPipeline() {
        return pipeline;
    }
}
