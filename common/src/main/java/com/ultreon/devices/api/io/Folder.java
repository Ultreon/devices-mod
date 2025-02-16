package com.ultreon.devices.api.io;

import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.core.DataPath;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.action.FileAction;
import com.ultreon.devices.core.io.task.TaskGetFiles;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.programs.system.component.FileBrowser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
@Deprecated
public class Folder extends File {
    protected List<File> files = new ArrayList<>();

    private boolean synced = false;

    /// The default constructor for a folder
    ///
    /// @param name the name for the folder
    public Folder(String name) {
        this(name, false);
    }

    private Folder(String name, boolean protect) {
        this.name = name;
        this.protect = protect;
    }

    /// Converts a tag compound to a folder instance.
    ///
    /// @param name      the name of the folder
    /// @param folderTag the tag compound from [#toTag()]
    /// @return a folder instance
    public static Folder fromTag(String name, CompoundTag folderTag) {
        Folder folder = new Folder(name);

        if (folderTag.contains("protected", Tag.TAG_BYTE)) folder.protect = folderTag.getBoolean("protected");

        CompoundTag fileList = folderTag.getCompound("files");
        for (String fileName : fileList.getAllKeys()) {
            CompoundTag fileTag = fileList.getCompound(fileName);
            if (fileTag.contains("files")) {
                File file = Folder.fromTag(fileName, fileTag);
                file.parent = folder;
                folder.files.add(file);
            } else {
                File file = File.fromTag(fileName, fileTag);
                file.parent = folder;
                folder.files.add(file);
            }
        }
        return folder;
    }

    /// Adds a file to the folder. The folder must be in the file system before you can add files to
    /// it. If the file with the same name exists, it will not overridden. This method does not
    /// verify if the file was added successfully. See [#add(File,Callback)] to determine if
    /// it was successful or not.
    ///
    /// @param file the file to add
    public void add(File file) {
        add(file, false, null);
    }

    /// Adds a file to the folder. The folder must be in the file system before you can add files to
    /// it. If the file with the same name exists, it will not overridden. This method allows the
    /// specification of a [Callback], and will return a
    /// [FileSystem.Response] indicating if the file was
    /// successfully added to the folder or an error occurred.
    ///
    /// @param file     the file to add
    /// @param callback the response callback
    public void add(File file, @Nullable Callback<FileSystem.Response> callback) {
        add(file, false, callback);
    }

    /// Adds a file to the folder. The folder must be in the file system before you can add files to
    /// it. If the file with the same name exists, it can be overridden by passing true to the
    /// override parameter. This method also allows the specification of a [Callback], and will
    /// return a [FileSystem.Response] indicating if the file was
    /// successfully added to the folder or an error occurred.
    ///
    /// @param file     the file to add
    /// @param override if should override existing file
    /// @param callback the response callback
    public void add(File file, boolean override, @Nullable Callback<FileSystem.Response> callback) {
        if (!valid)
            throw new IllegalStateException("Folder must be added to the system before you can add files to it");

        if (file == null) {
            DebugLog.log("File is null");
            if (callback != null) {
                DebugLog.log("Callback is not null");
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal file"), false);
            }
            return;
        }

        DebugLog.log("Adding file " + file.name + " to folder " + name);

        if (!FileSystem.PATTERN_FILE_NAME.matcher(file.name).matches()) {
            DebugLog.log("File name is invalid");
            if (callback != null) {
                DebugLog.log("Callback is not null");
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID_NAME, "Invalid file name"), true);
            }
            return;
        }

        if (hasFile(file.name)) {
            DebugLog.log("File already exists");
            if (!override) {
                DebugLog.log("File already exists and override is false");
                if (callback != null) {
                    DebugLog.log("Callback is not null");
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), true);
                }
                return;
            } else if (Objects.requireNonNull(getFile(file.name)).isProtected()) {
                DebugLog.log("File already exists and override is true and file is protected");
                if (callback != null) {
                    DebugLog.log("Callback is not null");
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), true);
                }
                return;
            }
        }

        DebugLog.log("File is valid");

        FileSystem.sendAction(drive.getUUID(), FileAction.Factory.makeNewFile(Path.of(file.getPath()), file.name, override), (response, success) -> {
            DebugLog.log("Received response");
            if (success) {
                DebugLog.log("File added successfully");
                if (override) files.remove(getFile(file.name));
                file.setDrive(drive);
                file.valid = true;
                file.parent = this;
                files.add(file);
                FileBrowser.refreshList = true;
            }
            if (callback != null) {
                DebugLog.log("Callback is not null");
                callback.execute(response, success);
            }
        });
    }

    /// Deletes the specified file name from the folder. The folder must be in the file system before
    /// you can delete files from it. If the file is not found, it will just fail silently. This
    /// method does not return a response if the file was deleted successfully. See
    /// [#delete(String,Callback)] (File, Callback)} to determine if it was successful or not.
    ///
    /// @param name the file name
    public void delete(String name) {
        delete(name, null);
    }

    /// Deletes the specified file name from the folder. The folder must be in the file system before
    /// you can delete files from it. This method also allows the specification of a [Callback]
    /// , and will return a [FileSystem.Response] indicating if
    /// the file was successfully deleted from the folder or an error occurred.
    ///
    /// @param name     the file name
    /// @param callback the response callback
    public void delete(String name, @Nullable Callback<FileSystem.Response> callback) {
        delete(getFile(name), callback);
    }

    /// Delete the specified file from the folder. The folder must be in the file system before
    /// you can delete files from it. If the file is not in this folder, it will just fail silently.
    /// This method does not return a response if the file was deleted successfully. See
    /// [#delete(String,Callback)] (File, Callback)} to determine if it was successful or not.
    ///
    /// @param file a file in this folder
    public void delete(File file) {
        delete(file, null);
    }

    /// Delete the specified file from the folder. The folder must be in the file system before
    /// you can delete files from it. The file must be in this folder, otherwise it will fail. This
    /// method also allows the specification of a [Callback], and will return a
    /// [FileSystem.Response] indicating if the file was
    /// successfully deleted from the folder or an error occurred.
    ///
    /// @param file     a file in this folder
    /// @param callback the response callback
    public void delete(File file, @Nullable Callback<FileSystem.Response> callback) {
        if (!valid) throw new IllegalStateException("Folder must be added to the system before you can delete files");

        if (file == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal file"), false);
            }
            return;
        }

        if (!files.contains(file)) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "The file does not exist in this folder"), false);
            }
            return;
        }

        if (file.isProtected()) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot delete protected files"), false);
            }
            return;
        }

        FileSystem.sendAction(drive.getUUID(), FileAction.Factory.makeDelete(Path.of(file.getPath())), (response, success) -> {
            if (success) {
                file.drive = null;
                file.valid = false;
                file.parent = null;
                files.remove(file);
                FileBrowser.refreshList = true;
            }
            if (callback != null) {
                callback.execute(response, success);
            }
        });
    }

    public void copyInto(File file, boolean override, boolean cut, @Nullable Callback<FileSystem.Response> callback) {
        if (file == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal file"), false);
            }
            return;
        }

        if (!file.valid || file.drive == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Source file is invalid"), false);
            }
            return;
        }

        if (hasFile(file.name)) {
            if (!override) {
                if (callback != null) {
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), true);
                }
                return;
            } else if (Objects.requireNonNull(getFile(file.name)).isProtected()) {
                if (callback != null) {
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), true);
                }
                return;
            }
        }

        FileSystem.sendAction(drive.getUUID(), FileAction.Factory.makeCopyCut(Path.of(getPath()), new DataPath(file.drive.getUUID(), Path.of(file.getPath())), false, cut), (response, success) -> {
            assert response != null;
            if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                if (file.isFolder()) {
                    file.copy();
                }
            }
        });
    }

    /// Checks if the folder contains a file for the specified name.
    ///
    /// @param name the name of the file to find
    /// @return if the file exists
    public boolean hasFile(String name) {
        return valid && files.stream().anyMatch(file -> file.name.equalsIgnoreCase(name));
    }

    /// Gets a file from this folder for the specified name. If the file is not found, it will return
    /// null.
    ///
    /// @param name the name of the file to get
    /// @return the found file
    @Nullable
    public File getFile(String name) {
        return files.stream().filter(file -> file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void getFile(String name, Callback<File> callback) {
        if (!valid) throw new IllegalStateException("Folder must be added to the system before retrieve files");

        if (!isSynced()) {
            sync((folder, success) -> callback.execute(getFile(name), success));
        } else {
            callback.execute(getFile(name), true);
        }
    }

    /// Checks if the folder contains a folder for the specified name.
    ///
    /// @param name the name of the folder to find
    /// @return if the folder exists
    public boolean hasFolder(String name) {
        return valid && files.stream().anyMatch(file -> file.isFolder() && file.name.equalsIgnoreCase(name));
    }

    /// Gets a folder from this folder for the specified name. If the folder is not found, it will
    /// return null.
    ///
    /// @param name the name of the folder to get
    /// @return the found folder
    @Nullable
    public Folder getFolder(String name) {
        return (Folder) files.stream().filter(file -> file.isFolder() && file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void getFolder(String name, Callback<Folder> callback) {
        Folder requestedFolder = getFolder(name);

        if (requestedFolder == null) {
            callback.execute(null, false);
            return;
        }

        if (!requestedFolder.isSynced()) {
            sync((folder, success) -> callback.execute(requestedFolder, success));
        } else {
            callback.execute(requestedFolder, true);
        }
    }

    /// Gets all the files in the folder.
    ///
    /// @return a list of files
    public List<File> getFiles() {
        return files;
    }

    /// Allows you to search this folder for files using a specified predicate. This only searches
    /// the folder itself and does not include any sub-folders. Once found, it will a list of all the
    /// files that matched the predicate.
    ///
    /// @param conditions the conditions of the file
    /// @return a list of found files
    public List<File> search(Predicate<File> conditions) {
        List<File> found = NonNullList.create();
        search(found, conditions);
        return found;
    }

    private void search(List<File> results, Predicate<File> conditions) {
        files.forEach(file -> {
            if (conditions.test(file)) {
                results.add(file);
            }
        });
    }

    /// Gets whether this file is actually folder
    ///
    /// @return is this file is a folder
    @Override
    @Deprecated
    public boolean isFolder() {
        return true;
    }

    /// Sets the data for this file. This does not work on folders and will fail silently.
    ///
    /// @param data the data to set
    @Override
    public void setData(@NotNull CompoundTag data) {

    }

    /// Sets the data for this file. This does not work on folders and will fail silently. A callback
    /// can be specified but will be a guaranteed fail for folders.
    ///
    /// @param data     the data to set
    /// @param callback the response callback
    @Override
    public void setData(@NotNull CompoundTag data, Callback<FileSystem.Response> callback) {
        if (callback != null) {
            callback.execute(FileSystem.createResponse(FileSystem.Status.FAILED, "Can not set data of a folder"), false);
        }
    }

    @Override
    void setDrive(Drive drive) {
        this.drive = drive;
        files.forEach(f -> f.setDrive(drive));
    }

    /// Do not use! Syncs files from the file system
    ///
    /// @param list the tag list to read from
    public void syncFiles(ListTag list) {
        files.removeIf(f -> !f.isFolder());
        for (int i = 0; i < list.size(); i++) {
            CompoundTag fileTag = list.getCompound(i);
            File file = File.fromTag(fileTag.getString("file_name"), fileTag.getCompound("data"));
            file.drive = drive;
            file.valid = true;
            file.parent = this;
            files.add(file);
        }
        synced = true;
    }

    public void sync(@Nullable Callback<Folder> callback) {
        if (!valid) throw new IllegalStateException("Folder must be added to the system before it can be synced");

        if (!isSynced()) {
            BlockPos pos = Laptop.getPos();
            if (pos == null) {
                if (callback != null) {
                    callback.execute(this, false);
                }
                return;
            }

            Task task = files(callback, pos);
            TaskManager.sendTask(task);
        } else if (callback != null) {
            callback.execute(this, true);
        }
    }

    private @NotNull Task files(@Nullable Callback<Folder> callback, BlockPos pos) {
//        Task task = new TaskGetFiles(this, pos);
//        task.setCallback((tag, success) -> {
//            if (success && Objects.requireNonNull(tag).contains("files", Tag.TAG_LIST)) {
//                ListTag files = tag.getList("files", Tag.TAG_COMPOUND);
//                if (callback != null) {
//                    callback.execute(this, true);
//                }
//            } else if (callback != null) {
//                callback.execute(this, false);
//            }
//        });
        throw new UnsupportedOperationException();
    }

    /// Do not use! Used for checking if folder is synced with file system
    ///
    /// @return is folder synced
    public boolean isSynced() {
        return synced;
    }

    public void refresh() {
        synced = false;
    }

    /// Do not use! Used for validating files against file system
    public void validate() {
        if (!synced) {
            valid = true;
            files.forEach(f -> {
                if (f.isFolder()) {
                    ((Folder) f).validate();
                } else {
                    f.valid = true;
                }
            });
        }
    }

    /// Converts this folder into a tag compound. Due to how the file system works, this tag does not
    /// include the name of the folder and will have to be set manually for any storage.
    ///
    /// @return the folder tag
    @Override
    public CompoundTag toTag() {
        CompoundTag folderTag = new CompoundTag();

        CompoundTag fileList = new CompoundTag();
        files.forEach(file -> fileList.put(file.getName(), file.toTag()));
        folderTag.put("files", fileList);

        if (protect) folderTag.putBoolean("protected", true);

        return folderTag;
    }

    /// Returns a copy of this folder. The copied folder is considered invalid and changes to it can
    /// not be made until it is added into the file system.
    ///
    /// @return copy of this folder
    @Override
    public File copy() {
        Folder folder = new Folder(name);
        files.forEach(f -> {
            File copy = f.copy();
            copy.protect = false;
            folder.files.add(copy);
        });
        return folder;
    }

    /// Returns a copy of this folder with a different name. The copied folder is considered invalid
    /// and changes to it can not be made until it is added into the file system.
    ///
    /// @param newName the new name for the folder
    /// @return copy of this folder
    @Override
    public File copy(String newName) {
        Folder folder = new Folder(newName);
        files.forEach(f -> {
            File copy = f.copy();
            copy.protect = false;
            folder.files.add(copy);
        });
        return folder;
    }
}
