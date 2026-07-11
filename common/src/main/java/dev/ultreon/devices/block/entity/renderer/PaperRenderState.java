package dev.ultreon.devices.block.entity.renderer;

import dev.ultreon.devices.api.print.IPrint;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class PaperRenderState extends BlockEntityRenderState {
    public @Nullable IPrint print;
    public String name;
    public Identifier id;
    public TextureInfo texture;
    public Direction facing;
}
