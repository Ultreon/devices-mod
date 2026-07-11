package dev.ultreon.devices.gui;

import dev.ultreon.devices.core.Window;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class GuiButtonClose extends Button {
    private static final Identifier CLOSE_HOVER = Window.TITLEBAR.withPath("close_hover");
    private static final Identifier CLOSE = Window.TITLEBAR.withPath("close");

    public GuiButtonClose(int x, int y) {
        super(x, y, 11, 11, Component.literal(""),
                (_) -> { }, (_)-> Component.empty());
    }

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, isHovered ? CLOSE_HOVER : CLOSE, this.getX(), this.getY(), 11, 11);
        }
    }
}
