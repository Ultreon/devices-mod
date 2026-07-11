package dev.ultreon.devices.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.block.*;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModBlocks {
    private static final Registrar<Block> REGISTER = OmnixerioDevices.REGISTRIES.get().get(Registries.BLOCK);

    public static void register() {
    }

    public static final DyeableRegistration<Block> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return ModBlocks.register(color.getName() + "_laptop", Properties.of(), properties -> new LaptopBlock(properties, color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<MacMaxXBlock> MAC_MAX_X = register("mac_max_x", Properties.of(), MacMaxXBlock::new);
    public static final RegistrySupplier<MacMaxXBlockPart> MAC_MAX_X_PART = register("mac_max_x_part", Properties.of(), MacMaxXBlockPart::new);

    public static final DyeableRegistration<Block> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return ModBlocks.register(color.getName() + "_printer", Properties.of(), properties -> new PrinterBlock(properties, color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return ModBlocks.register(color.getName() + "_router", Properties.of(), properties -> new RouterBlock(properties, color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> CLOCKS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return ModBlocks.register(color.getName() + "_clock", Properties.of(), properties -> new ClockBlock(properties, color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return ModBlocks.register(color.getName() + "_office_chair", Properties.of(), properties -> new OfficeChairBlock(properties, color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };


    public static final RegistrySupplier<PaperBlock> PAPER = register("paper", Properties.of(), PaperBlock::new);

    private static <T extends Block> RegistrySupplier<T> register(String name, Properties properties, Function<Properties, T> blockSupplier) {
        Identifier id = OmnixerioDevices.id(name);
        return REGISTER.register(id, () -> blockSupplier.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id))));
    }

    public static Stream<Block> getAllBlocks() {
        return REGISTER.getIds().stream().map(REGISTER::get);
    }

    public static List<LaptopBlock> getAllLaptops() {
        return getAllBlocks().filter(block -> block instanceof LaptopBlock).map(block -> (LaptopBlock) block).toList();
    }

    public static List<PrinterBlock> getAllPrinters() {
        return getAllBlocks().filter(block -> block instanceof PrinterBlock).map(block -> (PrinterBlock) block).toList();
    }

    public static List<RouterBlock> getAllRouters() {
        return getAllBlocks().filter(block -> block instanceof RouterBlock).map(block -> (RouterBlock) block).toList();
    }

    public static List<OfficeChairBlock> getAllOfficeChairs() {
        return getAllBlocks().filter(block -> block instanceof OfficeChairBlock).map(block -> (OfficeChairBlock) block).toList();
    }

    public static List<ClockBlock> getAllClocks() {
        return getAllBlocks().filter(block -> block instanceof ClockBlock).map(block -> (ClockBlock) block).toList();
    }
}
