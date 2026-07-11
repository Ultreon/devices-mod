package dev.ultreon.devices.api.app;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public interface IIcon {
    Identifier getIconAsset();

    int getIconSize();

    int getGridWidth();

    int getGridHeight();

    /**
     * Width of the source texture in pixels.
     *
     * @return The source width.
     */
    int getSourceWidth();

    /**
     * Height of the source texture in pixels.
     *
     * @return The source height.
     */
    int getSourceHeight();

    int getU();

    int getV();

    int getOrdinal();

    default void draw(GuiGraphicsExtractor graphics, Minecraft mc, int x, int y, int color) {
        int size = getIconSize();
        int assetWidth = getGridWidth() * size;
        int assetHeight = getGridHeight() * size;
        graphics.blit(RenderPipelines.GUI_TEXTURED, getIconAsset(), x, y, size, size, getU(), getV(), size, size, assetWidth, assetHeight);
    }
}
