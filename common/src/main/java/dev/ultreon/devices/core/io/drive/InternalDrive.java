package dev.ultreon.devices.core.io.drive;

import dev.ultreon.devices.core.io.ServerFolder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author MrCrayfish
 */
public final class InternalDrive extends AbstractDrive {
    public InternalDrive(String name) {
        super(name);
    }

    public static @Nullable AbstractDrive fromTag(CompoundTag driveTag) {
        Optional<String> string = driveTag.getString("name");
        if (string.isEmpty()) return null;
        AbstractDrive drive = new InternalDrive(string.get());
        if (driveTag.contains("root")) {
            Optional<CompoundTag> optionalFolderTag = driveTag.getCompound("root");
            if (optionalFolderTag.isEmpty()) return null;
            CompoundTag folderTag = optionalFolderTag.get();
            Optional<String> optionalFileName = folderTag.getString("file_name");
            if (optionalFileName.isEmpty()) return null;
            Optional<CompoundTag> optionalData = folderTag.getCompound("data");
            if (optionalData.isEmpty()) return null;
            drive.root = ServerFolder.fromTag(optionalFileName.get(), optionalData.get());
        }
        return drive;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag driveTag = new CompoundTag();
        driveTag.putString("name", name);

        CompoundTag folderTag = new CompoundTag();
        folderTag.putString("file_name", root.getName());
        folderTag.put("data", root.toTag());
        driveTag.put("root", folderTag);

        return driveTag;
    }

    @Override
    public Type getType() {
        return Type.INTERNAL;
    }
}
