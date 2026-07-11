package dev.ultreon.devices.item;

import dev.ultreon.devices.init.ModDataComponents;
import dev.ultreon.devices.item.data.MotherboardComponents;
import dev.ultreon.devices.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author MrCrayfish
 */
public class MotherboardItem extends ComponentItem {
    public MotherboardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<net.minecraft.network.chat.Component> builder, TooltipFlag tooltipFlag) {
        MotherboardComponents components = stack.get(ModDataComponents.MOTHERBOARD_COMPONENTS.get());
        if (components == null) {
            components = new MotherboardComponents(false, false, false, false);
            stack.set(ModDataComponents.MOTHERBOARD_COMPONENTS.get(), components);
        }

        if (!KeyboardHelper.isShiftDown()) {
            builder.accept(net.minecraft.network.chat.Component.literal("CPU: " + (components.hasCpu() ? "Added" : "Missing")));
            builder.accept(net.minecraft.network.chat.Component.literal("RAM: " + (components.hasRam() ? "Added" : "Missing")));
            builder.accept(net.minecraft.network.chat.Component.literal("GPU: " + (components.hasGpu() ? "Added" : "Missing")));
            builder.accept(net.minecraft.network.chat.Component.literal("WIFI: " + (components.hasWifi() ? "Added" : "Missing")));
            builder.accept(net.minecraft.network.chat.Component.literal(ChatFormatting.YELLOW + "Hold shift for help"));
        } else {
            builder.accept(net.minecraft.network.chat.Component.literal("To add the required components"));
            builder.accept(net.minecraft.network.chat.Component.literal("place the motherboard and the"));
            builder.accept(net.minecraft.network.chat.Component.literal("corresponding component into a"));
            builder.accept(net.minecraft.network.chat.Component.literal("crafting table to combine them."));
        }
    }

    private String getComponentStatus(CompoundTag tag, String component) {
        if (tag != null && tag.contains("components")) {
            CompoundTag components = tag.getCompoundOrEmpty("components");
            if (components.contains(component)) {
                return ChatFormatting.GREEN + "Added";
            }
        }
        return ChatFormatting.RED + "Missing";
    }

    public static class Component extends ComponentItem {
        public Component(Properties properties) {
            super(properties);
        }
    }
}
