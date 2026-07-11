package dev.ultreon.devices.block.entity.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.block.entity.PaperBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

/**
 * @author MrCrayfish
 */
public record PaperRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PaperBlockEntity, PaperRenderState> {

    public static final Minecraft instance = Minecraft.getInstance();

    public static Cache<BlockPos, TextureInfo> textureCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.of(10, ChronoUnit.SECONDS))
            .<BlockPos, TextureInfo>removalListener(notification -> {
                TextureInfo value = notification.getValue();
                if (value != null) {
                    instance.submit(() -> {
                        Minecraft.getInstance().getTextureManager().release(value.id());
                    });
                }
            })
            .build();


    @Override
    public PaperRenderState createRenderState() {
        return new PaperRenderState();
    }

    @Override
    public void extractRenderState(PaperBlockEntity blockEntity, PaperRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.print = blockEntity.getPrint();
        IPrint print = state.print;
        TextureInfo ifPresent = textureCache.getIfPresent(blockEntity.getBlockPos());
        if (ifPresent != null) {
            state.texture = ifPresent;
        } else if (print != null) {
            int[] pixels = print.getPixels();
            NativeImage image = new NativeImage(print.getResolution(), print.getResolution(), true);
            for (int i = 0; i < pixels.length; i++) {
                int r = (pixels[i] >> 16 & 255);
                int g = (pixels[i] >> 8 & 255);
                int b = (pixels[i] & 255);
                int a = (int) (double) (pixels[i] >> 24 & 255);
                image.setPixelABGR(i % print.getResolution(), i / print.getResolution(), new Color(r, g, b, a).getRGB());
            }
            state.name = "paper/" + UUID.randomUUID().toString().replace("-", "");
            DynamicTexture texture = new DynamicTexture(() -> state.name, image);
            state.id = OmnixerioDevices.id(state.name);
            Minecraft.getInstance().getTextureManager().register(state.id, texture);
            textureCache.put(blockEntity.getBlockPos(), new TextureInfo(state.id, texture));
        }
    }

    @Override
    public void submit(PaperRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        Vector3f vector3f = new Vector3f(0.5f, 0f, 0.5f);
        Quaternionf quat = (switch (state.facing) {
            case DOWN -> new Quaternionf().rotationX((float) Math.PI);
            case UP -> new Quaternionf();
            case NORTH -> new Quaternionf().rotateXYZ(0, 0.0F, 0);
            case SOUTH -> new Quaternionf().rotateXYZ(0, (float) (Math.PI), 0);
            case WEST -> new Quaternionf().rotateXYZ(0, (float) (Math.PI / 2), 0);
            case EAST -> new Quaternionf().rotateXYZ(0, (float) -(Math.PI / 2), 0);
        });
        vector3f.set(-1, -1, -1).rotate(quat);
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(quat);
        poseStack.translate(-0.5, -0.5, -0.5);

        float scale = 32768f;
        poseStack.scale(1 / scale, 1 / scale, 1 / scale);

        poseStack.translate(0, 0, 0.001);

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(state.id), (pose, buffer) -> {
            Matrix4f bufferPose = pose.pose();
            int packedLight = state.lightCoords;
            int packedOverlay = NO_OVERLAY;
            buffer.addVertex(bufferPose, 0.0f, 128.0f, -0.01f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setLight(packedLight).setOverlay(packedOverlay);
            buffer.addVertex(bufferPose, 128.0f, 128.0f, -0.01f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setLight(packedLight).setOverlay(packedOverlay);
            buffer.addVertex(bufferPose, 128.0f, 0.0f, -0.01f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setLight(packedLight).setOverlay(packedOverlay);
            buffer.addVertex(bufferPose, 0.0f, 0.0f, -0.01f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setLight(packedLight).setOverlay(packedOverlay);
        });

        poseStack.popPose();
    }
}
