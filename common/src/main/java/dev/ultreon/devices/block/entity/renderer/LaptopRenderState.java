package dev.ultreon.devices.block.entity.renderer;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;

public class LaptopRenderState extends BlockEntityRenderState {
    public Direction facing;
    public boolean open;
    public boolean externalDriveAttached;
    public DyeColor externalDriveColor;
    public float screenAngle;
    public ItemStackRenderState item;
    public BlockModelRenderState screen;
}
