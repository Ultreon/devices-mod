package dev.ultreon.devices.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class ModBlockEntities {
    private static final Registrar<BlockEntityType<?>> REGISTER = OmnixerioDevices.REGISTRIES.get().get(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PaperBlockEntity>> PAPER = REGISTER.register(OmnixerioDevices.id("paper"), () -> new BlockEntityType<>(PaperBlockEntity::new, Set.of(ModBlocks.PAPER.get())));
    public static final RegistrySupplier<BlockEntityType<LaptopBlockEntity>> LAPTOP = REGISTER.register(OmnixerioDevices.id("laptop"), () -> new BlockEntityType<>(LaptopBlockEntity::new, Set.of(ModBlocks.getAllLaptops().toArray(new Block[]{}))));
    public static final RegistrySupplier<BlockEntityType<MacMaxXBlockEntity>> MAC_MAX_X = REGISTER.register(OmnixerioDevices.id("mac_max_x"), () -> new BlockEntityType<>(MacMaxXBlockEntity::new, Set.of(ModBlocks.MAC_MAX_X.get())));
    public static final RegistrySupplier<BlockEntityType<PrinterBlockEntity>> PRINTER = REGISTER.register(OmnixerioDevices.id("printer"), () -> new BlockEntityType<>(PrinterBlockEntity::new, Set.of(ModBlocks.getAllPrinters().toArray(new Block[]{}))));
    public static final RegistrySupplier<BlockEntityType<RouterBlockEntity>> ROUTER = REGISTER.register(OmnixerioDevices.id("router"), () -> new BlockEntityType<>(RouterBlockEntity::new, Set.of(ModBlocks.getAllRouters().toArray(new Block[]{}))));
    public static final RegistrySupplier<BlockEntityType<OfficeChairBlockEntity>> SEAT = REGISTER.register(OmnixerioDevices.id("seat"), () -> new BlockEntityType<>(OfficeChairBlockEntity::new, Set.of(ModBlocks.getAllOfficeChairs().toArray(new Block[]{}))));
    public static final RegistrySupplier<BlockEntityType<ClockBlockEntity>> CLOCK = REGISTER.register(OmnixerioDevices.id("clock"), () -> new BlockEntityType<>(ClockBlockEntity::new, Set.of(ModBlocks.getAllClocks().toArray(new Block[]{}))));

    public static void register() {
   //    Marker
    }
}
