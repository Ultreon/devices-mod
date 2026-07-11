package dev.ultreon.devices.datagen;

import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.init.ModItems;
import dev.ultreon.devices.init.tags.ModItemTags;
import dev.architectury.registry.registries.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DevicesItemTagProvider extends FabricTagsProvider<Item> {
    public DevicesItemTagProvider(FabricPackOutput dataGenerator) {
        super(dataGenerator, Registries.ITEM, CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor()));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Registrar<Item> items = OmnixerioDevices.REGISTRIES.get().get(Registries.ITEM);
        TagAppender<Item> laptops = this.tag(ModItemTags.LAPTOPS);
        TagAppender<Item> printers = this.tag(ModItemTags.PRINTERS);
        TagAppender<Item> routers = this.tag(ModItemTags.ROUTERS);
        TagAppender<Item> flashDrives = this.tag(ModItemTags.FLASH_DRIVES);

        ModItems.getAllLaptops().forEach(o -> laptops.addOptional(ResourceKey.create(Registries.ITEM, Objects.requireNonNull(items.getId(o)))));
        ModItems.getAllPrinters().forEach(o -> printers.addOptional(ResourceKey.create(Registries.ITEM, Objects.requireNonNull(items.getId(o)))));
        ModItems.getAllRouters().forEach(o -> routers.addOptional(ResourceKey.create(Registries.ITEM, Objects.requireNonNull(items.getId(o)))));
        ModItems.getAllFlashDrives().forEach(o -> flashDrives.addOptional(ResourceKey.create(Registries.ITEM, Objects.requireNonNull(items.getId(o)))));
    }
}
