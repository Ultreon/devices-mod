package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.ultreon.devices.block.OfficeChairBlock;
import dev.ultreon.devices.block.entity.OfficeChairBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class OfficeChairRenderer implements BlockEntityRenderer<OfficeChairBlockEntity, OfficeChairRenderState> {
    private final BlockEntityRendererProvider.Context context;

    public OfficeChairRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public @NonNull OfficeChairRenderState createRenderState() {
        return new OfficeChairRenderState();
    }

    @Override
    public void extractRenderState(@NonNull OfficeChairBlockEntity blockEntity, @NonNull OfficeChairRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.seat = new BlockModelRenderState();
        state.rotation = blockEntity.getRotation();
        context.blockModelResolver().update(state.seat, blockEntity.getBlockState().getBlock().defaultBlockState().setValue(OfficeChairBlock.TYPE, OfficeChairBlock.Type.SEAT), BlockDisplayContext.create());
    }

    @Override
    public void submit(OfficeChairRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {


        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.rotation + 180));
            poseStack.translate(-0.5, 0, -0.5);
            state.seat.submit(poseStack, submitNodeCollector, state.lightCoords, NO_OVERLAY, 0x00000000);
        }
        poseStack.popPose();
    }
}