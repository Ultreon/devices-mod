package dev.ultreon.devices.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ultreon.devices.block.entity.renderer.PaperRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    @Shadow
    public abstract Level getLevel();

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"))
    private void onRemoveBlockEntity(BlockPos pos, CallbackInfo ci, @Local(name = "removeThis") @Nullable BlockEntity removeThis) {
        if (removeThis != null) {
            if (this.getLevel() instanceof ClientLevel) {
                PaperRenderer.textureCache.invalidate(pos);
            }
        }
    }
}
