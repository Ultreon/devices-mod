package dev.ultreon.devices.programs.system.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record Preset(ColorScheme colorScheme, Identifier id) {

    public Tag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        return tag;
    }

    public static @Nullable Preset fromTag(CompoundTag tag) {
        Optional<String> string = tag.getString("id");
        if (string.isEmpty()) return null;
        Identifier id = Identifier.tryParse(string.get());
        if (id == null) return null;
        return ColorSchemePresetRegistry.getPreset(id);
    }
}
