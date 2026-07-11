package dev.ultreon.devices.api.app.component;

import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.api.app.listener.ClickListener;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.renderer.RenderPipelines;

import java.awt.*;

@SuppressWarnings("unused")
public class CheckBox extends Component implements RadioGroup.Item {
    protected String name;
    protected boolean checked = false;
    protected RadioGroup group = null;

    protected ClickListener listener = null;

    protected int textColor = Color.WHITE.getRGB();
    protected int backgroundColor = Color.GRAY.getRGB();
    protected int borderColor = Color.BLACK.getRGB();
    protected int checkedColor = Color.DARK_GRAY.getRGB();

    /**
     * Default check box constructor
     *
     * @param name the name of the check box
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public CheckBox(String name, int left, int top) {
        super(left, top);
        this.name = name;
    }

    /**
     * Sets the radio group for this button.
     *
     * @param group the radio group.
     */
    public void setRadioGroup(RadioGroup group) {
        this.group = group;
        this.group.add(this);
    }

    /**
     * Sets the click listener. Use this to handle custom actions
     * when you press the check box.
     *
     * @param listener the click listener
     */
    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            if (group == null) {
                Color bgColor = new Color(getColorScheme().getBackgroundColor());
                graphics.fill(x, y, x + 10, y + 10, color(borderColor, bgColor.darker().darker().getRGB()));
                graphics.fill(x + 1, y + 1, x + 9, y + 9, color(backgroundColor, bgColor.getRGB()));
                if (checked) {
                    graphics.fill(x + 2, y + 2, x + 8, y + 8, color(checkedColor, bgColor.brighter().brighter().getRGB()));
                }
            } else {
                Color bgColor = new Color(getColorScheme().getBackgroundColor()).brighter().brighter();
                float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
                bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1f));
                graphics.blit(RenderPipelines.GUI_TEXTURED, COMPONENTS_GUI, x, y, checked ? 10 : 0, 60, 10, 10, 256, 256, 0xff00000 | bgColor.getRGB());
            }
            graphics.textRenderer().accept(TextAlignment.LEFT, x + 12, y + 1, net.minecraft.network.chat.Component.literal(name).withColor(color(textColor, getColorScheme().getTextColor())));
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!this.visible || !this.enabled) return;

        if (GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + 10, yPosition + 10)) {
            if (group != null) {
                group.deselect();
            }
            this.checked = !checked;
            if (listener != null) {
                listener.onClick(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public boolean isSelected() {
        return checked;
    }

    @Override
    public void setSelected(boolean enabled) {
        this.checked = enabled;
    }

    /**
     * Sets the text color for this component
     *
     * @param color the text color
     */
    public void setTextColor(Color color) {
        this.textColor = color.getRGB();
    }

    /**
     * Sets the background color for this component
     *
     * @param color the background color
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color.getRGB();
    }

    /**
     * Sets the border color for this component
     *
     * @param color the border color
     */
    public void setBorderColor(Color color) {
        this.borderColor = color.getRGB();
    }

    /**
     * Sets the checked color for this component
     *
     * @param color the checked color
     */
    public void setCheckedColor(Color color) {
        this.checkedColor = color.getRGB();
    }
}
