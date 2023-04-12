package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.init.DeviceTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class DevicesRecipeProvider extends FabricRecipeProvider {
    public DevicesRecipeProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        DeviceBlocks.LAPTOPS.getMap().forEach(((dyeColor, blockRegistrySupplier) -> laptop(exporter, blockRegistrySupplier.get(), dyeColor)));
        DeviceBlocks.ROUTERS.getMap().forEach(((dyeColor, blockRegistrySupplier) -> router(exporter, blockRegistrySupplier.get(), dyeColor)));
        DeviceItems.FLASH_DRIVE.getMap().forEach(((dyeColor, blockRegistrySupplier) -> flashDrive(exporter, blockRegistrySupplier.get(), dyeColor)));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, DeviceItems.COMPONENT_FULL_MOTHERBOARD.get())
                .requires(DeviceItems.COMPONENT_CPU.get())
                .requires(DeviceItems.COMPONENT_GPU.get())
                .requires(DeviceItems.COMPONENT_RAM.get())
                .requires(DeviceItems.COMPONENT_WIFI.get())
                .requires(DeviceItems.COMPONENT_MOTHERBOARD.get())
                .unlockedBy(getHasName(DeviceItems.COMPONENT_MOTHERBOARD.get()), has(DeviceItems.COMPONENT_MOTHERBOARD.get()))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_CPU.get()), has(DeviceItems.COMPONENT_CPU.get()))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_GPU.get()), has(DeviceItems.COMPONENT_GPU.get()))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_RAM.get()), has(DeviceItems.COMPONENT_RAM.get()))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_WIFI.get()), has(DeviceItems.COMPONENT_WIFI.get()))
                .save(exporter);
    }

    public static void laptop(Consumer<FinishedRecipe> exporter, ItemLike laptop, DyeColor color) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, laptop)
                .define('+', DyeUtils.getWoolFromDye(color))
                .define('/', Items.NETHERITE_INGOT)
                .define('#', DeviceItems.COMPONENT_SCREEN.get())
                .define('|', Items.QUARTZ)
                .define('$', DeviceItems.COMPONENT_BATTERY.get())
                .define('O', DeviceTags.Items.INTERNAL_STORAGE)
                .pattern("+#+")
                .pattern("/$/")
                .pattern("|O|").group("devices:laptop")
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_FULL_MOTHERBOARD.get()), has(DeviceItems.COMPONENT_FULL_MOTHERBOARD.get()))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_BATTERY.get()), has(DeviceItems.COMPONENT_BATTERY.get()))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_HARD_DRIVE.get()), has(DeviceTags.Items.INTERNAL_STORAGE))
                .save(exporter);
    }

    public static void router(Consumer<FinishedRecipe> exporter, ItemLike router, DyeColor color) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, router)
                .define('+', DyeUtils.getWoolFromDye(color))
                .define('/', Items.IRON_INGOT)
                .define('.', DeviceItems.PLASTIC.get())
                .define('$', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                .define('O', Items.ENDER_EYE)
                .pattern("/ /")
                .pattern("+O+")
                .pattern(".$.").group("devices:router")
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()), has(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
    }

    public static void flashDrive(Consumer<FinishedRecipe> exporter, ItemLike flashDrive, DyeColor color) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, flashDrive)
                .define('+', DyeUtils.getCarpetFromDye(color))
                .define('/', Items.IRON_INGOT)
                .define('$', DeviceItems.COMPONENT_FLASH_CHIP.get())
                .pattern("/$+").group("devices:flash_drive")
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()), has(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
    }
}
