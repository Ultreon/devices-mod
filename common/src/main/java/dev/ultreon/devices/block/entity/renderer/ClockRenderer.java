package dev.ultreon.devices.block.entity.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.block.ClockBlock;
import dev.ultreon.devices.block.entity.ClockBlockEntity;
import dev.ultreon.devices.core.TaskBar;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * @author MrCrayfish
 */
public record ClockRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<ClockBlockEntity, ClockRenderState> {
    private static final Quaternionf tmpQ = new Quaternionf();
    public static final float DEG2RAD = 0.017453292519943295f;

    @Override
    public @NonNull ClockRenderState createRenderState() {
        return new ClockRenderState();
    }

    @Override
    public void extractRenderState(@NonNull ClockBlockEntity blockEntity, @NonNull ClockRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        Level level = blockEntity.getLevel();
        state.time = Math.toIntExact(level != null ? level.getOverworldClockTime() % 24000 : 0);
        state.facing = blockEntity.getBlockState().getValue(ClockBlock.FACING);
    }

    @Override
    public void submit(ClockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(state.facing.getRotation());
            poseStack.mulPose(tmpQ.identity().rotateY(180f * DEG2RAD));
            poseStack.translate(0, 0.232, -0.375);

            poseStack.pushPose();
            {
                poseStack.scale(-0.02f, -0.02f, -0.02f);
                poseStack.mulPose(tmpQ.identity().rotateX((90) * DEG2RAD));

                int dayTime = state.time;
                String string = TaskBar.timeToString(dayTime);
                FormattedCharSequence visualOrderText = Component.literal(string).getVisualOrderText();
                submitNodeCollector.submitText(poseStack, -Minecraft.getInstance().font.width(string) / 2f, -Minecraft.getInstance().font.lineHeight, visualOrderText, false, Font.DisplayMode.NORMAL, 0x0f000f0, 0xffffffff, 0x00000000, 0x000000);
            }
            poseStack.popPose();
        }
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
