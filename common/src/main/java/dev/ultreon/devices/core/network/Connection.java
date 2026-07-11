package dev.ultreon.devices.core.network;

import dev.ultreon.devices.MoreCodecs;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Connection {
    private UUID routerId;
    private BlockPos routerPos;

    private Connection() {

    }

    public Connection(Router router) {
        this.routerId = router.getId();
        this.routerPos = router.getPos();
    }

    public UUID getRouterId() {
        return routerId;
    }

    @Nullable
    public BlockPos getRouterPos() {
        return routerPos;
    }

    public void setRouterPos(@Nullable BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Nullable
    public Router getRouter(@NotNull Level level) {
        if (routerPos == null)
            return null;

        BlockEntity blockEntity = level.getBlockEntity(routerPos);
        if (blockEntity instanceof RouterBlockEntity router) {
            if (router.getRouter().getId().equals(routerId)) {
                return router.getRouter();
            } else {
                DebugLog.log("Invalid router ID");
            }
        } else {
            DebugLog.log("Router is not a router");
        }
        return null;
    }

    public boolean isConnected() {
        return routerPos != null;
    }

    public void save(ValueOutput out) {
        out.store("id", MoreCodecs.UUID, routerId);
        out.store("Pos", BlockPos.CODEC, routerPos);
    }

    public @Nullable Connection load(ValueInput in) {
        Connection connection = new Connection();

        Optional<UUID> optionalId = in.read("id", MoreCodecs.UUID);
        Optional<BlockPos> optionalPos = in.read("Pos", BlockPos.CODEC);

        if (optionalId.isEmpty()) return null;

        if (optionalPos.isEmpty()) {
            connection.routerId = null;
            connection.routerPos = null;
            return connection;
        }

        connection.routerId = optionalId.get();
        connection.routerPos = optionalPos.get();
        return connection;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.store("id", MoreCodecs.UUID, routerId);
        tag.store("Pos", BlockPos.CODEC, routerPos);
        return tag;
    }

    public static @Nullable Connection fromTag(CompoundTag tag) {
        Connection connection = new Connection();
        Optional<UUID> optionalId = tag.read("id", MoreCodecs.UUID);
        Optional<BlockPos> optionalPos = tag.read("Pos", BlockPos.CODEC);

        if (optionalId.isEmpty()) return null;
        if (optionalPos.isEmpty()) {
            connection.routerId = null;
            connection.routerPos = null;
            return connection;
        }
        connection.routerId = optionalId.get();
        connection.routerPos = optionalPos.get();
        return connection;
    }
}
