package dev.ultreon.devices.block.entity.renderer;


import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.block.PrinterBlock;
import dev.ultreon.devices.block.entity.PrinterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.UUID;

import static dev.ultreon.devices.block.entity.renderer.PaperRenderer.textureCache;
import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

/**
 * @author MrCrayfish
 */
public record PrinterRenderer(BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PrinterBlockEntity, PrinterRenderState> {
    private static final Quaternionf tmpQ = new Quaternionf();
    public static final float DEG2RAD = 0.017453292519943295f;
    public static final double PIXEL_SIZE = 0.015625;

    private static void submitPaper(@UnknownNullability PrinterRenderState state, PoseStack pose, SubmitNodeCollector submitNodeCollector, @UnknownNullability CameraRenderState cameraRenderState, PaperModel paperModel) {
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(state.facing.getRotation());
        pose.mulPose(new Quaternionf().rotateX(67.5f * DEG2RAD));
        pose.translate(0, 0, 0.4);
        pose.translate(-13 * PIXEL_SIZE, -13 * PIXEL_SIZE, -1 * PIXEL_SIZE);
        pose.scale(0.3f, 0.3f, 0.3f);

        drawBuffer(state, pose, submitNodeCollector, paperModel);
    }

    private static void submitPrint(PrinterRenderState state, PoseStack pose, SubmitNodeCollector submitNodeCollector, PaperModel paperModel) {
        if (state.isLoading) {
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(state.facing.getRotation());
            pose.mulPose(tmpQ.identity().rotateX(67.5f * DEG2RAD));

            double progress = Math.max(-0.4, -0.4 + (0.4 * ((double) (state.remainingPrintTime - 10) / 20)));
            pose.translate(0, -progress, 0.4);
            pose.translate(-13 * PIXEL_SIZE, -13 * PIXEL_SIZE, -1 * PIXEL_SIZE);
            pose.scale(0.3f, 0.3f, 0.3f);

            drawBuffer(state, pose, submitNodeCollector, paperModel);
        } else if (state.isPrinting) {
            pose.translate(0.5, 0.078125, 0.5);
            pose.mulPose(state.facing.getRotation());

            double progress = -0.35 + (((double) (state.remainingPrintTime - 20) / state.totalPrintTime));
            pose.translate(0, -progress, 0);
            pose.translate(-13 * PIXEL_SIZE, -13 * PIXEL_SIZE, -0.5 * PIXEL_SIZE);
            pose.scale(0.3f, 0.3f, 0.3f);

            drawBuffer(state, pose, submitNodeCollector, paperModel);

            pose.translate(0.4, 0.085, -0.001);
            pose.mulPose(tmpQ.identity().rotateY(180f * DEG2RAD));

            //region <RenderPrint()>
            IPrint print = state.paperState.print;
            if (print != null) {
                pose.pushPose();
                pose.translate(-15 * 0.0625, 15 * 0.03125, 0);
                pose.scale(1 / 16384f, 1 / 16384f, 1 / 16384f);
                pose.scale(1 / 1.5f, 1 / 1.5f, 1 / 1.5f);

                IPrint.Renderer renderer = PrintingManager.getRenderer(print);
//                VertexConsumer buffer = bufferSource.getBuffer(paperModel.renderType(PaperModel.TEXTURE));
//                renderer.render(pose, print.toTag(), packedLight, packedOverlay, blockEntity.getBlockState().facing);
//                pose.popPose();
            }
            //endregion
        }
    }

    private static void drawBuffer(PrinterRenderState state, @NotNull PoseStack pose, SubmitNodeCollector submitNodeCollector, PaperModel paperModel) {
        submitNodeCollector.submitModel(paperModel, state.paperState, pose, PaperModel.TEXTURE, state.lightCoords, NO_OVERLAY, 0X000000, null);
    }

    private static void submitDisplay(PrinterRenderState state, @NotNull PoseStack pose, @NotNull SubmitNodeCollector submitNodeCollector) {
        // region <Prepare()>
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(state.facing.getRotation());
        pose.mulPose(tmpQ.identity().rotateY(180f * DEG2RAD));
        pose.translate(0.0675, 0.005, -0.032);
        pose.translate((8 -5.85) * 0.0625, (8 -5) * 0.0625, (-4.25) * 0.0625);

        pose.scale(-0.010416667f, -0.010416667f, -0.010416667f);
        pose.mulPose(tmpQ.identity().rotateX((90 + 22.5f) * DEG2RAD));
        // endregion

        FormattedCharSequence visualOrderText = Component.literal(Integer.toString(state.paperCount)).getVisualOrderText();
        submitNodeCollector.submitText(pose, -Minecraft.getInstance().font.width(Integer.toString(state.paperCount)), -Minecraft.getInstance().font.lineHeight, visualOrderText, false, Font.DisplayMode.NORMAL, 0x0f000f0, 0xffffffff, 0x000000, 0x000000);
        pose.popPose();
    }

    @Override
    public PrinterRenderState createRenderState() {
        return new PrinterRenderState();   
    }

    @Override
    public void extractRenderState(PrinterBlockEntity blockEntity, PrinterRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        
        state.hasPaper = blockEntity.hasPaper();
        state.paperCount = blockEntity.getPaperCount();
        state.isLoading = blockEntity.isLoading();
        state.isPrinting = blockEntity.isPrinting();
        state.totalPrintTime = blockEntity.getTotalPrintTime();
        state.remainingPrintTime = blockEntity.getRemainingPrintTime();
        state.paperState = new PaperRenderState();
        state.facing = blockEntity.getBlockState().getValue(PrinterBlock.FACING);

        PaperRenderState paperState = state.paperState;
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        paperState.print = blockEntity.getPrint();
        IPrint print = paperState.print;
        TextureInfo ifPresent = textureCache.getIfPresent(blockEntity.getBlockPos());
        if (ifPresent != null) {
            paperState.texture = ifPresent;
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
            paperState.name = "paper/" + UUID.randomUUID().toString().replace("-", "");
            DynamicTexture texture = new DynamicTexture(() -> paperState.name, image);
            paperState.id = OmnixerioDevices.id(paperState.name);
            Minecraft.getInstance().getTextureManager().register(paperState.id, texture);
            textureCache.put(blockEntity.getBlockPos(), new TextureInfo(paperState.id, texture));
        }
    }

    @Override
    public void submit(PrinterRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        PaperModel paperModel = new PaperModel(Minecraft.getInstance().getEntityModels().bakeLayer(PaperModel.LAYER_LOCATION));

        poseStack.pushPose();

        // region <RenderMain()>
        if (state.hasPaper) {
            poseStack.pushPose();
            submitPaper(state, poseStack, submitNodeCollector, camera, paperModel);
            poseStack.popPose();
        }

        poseStack.pushPose();
        submitPrint(state, poseStack, submitNodeCollector, paperModel);
        poseStack.popPose();

        poseStack.pushPose();
        submitDisplay(state, poseStack, submitNodeCollector);
        poseStack.popPose();
        // endregion

        poseStack.popPose();
    }

    public static class PaperModel extends Model<PaperRenderState> {
        public static final Identifier TEXTURE = OmnixerioDevices.id("textures/block/paper.png");
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(OmnixerioDevices.id("paper_model"), "main");
        private final ModelPart root;
        private final ModelPart main;

        public PaperModel(ModelPart pRoot) {
            super(pRoot, RenderTypes::entitySolid);
            this.root = pRoot;
            this.main = pRoot.getChild("main");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 22, 30, 1), PartPose.offset(0f, 0f, 0f));
            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        public ModelPart getMain() {
            return main;
        }
    }
}
