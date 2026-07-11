package dev.ultreon.devices.item;

import dev.ultreon.devices.OmnixerioDevices;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.arch$tab(OmnixerioDevices.TAB_DEVICE));
    }

    public static boolean isComponent(ItemStack item) {
        return item.getItem() instanceof ComponentItem;
    }
}
