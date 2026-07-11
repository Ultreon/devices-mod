package dev.ultreon.devices.api.utils;

import com.mojang.blaze3d.vertex.*;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.object.AppInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@SuppressWarnings("unused")
public class RenderUtil {
    public static void renderItem(GuiGraphicsExtractor graphics, int x, int y, ItemStack stack, boolean overlay) {
        graphics.item(stack, x, y);
    }

    public static void drawIcon(GuiGraphicsExtractor graphics, double x, double y, AppInfo info, int width, int height) {
        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, sourceWidth, sourceHeight, (int) textureWidth, (int) textureHeight);
        if (info == null || (info.getIcon().getBase().getU() == -1 && info.getIcon().getBase().getV() == -1)) {
            drawRectWithTexture(Laptop.ICON_TEXTURES, graphics, x, y, 0, 0, width, height, 14, 14, 224, 224);
            return;
        }
        var glyphs = new AppInfo.Icon.Glyph[]{info.getIcon().getBase(), info.getIcon().getOverlay0(), info.getIcon().getOverlay1()};
        for (AppInfo.Icon.Glyph glyph : glyphs) {
            if (glyph.getU() == -1 || glyph.getV() == -1) continue;
            var col = new Color(info.getTint(glyph.getType()));
            drawRectWithTexture(Laptop.ICON_TEXTURES, graphics, x, y, glyph.getU(), glyph.getV(), width, height, 14, 14, 224, 224, 0xff000000 | col.getRGB());
        }
    }

    public static void drawRectWithTexture3(Identifier location, GuiGraphicsExtractor graphics, double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        drawRectWithTexture(location, graphics, x, y, 0, u, v, width, height, (int) textureWidth, (int) textureHeight);
    }

    public static void drawRectWithTexture3(Identifier location, GuiGraphicsExtractor graphics, double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight, int color) {
        drawRectWithTexture(location, graphics, x, y, 0, u, v, width, height, (int) textureWidth, (int) textureHeight, color);
    }

    /**
     * Texture size must be 256x256
     *
     * @param graphics      gui graphics helper
     * @param x             the x position of the rectangle
     * @param y             the y position of the rectangle
     * @param z             the z position of the rectangle
     * @param u             the x position of the texture
     * @param v             the y position of the texture
     * @param width         the width of the rectangle
     * @param height        the height of the rectangle
     * @param textureWidth  the width of the texture
     * @param textureHeight the height of the texture
     */
    public static void drawRectWithTexture(Identifier location, GuiGraphicsExtractor graphics, double x, double y, double z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, (int) x, (int) y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawRectWithTexture(Identifier location, GuiGraphicsExtractor graphics, double x, double y, double z, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, (int) x, (int) y, u, v, width, height, textureWidth, textureHeight, color);
    }

    public static void drawRectWithTexture(Identifier location, PoseStack pose, double x, double y, double z, GuiGraphicsExtractor graphics, float u, float v, int width, int height, float textureWidth, float textureHeight) {
//        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, width, height, (int) textureWidth, (int) textureHeight);
//        float scale = 0.00390625f;
//        var e = pose.last().pose();
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        buffer.addVertex(e, (float) x, (float) (y + height), (float) z).setUv(u * scale, (v + textureHeight) * scale);
//        buffer.addVertex(e, (float) (x + width), (float) (y + height), (float) z).setUv((u + textureWidth) * scale, (v + textureHeight) * scale);
//        buffer.addVertex(e, (float) (x + width), (float) y, (float) z).setUv((u + textureWidth) * scale, v * scale);
//        buffer.addVertex(e, (float) x, (float) y, (float) z).setUv(u * scale, v * scale);
//        BufferUploader.drawWithShader(buffer.buildOrThrow());

    }

    public static void drawRectWithFullTexture(GuiGraphicsExtractor graphics, double x, double y, float u, float v, int width, int height) {
//        // Gui.blit(pose, (int) x, (int) y, width, height, u, v, width, height, 256, 256);
//        var e = graphics.pose().last().pose();
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        buffer.addVertex(e, (float) x, (float) (y + height), 0).setUv(0, 1);
//        buffer.addVertex(e, (float) (x + width), (float) (y + height), 0).setUv(1, 1);
//        buffer.addVertex(e, (float) (x + width), (float) y, 0).setUv(1, 0);
//        buffer.addVertex(e, (float) x, (float) y, 0).setUv(0, 0);
//        BufferUploader.drawWithShader(buffer.buildOrThrow());

        graphics.blit(RenderPipelines.GUI_TEXTURED, Laptop.ICON_TEXTURES, (int) x, (int) y, u, v, width, height, width, height);
    }

    public static void drawRectWithTexture(Identifier location, GuiGraphicsExtractor graphics, double x, double y, float u, float v, int width, int height, int textureWidth, int textureHeight, int sourceWidth, int sourceHeight) {
//        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, sourceWidth, sourceHeight, (int) textureWidth, (int) textureHeight);
//        float scaleWidth = 1f / sourceWidth;
//        float scaleHeight = 1f / sourceHeight;
//        var e = pose.last().pose();
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        buffer.addVertex(e, (float) x, (float) (y + height), 0).setUv(u * scaleWidth, (v + textureHeight) * scaleHeight);
//        buffer.addVertex(e, (float) (x + width), (float) (y + height), 0).setUv((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight);
//        buffer.addVertex(e, (float) (x + width), (float) y, 0).setUv((u + textureWidth) * scaleWidth, v * scaleHeight);
//        buffer.addVertex(e, (float) x, (float) y, 0).setUv(u * scaleWidth, v * scaleHeight);
//        BufferUploader.drawWithShader(buffer.buildOrThrow());
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, (int) x, (int) y, u, v, width, height, sourceWidth, sourceHeight, textureWidth, textureHeight);
    }

    public static void drawRectWithTexture(Identifier location, GuiGraphicsExtractor graphics, double x, double y, float u, float v, int width, int height, int textureWidth, int textureHeight, int sourceWidth, int sourceHeight, int color) {
//        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, sourceWidth, sourceHeight, (int) textureWidth, (int) textureHeight);
//        float scaleWidth = 1f / sourceWidth;
//        float scaleHeight = 1f / sourceHeight;
//        var e = pose.last().pose();
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        buffer.addVertex(e, (float) x, (float) (y + height), 0).setUv(u * scaleWidth, (v + textureHeight) * scaleHeight);
//        buffer.addVertex(e, (float) (x + width), (float) (y + height), 0).setUv((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight);
//        buffer.addVertex(e, (float) (x + width), (float) y, 0).setUv((u + textureWidth) * scaleWidth, v * scaleHeight);
//        buffer.addVertex(e, (float) x, (float) y, 0).setUv(u * scaleWidth, v * scaleHeight);
//        BufferUploader.drawWithShader(buffer.buildOrThrow());
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, (int) x, (int) y, u, v, width, height, sourceWidth, sourceHeight, textureWidth, textureHeight, color);
    }

    @Deprecated
    public static void drawRectWithTexture2(Identifier location, GuiGraphicsExtractor pose, double x, double y, float u, float v, int width, int height, int textureWidth, int textureHeight, int sourceWidth, int sourceHeight) {
        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, sourceWidth, sourceHeight, (int) textureWidth, (int) textureHeight);
//        float scaleWidth = 1f / sourceWidth;
//        float scaleHeight = 1f / sourceHeight;
//        var e = pose.last().pose();
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        buffer.addVertex(e, (float) x, (float) (y + height), 0).setUv(u * scaleWidth, (v + textureHeight) * scaleHeight);
//        buffer.addVertex(e, (float) (x + width), (float) (y + height), 0).setUv((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight);
//        buffer.addVertex(e, (float) (x + width), (float) y, 0).setUv((u + textureWidth) * scaleWidth, v * scaleHeight);
//        buffer.addVertex(e, (float) x, (float) y, 0).setUv(u * scaleWidth, v * scaleHeight);
////        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//        RenderSystem.disableCull();
//        RenderSystem.depthFunc(GL11.GL_LEQUAL);
//        RenderSystem.enableCull();
//        BufferUploader.drawWithShader(buffer.buildOrThrow());

        pose.blit(RenderPipelines.GUI_TEXTURED, location, (int) x, (int) y, u, v, width, height, sourceWidth, sourceHeight, textureWidth, textureHeight);
    }

    public static void drawApplicationIcon(GuiGraphicsExtractor graphics, @Nullable AppInfo info, double x, double y) {
        //TODO: Reset color GlStateManager.color(1f, 1f, 1f);
        if (info != null) {
            drawIcon(graphics, x, y, info, 14, 14);
            //  drawRectWithTexture(pose, x, y, info.getIconU(), info.getIconV(), 14, 14, 14, 14, 224, 224);
        } else {
            drawRectWithTexture(Laptop.ICON_TEXTURES, graphics, x, y, 0, 0, 14, 14, 14, 14, 224, 224);
        }
    }

    public static void drawStringClipped(GuiGraphicsExtractor graphics, String text, int x, int y, int width, int color, boolean shadow) {
        graphics.textRenderer().acceptScrolling(Component.literal(text), x + width / 2, x, x + width, y, y + Minecraft.getInstance().font.lineHeight);
    }

    public static String clipStringToWidth(String text, int width) {
        Font fontRenderer = Laptop.getLaptopFont();
        String clipped = text;
        if (fontRenderer.width(clipped) > width) {
            clipped = fontRenderer.plainSubstrByWidth(clipped, width - 8) + "...";
        }
        return clipped;
    }

    public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    public static int color(int color, int defaultColor) {
        return color > 0 ? color : defaultColor;
    }
}
