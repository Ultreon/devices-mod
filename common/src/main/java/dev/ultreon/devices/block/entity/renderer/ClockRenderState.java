package dev.ultreon.devices.block.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class ClockRenderState extends BlockEntityRenderState {
    public int time;
    public Direction facing;
}
