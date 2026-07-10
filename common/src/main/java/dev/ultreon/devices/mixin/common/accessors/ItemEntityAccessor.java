package dev.ultreon.devices.mixin.common.accessors;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
    @Mutable
    @Accessor("bobOffs")
    void setBobOffs(float bobOffs);

    @Accessor("bobOffs")
    float getBobOffs();
}
