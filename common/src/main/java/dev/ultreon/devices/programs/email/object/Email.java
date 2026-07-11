package dev.ultreon.devices.programs.email.object;

import dev.ultreon.devices.api.io.File;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author MrCrayfish
 */
public class Email {
    private final String subject;
    private String author;
    private final String message;
    private final File attachment;
    private boolean read;

    public Email(String subject, String message, @Nullable File file) {
        this.subject = subject;
        this.message = message;
        this.attachment = file;
        this.read = false;
    }

    public Email(String subject, String author, String message, @Nullable File attachment) {
        this(subject, message, attachment);
        this.author = author;
    }

    public static Email readFromNBT(CompoundTag nbt) {
        File attachment = null;
        Optional<CompoundTag> fileTag = nbt.getCompound("attachment");
        if (fileTag.isPresent()) {
            attachment = File.fromTag(fileTag.get().getStringOr("file_name", "unnamed"), fileTag.get().getCompoundOrEmpty("data"));
        }
        Email email = new Email(nbt.getString("subject").orElseThrow(), nbt.getString("author").orElseThrow(), nbt.getString("message").orElseThrow(), attachment);
        email.setRead(nbt.getBoolean("read").orElse(false));
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public File getAttachment() {
        return attachment;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void save(CompoundTag nbt) {
        nbt.putString("subject", this.subject);
        if (author != null) nbt.putString("author", this.author);
        nbt.putString("message", this.message);
        nbt.putBoolean("read", this.read);

        if (attachment != null) {
            CompoundTag fileTag = new CompoundTag();
            fileTag.putString("file_name", attachment.getName());
            fileTag.put("data", attachment.toTag());
            nbt.put("attachment", fileTag);
        }
    }
}
