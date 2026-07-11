package dev.ultreon.devices.block.entity.renderer;

import dev.ultreon.devices.block.entity.OfficeChairBlockEntity;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class PrinterRenderState extends BlockEntityRenderState {
    public boolean hasPaper;
    public boolean isLoading;
    public boolean isPrinting;
    public Direction facing;
    public int remainingPrintTime;
    public int totalPrintTime;
    public PaperRenderState paperState;
    public int paperCount;
}
