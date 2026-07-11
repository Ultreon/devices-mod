package dev.ultreon.devices.api.app.component;

import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public class ProgressBar extends Component {
    protected int width, height;
    protected int progress = 0;
    protected int max = 100;

    protected int progressColor = new Color(189, 198, 255).getRGB();
    protected int backgroundColor = Color.DARK_GRAY.getRGB();
    protected int borderColor = Color.BLACK.getRGB();

    /**
     * Default progress bar constructor
     *
     * @param left   how many pixels from the left
     * @param top    how many pixels from the top
     * @param width  width of the progress bar
     * @param height height of the progress bar
     */
    public ProgressBar(int left, int top, int width, int height) {
        super(left, top);
        this.width = width;
        this.height = height;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            Color bgColor = new Color(getColorScheme().getBackgroundColor());
            graphics.fill(x, y, x + width, y + height, bgColor.darker().darker().getRGB());
            graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, bgColor.getRGB());
            graphics.fill(x + 2, y + 2, x + 2 + getProgressScaled(), y + height - 2, bgColor.brighter().brighter().getRGB());
        }
    }

    private int getProgressScaled() {
        return (int) Math.ceil(((width - 4) * ((double) progress / (double) max)));
    }

    /**
     * Gets the current progress.
     *
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Sets the current progress.
     *
     * @param progress the progress to set
     */
    public void setProgress(int progress) {
        if (progress > max) {
            progress = max;
        } else if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;
    }

    /**
     * Sets the max progress
     *
     * @param max the max progress
     */
    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
        }
    }
}
