package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * @author MrCrayfish
 */
public class PaperBlockEntity extends SyncBlockEntity {
    private IPrint print;
    private byte rotation;

    public PaperBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.PAPER.get(), pWorldPosition, pBlockState);
    }

    public void nextRotation() {
        rotation++;
        if (rotation > 7) {
            rotation = 0;
        }
        pipeline.putByte("rotation", rotation);
        sync();
        playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM);
    }

    public float getRotation() {
        return rotation * 45f;
    }

    @Nullable
    public IPrint getPrint() {
        return print;
    }

    @Override
    public void loadAdditional(@NonNull ValueInput tag) {
        super.loadAdditional(tag);
        Optional<ValueInput> optionalPrint = tag.child("print");
        optionalPrint.ifPresent(print -> this.print = IPrint.readInput(print));
        this.rotation = tag.getByteOr("rotation", (byte) 0);
    }

    @Override
    public void saveAdditional(@NonNull ValueOutput tag) {
        super.saveAdditional(tag);
        if (print != null) {
            IPrint.store(print, tag.child("print"));
        }
        tag.putByte("rotation", rotation);
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = new CompoundTag();
        if (print != null) {
            CompoundTag printTag = IPrint.save(print);
            tag.put("print", printTag);
        }
        tag.putByte("rotation", rotation);
        return tag;
    }

    private void playSound(SoundEvent sound) {
        Level lvl = level;
        if (lvl != null) {
            lvl.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1f, 1f);
        }
    }
}
