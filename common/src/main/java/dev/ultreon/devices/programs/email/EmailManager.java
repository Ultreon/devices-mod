package dev.ultreon.devices.programs.email;

import com.google.common.collect.HashBiMap;
import dev.ultreon.devices.MoreCodecs;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.WorldSavedData;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.api.app.Notification;
import dev.ultreon.devices.programs.email.object.Email;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * @author MrCrayfish
 */
public class EmailManager implements WorldSavedData {
    public static final EmailManager INSTANCE = new EmailManager();
    private final HashBiMap<UUID, String> uuidToName = HashBiMap.create();
    private final Map<String, List<Email>> nameToInbox = new HashMap<>();
    @Environment(EnvType.CLIENT)
    private List<Email> inbox;

    public boolean addEmailToInbox(Email email, String to) {
        if (nameToInbox.containsKey(to)) {
            nameToInbox.get(to).add(0, email);
            sendNotification(to, email);
            return true;
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    public List<Email> getInbox() {
        if (inbox == null) {
            inbox = new ArrayList<>();
        }
        return inbox;
    }

    public List<Email> getEmailsForAccount(Player player) {
        if (uuidToName.containsKey(player.getUUID())) {
            return nameToInbox.get(uuidToName.get(player.getUUID()));
        }
        return new ArrayList<Email>();
    }

    public boolean addAccount(Player player, String name) {
        if (!uuidToName.containsKey(player.getUUID())) {
            if (!uuidToName.containsValue(name)) {
                uuidToName.put(player.getUUID(), name);
                nameToInbox.put(name, new ArrayList<Email>());
                return true;
            }
        }
        return false;
    }

    public boolean hasAccount(UUID uuid) {
        return uuidToName.containsKey(uuid);
    }

    public String getName(Player player) {
        return uuidToName.get(player.getUUID());
    }

    public void load(CompoundTag nbt) {
        nameToInbox.clear();

        Optional<ListTag> optionalInboxes = nbt.getList("Inboxes");
        if (optionalInboxes.isEmpty()) return;
        ListTag inboxes = optionalInboxes.get();
        for (int i = 0; i < inboxes.size(); i++) {
            Optional<CompoundTag> optionalInbox = inboxes.getCompound(i);
            if (optionalInbox.isEmpty()) continue;
            CompoundTag inbox = optionalInbox.get();
            String name = inbox.getStringOr("Name", "Anonymous");

            List<Email> emails = new ArrayList<Email>();
            ListTag emailTagList = (ListTag) inbox.get("Emails");
            for (int j = 0; j < emailTagList.size(); j++) {
                Optional<CompoundTag> inboxEmailTag = emailTagList.getCompound(j);
                if (inboxEmailTag.isEmpty()) continue;
                CompoundTag emailTag = inboxEmailTag.get();
                Email email = Email.readFromNBT(emailTag);
                emails.add(email);
            }
            nameToInbox.put(name, emails);
        }

        uuidToName.clear();

        ListTag accounts = (ListTag) nbt.get("Accounts");
        for (int i = 0; i < accounts.size(); i++) {
            Optional<CompoundTag> optionalAccount = accounts.getCompound(i);
            if (optionalAccount.isEmpty()) continue;

            CompoundTag account = optionalAccount.get();

            Optional<UUID> optionalUUID = account.read("UUID", MoreCodecs.UUID);
            Optional<String> optionalName = account.getString("Name");
            if (optionalUUID.isEmpty() || optionalName.isEmpty()) continue;
            UUID uuid = optionalUUID.get();
            String name = optionalName.get();
            uuidToName.put(uuid, name);
        }
    }

    public void save(CompoundTag nbt) {
        ListTag inboxes = new ListTag();
        for (String key : nameToInbox.keySet()) {
            CompoundTag inbox = new CompoundTag();
            inbox.putString("Name", key);

            ListTag emailTagList = new ListTag();
            List<Email> emails = nameToInbox.get(key);
            for (Email email : emails) {
                CompoundTag emailTag = new CompoundTag();
                email.save(emailTag);
                emailTagList.add(emailTag);
            }
            inbox.put("Emails", emailTagList);
            inboxes.add(inbox);
        }
        nbt.put("Inboxes", inboxes);

        ListTag accounts = new ListTag();
        for (UUID key : uuidToName.keySet()) {
            CompoundTag account = new CompoundTag();
            account.store("UUID", MoreCodecs.UUID, key);
            account.putString("Name", Objects.requireNonNull(uuidToName.get(key)));
            accounts.add(account);
        }
        nbt.put("Accounts", accounts);
    }

    public void clear() {
        nameToInbox.clear();
        uuidToName.clear();
        inbox.clear();
    }

    private void sendNotification(String name, Email email) {
        MinecraftServer server = OmnixerioDevices.getServer();
        UUID id = uuidToName.inverse().get(name);
        if (id != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(id);
            if (player != null) {
                Notification notification = new Notification(Icons.MAIL, "New Email!", "from " + email.getAuthor());
                notification.pushTo(player);
            }
        }
    }
}
