package dev.ultreon.devices.api.print;

import dev.ultreon.devices.init.ModBlockEntities;
import dev.ultreon.devices.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

//printing somethings takes makes ink cartridge take damage. cartridge can only stack to one

/**
 * @author MrCrayfish
 */
public interface IPrint {
    static void store(IPrint print, ValueOutput child) {
        child.putString("type", PrintingManager.getPrintIdentifier(print));
        print.store(child.child("data"));
    }

    static CompoundTag save(IPrint pritn) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", PrintingManager.getPrintIdentifier(pritn));
        CompoundTag data = new CompoundTag();
        pritn.saveTag(data);
        tag.put("data", data);
        return tag;
    }

    @Nullable
    static IPrint readInput(@UnknownNullability ValueInput tag) {
        Optional<String> string = tag.getString("type");
        if (string.isEmpty()) return null;
        IPrint print = PrintingManager.getPrint(string.get());
        if (print != null) {
            Optional<ValueInput> data = tag.child("data");
            if (data.isEmpty()) return null;
            print.read(data.get());
            return print;
        }
        return null;
    }

    static IPrint load(CompoundTag tag) {
        Optional<String> string = tag.getString("type");
        if (string.isEmpty()) return null;
        IPrint print = PrintingManager.getPrint(string.get());
        if (print != null) {
            Optional<CompoundTag> data = tag.getCompound("data");
            if (data.isEmpty()) return null;
            print.loadTag(data.get());
            return print;
        }

        return null;
    }

    static ItemStack generateItem(IPrint print) {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.put("print", save(print));

        ItemStack stack = new ItemStack(ModBlocks.PAPER.get());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(ModBlockEntities.PAPER.get(), blockEntityTag));

        if (print.getName() != null && !print.getName().isEmpty()) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(print.getName()));
        }
        return stack;
    }

    String getName();

    /**
     * Gets the speed of the print. The higher the value, the longer it will take to print.
     *
     * @return the speed of this print
     */
    int speed();

    /**
     * Gets whether or not this print requires colored ink.
     *
     * @return if print requires ink
     */
    boolean requiresColor();

    /**
     * Converts print into an NBT tag compound. Used for the renderer.
     */
    void store(ValueOutput data);

    void read(ValueInput tag);

    void saveTag(CompoundTag tag);

    void loadTag(CompoundTag tag);

    int[] getPixels();

    void setPixels(int[] pixels);

    int getResolution();

    void setResolution(int resolution);

    @Environment(EnvType.CLIENT)
    Class<? extends Renderer> getRenderer();

    interface Renderer {

        boolean render(GuiGraphicsExtractor pose, CompoundTag data, int packedLight, int packedOverlay, Direction direction);

        void delete();
    }
}
