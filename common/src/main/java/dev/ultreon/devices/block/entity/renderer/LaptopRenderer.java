package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import dev.ultreon.devices.init.ModItems;
import dev.ultreon.devices.item.FlashDriveItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity, LaptopRenderState> {
    private final BlockEntityRendererProvider.Context context;
    private final ItemModelResolver itemModelResolver;

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.itemModelResolver = this.context.itemModelResolver();
    }

    @Override
    public @NonNull LaptopRenderState createRenderState() {
        return new LaptopRenderState();
    }

    @Override
    public void extractRenderState(@NonNull LaptopBlockEntity blockEntity, @NonNull LaptopRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.facing = blockEntity.getBlockState().getValue(LaptopBlock.FACING);
        state.open = blockEntity.getBlockState().getValue(LaptopBlock.OPEN);
        state.externalDriveAttached = blockEntity.isExternalDriveAttached();
        state.externalDriveColor = blockEntity.getExternalDriveColor();
        state.screenAngle = blockEntity.getScreenAngle(partialTicks);

        int seed = (int)blockEntity.getBlockPos().asLong();

        state.item = new ItemStackRenderState();
        if (state.externalDriveAttached) {
            if (state.externalDriveColor == null) {
                state.externalDriveColor = DyeColor.WHITE;
            }
            FlashDriveItem flashDriveByColor = ModItems.getFlashDriveByColor(state.externalDriveColor);
            if (flashDriveByColor != null) {
                this.itemModelResolver.updateForTopItem(state.item, new ItemStack(flashDriveByColor), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed);
            }
        }
        state.screen = new BlockModelRenderState();
        context.blockModelResolver().update(state.screen, blockEntity.getBlock().defaultBlockState().setValue(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN), BlockDisplayContext.create());
    }

    @Override
    public void submit(@NonNull LaptopRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {

        poseStack.pushPose();
        {
            if (state.externalDriveAttached) {
                poseStack.pushPose();
                {
                    poseStack.translate(0.5, 0, 0.5);
                    poseStack.mulPose(state.facing.getRotation());
                    poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(-90)));
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-90)));
                    poseStack.translate(-0.5, 0, -0.5);
                    poseStack.translate(0.595, -0.2075, -0.005);

                    if (!state.item.isEmpty()) {
                        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, NO_OVERLAY, 0x00000000);
                    }
                }
                poseStack.popPose();
            }

            poseStack.pushPose();
            {
                var direction = state.facing.getClockWise().toYRot();
                poseStack.translate(0.5, 0, 0.5);//west/east +90 north/south -90
                poseStack.mulPose(Axis.YP.rotationDegrees(state.facing == Direction.EAST || state.facing == Direction.WEST ? direction + 90 : direction - 90));
                poseStack.translate(-0.5, 0, -0.5);
                poseStack.translate(0, 0.0625, 0.25);
                poseStack.mulPose(Axis.XP.rotationDegrees(state.screenAngle + 180));
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                state.screen.submit(poseStack, submitNodeCollector, state.lightCoords, NO_OVERLAY, 0x00000000);
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}