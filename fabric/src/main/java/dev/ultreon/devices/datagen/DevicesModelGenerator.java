package dev.ultreon.devices.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.ultreon.devices.init.ModBlocks;
import dev.ultreon.devices.init.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.world.level.block.Block;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DevicesModelGenerator extends FabricModelProvider {

    private final FabricPackOutput dataOutput;

    public DevicesModelGenerator(FabricPackOutput dataOutput) {
        super(dataOutput);
        this.dataOutput = dataOutput;
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(ModItems.GLASS_DUST.get(), ModelTemplates.FLAT_ITEM);
    }
}
