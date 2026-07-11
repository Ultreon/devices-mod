package dev.ultreon.devices.programs.system.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.awt.*;

/**
 * @author MrCrayfish
 */
public class ColorScheme {
    public int buttonColor;
    public int textColor;
    public int textSecondaryColor;
    public int headerColor;
    public int backgroundColor;
    public int backgroundSecondaryColor;
    public int itemBackgroundColor;
    public int itemHighlightColor;
    public int buttonOutlineColor;
    public int windowBackgroundColor;
    public int windowOutlineColor;
    public int iconColor;
    public int iconSecondaryColor;

    public ColorScheme() {
        resetDefault();
    }

    public static ColorScheme fromTag(CompoundTag tag) {
        ColorScheme scheme = new ColorScheme();
        if (tag.contains("buttonColor")) {
            scheme.buttonColor = tag.getIntOr("buttonColor", Color.decode("0x2E6897").getRGB());
        }
        if (tag.contains("textColor")) {
            scheme.textColor = tag.getIntOr("textColor", Color.decode("0xFFFFFF").getRGB());
        }
        if (tag.contains("textSecondaryColor")) {
            scheme.textSecondaryColor = tag.getIntOr("textSecondaryColor", Color.decode("0xABEFF4").getRGB());
        }
        if (tag.contains("headerColor")) {
            scheme.headerColor = tag.getIntOr("headerColor", Color.decode("0x387A96").getRGB());
        }
        if (tag.contains("backgroundColor")) {
            scheme.backgroundColor = tag.getIntOr("backgroundColor", Color.decode("0x6899C2").getRGB());
        }
        if (tag.contains("backgroundSecondaryColor")) {
            scheme.backgroundSecondaryColor = tag.getIntOr("backgroundSecondaryColor", Color.decode("0x36C052").getRGB());
        }
        if (tag.contains("itemBackgroundColor")) {
            scheme.itemBackgroundColor = tag.getIntOr("itemBackgroundColor", Color.decode("0x2E6897").getRGB());
        }
        if (tag.contains("itemHighlightColor")) {
            scheme.itemHighlightColor = tag.getIntOr("itemHighlightColor", Color.decode("0x8B74C9").getRGB());
        }
        if (tag.contains("buttonOutlineColor")) {
            scheme.buttonOutlineColor = tag.getIntOr("buttonOutlineColor", Color.decode("0xFFFFFF").getRGB());
        }
        if (tag.contains("windowBackgroundColor")) {
            scheme.windowBackgroundColor = tag.getIntOr("windowBackgroundColor", Color.decode("0x788086").getRGB());
        }
        if (tag.contains("windowOutlineColor")) {
            scheme.windowOutlineColor = tag.getIntOr("windowOutlineColor", Color.decode("0x3F3F3F").getRGB());
        }
        if (tag.contains("iconColor")) {
            scheme.iconColor = tag.getIntOr("iconColor", Color.decode("0x6899C2").getRGB());
        }
        if (tag.contains("iconSecondaryColor")) {
            scheme.iconSecondaryColor = tag.getIntOr("iconSecondaryColor", Color.decode("0x36C052").getRGB());
        }
        return scheme;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSecondaryColor() {
        return textSecondaryColor;
    }

    public void setTextSecondaryColor(int textSecondaryColor) {
        this.textSecondaryColor = textSecondaryColor;
    }

    public int getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(int headerColor) {
        this.headerColor = headerColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundSecondaryColor() {
        return backgroundSecondaryColor;
    }

    public void setBackgroundSecondaryColor(int backgroundSecondaryColor) {
        this.backgroundSecondaryColor = backgroundSecondaryColor;
    }

    public int getItemBackgroundColor() {
        return itemBackgroundColor;
    }

    public void setItemBackgroundColor(int itemBackgroundColor) {
        this.itemBackgroundColor = itemBackgroundColor;
    }

    public int getItemHighlightColor() {
        return itemHighlightColor;
    }

    public void setItemHighlightColor(int itemHighlightColor) {
        this.itemHighlightColor = itemHighlightColor;
    }

    public int getButtonOutlineColor() {
        return buttonOutlineColor;
    }

    public void setButtonOutlineColor(int buttonOutlineColor) {
        this.buttonOutlineColor = buttonOutlineColor;
    }

    public int getWindowBackgroundColor() {
        return windowBackgroundColor;
    }

    public void setWindowBackgroundColor(int windowBackgroundColor) {
        this.windowBackgroundColor = windowBackgroundColor;
    }

    public int getWindowOutlineColor() {
        return windowOutlineColor;
    }

    public void setWindowOutlineColor(int windowOutlineColor) {
        this.windowOutlineColor = windowOutlineColor;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public int getIconSecondaryColor() {
        return iconSecondaryColor;
    }

    public void setIconSecondaryColor(int iconSecondaryColor) {
        this.iconSecondaryColor = iconSecondaryColor;
    }

    public void resetDefault() {
        buttonColor = Color.decode("0x2E6897").getRGB();
        textColor = Color.decode("0xFFFFFF").getRGB();
        textSecondaryColor = Color.decode("0xABEFF4").getRGB();
        headerColor = Color.decode("0x387A96").getRGB();
        backgroundColor = Color.decode("0x6899C2").getRGB();
        backgroundSecondaryColor = Color.decode("0x36C052").getRGB();
        backgroundColor = Color.decode("0x6899C2").getRGB();
        backgroundSecondaryColor = Color.decode("0x36C052").getRGB();
        iconColor = Color.decode("0x6899C2").getRGB();
        iconSecondaryColor = Color.decode("0x36C052").getRGB();
        itemBackgroundColor = Color.decode("0x2E6897").getRGB();
        itemHighlightColor = Color.decode("0x8B74C9").getRGB();
        buttonOutlineColor = Color.decode("0xFFFFFF").getRGB();
        windowBackgroundColor = Color.decode("0x788086").getRGB();
        windowOutlineColor = Color.decode("0x3F3F3F").getRGB();
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("buttonColor", buttonColor);
        tag.putInt("textColor", textColor);
        tag.putInt("textSecondaryColor", textSecondaryColor);
        tag.putInt("headerColor", headerColor);
        tag.putInt("backgroundColor", backgroundColor);
        tag.putInt("backgroundSecondaryColor", backgroundSecondaryColor);
        tag.putInt("itemBackgroundColor", itemBackgroundColor);
        tag.putInt("itemHighlightColor", itemHighlightColor);
        tag.putInt("buttonOutlineColor", buttonOutlineColor);
        tag.putInt("windowBackgroundColor", windowBackgroundColor);
        tag.putInt("windowOutlineColor", windowOutlineColor);
        tag.putInt("iconColor", iconColor);
        tag.putInt("iconSecondaryColor", iconSecondaryColor);
        return tag;
    }
}
