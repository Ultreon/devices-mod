package dev.ultreon.devices.programs.gitweb.module;

import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;
import java.util.Map;

import static dev.ultreon.devices.programs.gitweb.module.ContainerModule.getItem;

public class BannerIIModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"banner"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"waving"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return LoomBox.HEIGHT;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        layout.addComponent(createContainer(data));
    }

    public LoomBox createContainer(Map<String, String> data) {
        return new LoomBox(getItem(data, "banner"), Boolean.parseBoolean(data.get("waving")));
    }

    public static class LoomBox extends Component {
        public static final int HEIGHT = 84;
        private final ItemStack banner;
        private final boolean waving;
        private final BannerFlagModel flagModel;
        private final ModelPart flagPart;
        private final BannerPatternLayers bannerPatternLayers;

        public LoomBox(ItemStack banner, boolean waving) {
            super(0, 0);
            this.banner = banner;
            this.waving = waving;
            ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.STANDING_BANNER_FLAG);
            this.flagPart = root.getChild("flag");
            this.flagModel = new BannerFlagModel(root);

            if (!banner.isEmpty())
                this.bannerPatternLayers = banner.get(DataComponents.BANNER_PATTERNS);
            else
                this.bannerPatternLayers = new BannerPatternLayers(List.of());
        }

        @Override
        protected void extractRenderState(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            super.extractRenderState(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
            if (banner.isEmpty()) return;

            if (waving) {
                long l = System.currentTimeMillis() / 50;
                float h = ((float) Math.floorMod(l, 100L) + partialTicks) / 100.0f;
                this.flagPart.yRot = (float) Math.toRadians(30);
                this.flagPart.xRot = (-0.0125f + 0.01f * Mth.cos((float) Math.PI * 2 * h)) * (float) Math.PI;
            } else {
                this.flagPart.yRot = (float) Math.toRadians(30);
                this.flagPart.xRot = 0.0f;
            }

            DyeColor baseColor = ((BannerItem) banner.getItem()).getColor();
            graphics.bannerPattern(this.flagModel, baseColor, this.bannerPatternLayers, x + 130, y + 55, x + 150, y + 95);
            Minecraft.getInstance().gameRenderer.lighting().setupFor(com.mojang.blaze3d.platform.Lighting.Entry.ITEMS_3D);
        }
    }
}
