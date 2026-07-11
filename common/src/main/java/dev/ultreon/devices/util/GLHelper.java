package dev.ultreon.devices.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Stack;

/**
 * @author MrCrayfish
 */
public class GLHelper {
    public static Stack<Scissor> scissorStack = new Stack<>();

    public static void pushScissor(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        if (!scissorStack.isEmpty()) {
            Scissor scissor = scissorStack.peek();
            x = Math.max(scissor.x, x);
            y = Math.max(scissor.y, y);
            width = x + width > scissor.x + scissor.width ? scissor.x + scissor.width - x : width;
            height = y + height > scissor.y + scissor.height ? scissor.y + scissor.height - y : height;
        }

        Minecraft mc = Minecraft.getInstance();
        ScaledResolution resolution = new ScaledResolution(mc);
        double scale = resolution.getScaleFactor();
        graphics.enableScissor((int) (x * scale), (int) (mc.getWindow().getHeight() - y * scale - height * scale), (int) Math.max(0, width * scale), (int) Math.max(0, height * scale));
        scissorStack.push(new Scissor(x, y, width, height));
    }

    public static void popScissor(GuiGraphicsExtractor graphics) {
        if (!scissorStack.isEmpty()) {
            scissorStack.pop();
        }
        restoreScissor(graphics);
    }

    private static void restoreScissor(GuiGraphicsExtractor graphics) {
        if (!scissorStack.isEmpty()) {
            Scissor scissor = scissorStack.peek();
            Minecraft mc = Minecraft.getInstance();
            ScaledResolution resolution = new ScaledResolution(mc);
            double scale = resolution.getScaleFactor();
            graphics.enableScissor((int) (scissor.x * scale), (int) (mc.getWindow().getHeight() - scissor.y * scale - scissor.height * scale), (int) Math.max(0, scissor.width * scale), (int) Math.max(0, scissor.height * scale));
        } else {
            graphics.disableScissor();
        }
    }

    public static boolean isScissorStackEmpty() {
        return scissorStack.isEmpty();
    }

    /**
     * Do not call! Used for core only.
     */
    public static void clearScissorStack() {
        scissorStack.clear();
    }

    public static class Scissor {
        public int x;
        public int y;
        public int width;
        public int height;

        Scissor(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
