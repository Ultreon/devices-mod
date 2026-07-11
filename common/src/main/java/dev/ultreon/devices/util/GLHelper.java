package dev.ultreon.devices.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * @author MrCrayfish
 */
public class GLHelper {
    private static int depth = 0;

    public static void pushScissor(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        depth++;
        graphics.enableScissor(x, y, x + width, y + height);
    }

    public static void popScissor(GuiGraphicsExtractor graphics) {
        restoreScissor(graphics);
    }

    private static void restoreScissor(GuiGraphicsExtractor graphics) {
        if (depth <= 0) throw new IllegalStateException("Scissor stack underflow!");

        graphics.disableScissor();
        depth--;
    }

    public static boolean isScissorStackEmpty() {
        return depth == 0;
    }

    /**
     * Do not call! Used for core only.
     */
    public static void clearScissorStack(GuiGraphicsExtractor graphics) {
        while (depth-- > 0) {
            restoreScissor(graphics);
        }
    }
}
