package dev.ultreon.devices.item;

import dev.architectury.registry.registries.RegistrarManager;
import dev.ultreon.devices.IDeviceType;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.util.Colored;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Objects;
import java.util.function.Consumer;

public class FlashDriveItem extends Item implements Colored, SubItems, IDeviceType {

    private final DyeColor color;

    public FlashDriveItem(Properties properties, DyeColor color) {
        super(properties.arch$tab(OmnixerioDevices.TAB_DEVICE).rarity(Rarity.UNCOMMON).stacksTo(1));
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        TextColor textColor = TextColor.fromRgb(this.color == DyeColor.BLACK ? 0xffffff : this.color.getTextColor());

        MutableComponent colorComponent = Component.literal(WordUtils.capitalize(this.color.getName().replace("_", " ")))
                .withStyle(style -> style.withBold(true).withColor(textColor));
        builder.accept(Component.literal("Color: ").withStyle(ChatFormatting.GRAY).append(colorComponent));
    }

    @Override
    public NonNullList<Identifier> getModels() {
        NonNullList<Identifier> modelLocations = NonNullList.create();
        for (DyeColor color : DyeColor.values())
            modelLocations.add(OmnixerioDevices.id(Objects.requireNonNull(RegistrarManager.getId(this, Registries.ITEM)).getPath().substring(5) + "/" + color.getName()));
        return modelLocations;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public ModDeviceTypes getDeviceType() {
        return ModDeviceTypes.FLASH_DRIVE;
    }
}
