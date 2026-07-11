package dev.ultreon.devices.core.io.drive;

import dev.ultreon.devices.core.io.ServerFolder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author MrCrayfish
 */
public final class ExternalDrive extends AbstractDrive {
    private static final Predicate<CompoundTag> PREDICATE_DRIVE_TAG = tag -> tag.contains("name") && tag.contains("uuid") && tag.contains("root");

    private ExternalDrive() {
    }

    public ExternalDrive(String displayName) {
        super(displayName);
    }

    @Nullable
    public static ExternalDrive fromTag(CompoundTag driveTag) {
        if (!PREDICATE_DRIVE_TAG.test(driveTag)) return null;

        ExternalDrive drive = new ExternalDrive();
        drive.name = driveTag.getStringOr("name", drive.name == null ? "" : drive.name);
        drive.uuid = UUID.fromString(String.valueOf(driveTag.getString("uuid")));

        Optional<CompoundTag> optionalFolderTag = driveTag.getCompound("root");
        if (optionalFolderTag.isEmpty()) {
            drive.root = new ServerFolder("");
        } else {
            CompoundTag folderTag = optionalFolderTag.get();
            drive.root = ServerFolder.fromTag(folderTag.getStringOr("file_name", ""), folderTag.getCompoundOrEmpty("data"));
        }

        return drive;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag driveTag = new CompoundTag();
        driveTag.putString("name", name);
        driveTag.putString("uuid", uuid.toString());

        CompoundTag folderTag = new CompoundTag();
        folderTag.putString("file_name", root.getName());
        folderTag.put("data", root.toTag());
        driveTag.put("root", folderTag);

        return driveTag;
    }

    @Override
    public Type getType() {
        return Type.EXTERNAL;
    }
}
