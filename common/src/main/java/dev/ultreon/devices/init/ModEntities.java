package dev.ultreon.devices.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.entity.SeatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ModEntities {
    private static final Registrar<EntityType<?>> REGISTER = OmnixerioDevices.REGISTRIES.get().get(Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<SeatEntity>> SEAT = register("seat", () -> {
        Identifier id = OmnixerioDevices.id("seat");
        return EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(ResourceKey.create(Registries.ENTITY_TYPE, id));
    });

    private static <T extends EntityType<?>> RegistrySupplier<T> register(String id, Supplier<T> supplier) {
        return REGISTER.register(OmnixerioDevices.id(id), supplier);
    }

    public static void register() {

    }
}
