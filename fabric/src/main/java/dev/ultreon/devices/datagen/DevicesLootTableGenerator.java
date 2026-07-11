package dev.ultreon.devices.datagen;

import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.init.ModBlocks;
import dev.architectury.registry.registries.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DevicesLootTableGenerator extends FabricBlockLootSubProvider {
    public DevicesLootTableGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        for (var block : ModBlocks.getAllBlocks().toList()) {
            dropSelf(block);
        }
    }

    @Override
    public void generate(@NonNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        Registrar<Block> blocks = OmnixerioDevices.REGISTRIES.get().get(Registries.BLOCK);
        for (var block : ModBlocks.getAllBlocks().toList()) {
            Identifier id = blocks.getId(block);
            if (id == null) continue;
            if (id.getNamespace().equals(OmnixerioDevices.MOD_ID)) {
                biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(id.getNamespace(), "blocks/" + id.getPath())),
                        createSingleItemTable(block.asItem()));
            }
        }
    }
}
