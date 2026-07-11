package dev.ultreon.devices.util;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class ItemColors {
    public static ItemLike woolByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WOOL.white();
            case ORANGE -> Items.WOOL.orange();
            case MAGENTA -> Items.WOOL.magenta();
            case LIGHT_BLUE -> Items.WOOL.lightBlue();
            case YELLOW -> Items.WOOL.yellow();
            case LIME -> Items.WOOL.lime();
            case PINK -> Items.WOOL.pink();
            case GRAY -> Items.WOOL.gray();
            case LIGHT_GRAY -> Items.WOOL.lightGray();
            case CYAN -> Items.WOOL.cyan();
            case PURPLE -> Items.WOOL.purple();
            case BLUE -> Items.WOOL.blue();
            case BROWN -> Items.WOOL.brown();
            case GREEN -> Items.WOOL.green();
            case RED -> Items.WOOL.red();
            case BLACK -> Items.WOOL.black();
        };
    }

    public static ItemLike dyeByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.DYE.white();
            case ORANGE -> Items.DYE.orange();
            case MAGENTA -> Items.DYE.magenta();
            case LIGHT_BLUE -> Items.DYE.lightBlue();
            case YELLOW -> Items.DYE.yellow();
            case LIME -> Items.DYE.lime();
            case PINK -> Items.DYE.pink();
            case GRAY -> Items.DYE.gray();
            case LIGHT_GRAY -> Items.DYE.lightGray();
            case CYAN -> Items.DYE.cyan();
            case PURPLE -> Items.DYE.purple();
            case BLUE -> Items.DYE.blue();
            case BROWN -> Items.DYE.brown();
            case GREEN -> Items.DYE.green();
            case RED -> Items.DYE.red();
            case BLACK -> Items.DYE.black();
        };
    }

    public static ItemLike carpetByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.CARPET.white();
            case ORANGE -> Items.CARPET.orange();
            case MAGENTA -> Items.CARPET.magenta();
            case LIGHT_BLUE -> Items.CARPET.lightBlue();
            case YELLOW -> Items.CARPET.yellow();
            case LIME -> Items.CARPET.lime();
            case PINK -> Items.CARPET.pink();
            case GRAY -> Items.CARPET.gray();
            case LIGHT_GRAY -> Items.CARPET.lightGray();
            case CYAN -> Items.CARPET.cyan();
            case PURPLE -> Items.CARPET.purple();
            case BLUE -> Items.CARPET.blue();
            case BROWN -> Items.CARPET.brown();
            case GREEN -> Items.CARPET.green();
            case RED -> Items.CARPET.red();
            case BLACK -> Items.CARPET.black();
        };
    }

    public static ItemLike terracottaByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.DYED_TERRACOTTA.white();
            case ORANGE -> Items.DYED_TERRACOTTA.orange();
            case MAGENTA -> Items.DYED_TERRACOTTA.magenta();
            case LIGHT_BLUE -> Items.DYED_TERRACOTTA.lightBlue();
            case YELLOW -> Items.DYED_TERRACOTTA.yellow();
            case LIME -> Items.DYED_TERRACOTTA.lime();
            case PINK -> Items.DYED_TERRACOTTA.pink();
            case GRAY -> Items.DYED_TERRACOTTA.gray();
            case LIGHT_GRAY -> Items.DYED_TERRACOTTA.lightGray();
            case CYAN -> Items.DYED_TERRACOTTA.cyan();
            case PURPLE -> Items.DYED_TERRACOTTA.purple();
            case BLUE -> Items.DYED_TERRACOTTA.blue();
            case BROWN -> Items.DYED_TERRACOTTA.brown();
            case GREEN -> Items.DYED_TERRACOTTA.green();
            case RED -> Items.DYED_TERRACOTTA.red();
            case BLACK -> Items.DYED_TERRACOTTA.black();
        };
    }

    public static ItemLike glassByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.STAINED_GLASS.white();
            case ORANGE -> Items.STAINED_GLASS.orange();
            case MAGENTA -> Items.STAINED_GLASS.magenta();
            case LIGHT_BLUE -> Items.STAINED_GLASS.lightBlue();
            case YELLOW -> Items.STAINED_GLASS.yellow();
            case LIME -> Items.STAINED_GLASS.lime();
            case PINK -> Items.STAINED_GLASS.pink();
            case GRAY -> Items.STAINED_GLASS.gray();
            case LIGHT_GRAY -> Items.STAINED_GLASS.lightGray();
            case CYAN -> Items.STAINED_GLASS.cyan();
            case PURPLE -> Items.STAINED_GLASS.purple();
            case BLUE -> Items.STAINED_GLASS.blue();
            case BROWN -> Items.STAINED_GLASS.brown();
            case GREEN -> Items.STAINED_GLASS.green();
            case RED -> Items.STAINED_GLASS.red();
            case BLACK -> Items.STAINED_GLASS.black();
        };
    }

    public static ItemLike glassPaneByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.STAINED_GLASS_PANE.white();
            case ORANGE -> Items.STAINED_GLASS_PANE.orange();
            case MAGENTA -> Items.STAINED_GLASS_PANE.magenta();
            case LIGHT_BLUE -> Items.STAINED_GLASS_PANE.lightBlue();
            case YELLOW -> Items.STAINED_GLASS_PANE.yellow();
            case LIME -> Items.STAINED_GLASS_PANE.lime();
            case PINK -> Items.STAINED_GLASS_PANE.pink();
            case GRAY -> Items.STAINED_GLASS_PANE.gray();
            case LIGHT_GRAY -> Items.STAINED_GLASS_PANE.lightGray();
            case CYAN -> Items.STAINED_GLASS_PANE.cyan();
            case PURPLE -> Items.STAINED_GLASS_PANE.purple();
            case BLUE -> Items.STAINED_GLASS_PANE.blue();
            case BROWN -> Items.STAINED_GLASS_PANE.brown();
            case GREEN -> Items.STAINED_GLASS_PANE.green();
            case RED -> Items.STAINED_GLASS_PANE.red();
            case BLACK -> Items.STAINED_GLASS_PANE.black();
        };
    }

    public static ItemLike concreteByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.CONCRETE.white();
            case ORANGE -> Items.CONCRETE.orange();
            case MAGENTA -> Items.CONCRETE.magenta();
            case LIGHT_BLUE -> Items.CONCRETE.lightBlue();
            case YELLOW -> Items.CONCRETE.yellow();
            case LIME -> Items.CONCRETE.lime();
            case PINK -> Items.CONCRETE.pink();
            case GRAY -> Items.CONCRETE.gray();
            case LIGHT_GRAY -> Items.CONCRETE.lightGray();
            case CYAN -> Items.CONCRETE.cyan();
            case PURPLE -> Items.CONCRETE.purple();
            case BLUE -> Items.CONCRETE.blue();
            case BROWN -> Items.CONCRETE.brown();
            case GREEN -> Items.CONCRETE.green();
            case RED -> Items.CONCRETE.red();
            case BLACK -> Items.CONCRETE.black();
        };
    }

    public static ItemLike concretePowderByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.CONCRETE_POWDER.white();
            case ORANGE -> Items.CONCRETE_POWDER.orange();
            case MAGENTA -> Items.CONCRETE_POWDER.magenta();
            case LIGHT_BLUE -> Items.CONCRETE_POWDER.lightBlue();
            case YELLOW -> Items.CONCRETE_POWDER.yellow();
            case LIME -> Items.CONCRETE_POWDER.lime();
            case PINK -> Items.CONCRETE_POWDER.pink();
            case GRAY -> Items.CONCRETE_POWDER.gray();
            case LIGHT_GRAY -> Items.CONCRETE_POWDER.lightGray();
            case CYAN -> Items.CONCRETE_POWDER.cyan();
            case PURPLE -> Items.CONCRETE_POWDER.purple();
            case BLUE -> Items.CONCRETE_POWDER.blue();
            case BROWN -> Items.CONCRETE_POWDER.brown();
            case GREEN -> Items.CONCRETE_POWDER.green();
            case RED -> Items.CONCRETE_POWDER.red();
            case BLACK -> Items.CONCRETE_POWDER.black();
        };
    }

    public static ItemLike glazedTerracottaByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.GLAZED_TERRACOTTA.white();
            case ORANGE -> Items.GLAZED_TERRACOTTA.orange();
            case MAGENTA -> Items.GLAZED_TERRACOTTA.magenta();
            case LIGHT_BLUE -> Items.GLAZED_TERRACOTTA.lightBlue();
            case YELLOW -> Items.GLAZED_TERRACOTTA.yellow();
            case LIME -> Items.GLAZED_TERRACOTTA.lime();
            case PINK -> Items.GLAZED_TERRACOTTA.pink();
            case GRAY -> Items.GLAZED_TERRACOTTA.gray();
            case LIGHT_GRAY -> Items.GLAZED_TERRACOTTA.lightGray();
            case CYAN -> Items.GLAZED_TERRACOTTA.cyan();
            case PURPLE -> Items.GLAZED_TERRACOTTA.purple();
            case BLUE -> Items.GLAZED_TERRACOTTA.blue();
            case BROWN -> Items.GLAZED_TERRACOTTA.brown();
            case GREEN -> Items.GLAZED_TERRACOTTA.green();
            case RED -> Items.GLAZED_TERRACOTTA.red();
            case BLACK -> Items.GLAZED_TERRACOTTA.black();
        };
    }

    public static ItemLike bedByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.BED.white();
            case ORANGE -> Items.BED.orange();
            case MAGENTA -> Items.BED.magenta();
            case LIGHT_BLUE -> Items.BED.lightBlue();
            case YELLOW -> Items.BED.yellow();
            case LIME -> Items.BED.lime();
            case PINK -> Items.BED.pink();
            case GRAY -> Items.BED.gray();
            case LIGHT_GRAY -> Items.BED.lightGray();
            case CYAN -> Items.BED.cyan();
            case PURPLE -> Items.BED.purple();
            case BLUE -> Items.BED.blue();
            case BROWN -> Items.BED.brown();
            case GREEN -> Items.BED.green();
            case RED -> Items.BED.red();
            case BLACK -> Items.BED.black();
        };
    }

    public static ItemLike shulkerBoxByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.DYED_SHULKER_BOX.white();
            case ORANGE -> Items.DYED_SHULKER_BOX.orange();
            case MAGENTA -> Items.DYED_SHULKER_BOX.magenta();
            case LIGHT_BLUE -> Items.DYED_SHULKER_BOX.lightBlue();
            case YELLOW -> Items.DYED_SHULKER_BOX.yellow();
            case LIME -> Items.DYED_SHULKER_BOX.lime();
            case PINK -> Items.DYED_SHULKER_BOX.pink();
            case GRAY -> Items.DYED_SHULKER_BOX.gray();
            case LIGHT_GRAY -> Items.DYED_SHULKER_BOX.lightGray();
            case CYAN -> Items.DYED_SHULKER_BOX.cyan();
            case PURPLE -> Items.DYED_SHULKER_BOX.purple();
            case BLUE -> Items.DYED_SHULKER_BOX.blue();
            case BROWN -> Items.DYED_SHULKER_BOX.brown();
            case GREEN -> Items.DYED_SHULKER_BOX.green();
            case RED -> Items.DYED_SHULKER_BOX.red();
            case BLACK -> Items.DYED_SHULKER_BOX.black();
        };
    }

    public static ItemLike candleByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.DYED_CANDLE.white();
            case ORANGE -> Items.DYED_CANDLE.orange();
            case MAGENTA -> Items.DYED_CANDLE.magenta();
            case LIGHT_BLUE -> Items.DYED_CANDLE.lightBlue();
            case YELLOW -> Items.DYED_CANDLE.yellow();
            case LIME -> Items.DYED_CANDLE.lime();
            case PINK -> Items.DYED_CANDLE.pink();
            case GRAY -> Items.DYED_CANDLE.gray();
            case LIGHT_GRAY -> Items.DYED_CANDLE.lightGray();
            case CYAN -> Items.DYED_CANDLE.cyan();
            case PURPLE -> Items.DYED_CANDLE.purple();
            case BLUE -> Items.DYED_CANDLE.blue();
            case BROWN -> Items.DYED_CANDLE.brown();
            case GREEN -> Items.DYED_CANDLE.green();
            case RED -> Items.DYED_CANDLE.red();
            case BLACK -> Items.DYED_CANDLE.black();
        };
    }

    public static ItemLike bannerByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.BANNER.white();
            case ORANGE -> Items.BANNER.orange();
            case MAGENTA -> Items.BANNER.magenta();
            case LIGHT_BLUE -> Items.BANNER.lightBlue();
            case YELLOW -> Items.BANNER.yellow();
            case LIME -> Items.BANNER.lime();
            case PINK -> Items.BANNER.pink();
            case GRAY -> Items.BANNER.gray();
            case LIGHT_GRAY -> Items.BANNER.lightGray();
            case CYAN -> Items.BANNER.cyan();
            case PURPLE -> Items.BANNER.purple();
            case BLUE -> Items.BANNER.blue();
            case BROWN -> Items.BANNER.brown();
            case GREEN -> Items.BANNER.green();
            case RED -> Items.BANNER.red();
            case BLACK -> Items.BANNER.black();
        };
    }
}
