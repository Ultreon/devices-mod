package dev.ultreon.devices.programs.gitweb.component.container;

import com.mojang.blaze3d.platform.Lighting;
import dev.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;

public class LoomBox extends ContainerBox {
    public static final int HEIGHT = 84;
    private final ItemStack result;
    private final BannerFlagModel flag;
    private final BannerPatternLayers bannerPatternLayers;

    public LoomBox(ItemStack banner, ItemStack dye, ItemStack pattern, ItemStack result) {
        super(0, 0, 128, 72, HEIGHT, new ItemStack(Blocks.LOOM), "Loom");
        this.result = result;
        slots.add(new Slot(13, 26, banner));
        slots.add(new Slot(33, 26, dye));
        slots.add(new Slot(23, 45, pattern));
        slots.add(new Slot(94, 58, this.result));

        if (!result.isEmpty()) {
            bannerPatternLayers = result.get(DataComponents.BANNER_PATTERNS);
        } else {
            bannerPatternLayers = new BannerPatternLayers(List.of());
        }
        ModelPart modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.STANDING_BANNER_FLAG);
        this.flag = new BannerFlagModel(modelPart);
    }

    @Override
    protected void extractRenderState(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.extractRenderState(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        int topPos = y + 12;
        if (result.isEmpty()) return;
        DyeColor baseColor = ((BannerItem) result.getItem()).getColor();
        graphics.bannerPattern(this.flag, baseColor, this.bannerPatternLayers, x + 70, topPos + 20, x + 90, topPos + 60);
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);
    }
}
