package dev.ultreon.devices.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.OmnixerioDevices;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

/**
 * @author MrCrayfish
 */
public class ModSounds {
    private static final Registrar<SoundEvent> REGISTER = OmnixerioDevices.REGISTRIES.get().get(Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> PRINTER_PRINTING = REGISTER.register(OmnixerioDevices.id("printer_printing"), () -> SoundEvent.createVariableRangeEvent(OmnixerioDevices.id("printer_printing")));
    public static final RegistrySupplier<SoundEvent> PRINTER_LOADING_PAPER = REGISTER.register(OmnixerioDevices.id("printer_loading_paper"), () -> SoundEvent.createVariableRangeEvent(OmnixerioDevices.id("printer_loading_paper")));

    public static void register() {

    }
}
