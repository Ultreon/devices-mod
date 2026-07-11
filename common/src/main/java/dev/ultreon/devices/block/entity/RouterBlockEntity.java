package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.core.network.Router;
import dev.ultreon.devices.init.ModBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

@SuppressWarnings("unused")
public class RouterBlockEntity extends DeviceBlockEntity.Colored {
    private Router router;

    @Environment(EnvType.CLIENT)
    private int debugTimer;

    public RouterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.ROUTER.get(), pWorldPosition, pBlockState);
    }

    public Router getRouter() {
        if (router == null) {
            router = new Router(worldPosition);
            setChanged();
        }
        return router;
    }

    public void tick() {
        assert level != null;
        if (!level.isClientSide()) {
            getRouter().tick(level);
        } else if (debugTimer > 0) {
            debugTimer--;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean isDebug() {
        return debugTimer > 0;
    }

    @Environment(EnvType.CLIENT)
    public void setDebug(boolean debug) {
        if (debug) {
            debugTimer = 1200;
        } else {
            debugTimer = 0;
        }
    }

    public String getDeviceName() {
        return "Router";
    }

    @Override
    public void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);

        Optional<ValueInput> optionalRouter = tag.child("router");
        if (optionalRouter.isPresent()) {
            router = Router.load(worldPosition, optionalRouter.get());
        }

        Optional<CompoundTag> router1 = tag.read("router", CompoundTag.CODEC);
        router1.ifPresent(compoundTag -> router = Router.fromTag(worldPosition, compoundTag));
    }

    public void syncDevicesToClient() {
        pipeline.put("router", getRouter().toTag(true));
        sync();
    }

    // Todo - Maybe implement this whenever possible?
//    @Override
//    public double getMaxRenderDistanceSqr() {
//        return 16384;
//    }
//
//    @PlatformOnly("forge")
//    @Environment(EnvType.CLIENT)
//    @ExpectPlatform
//    public AABB getRenderBoundingBox() {
//        throw new AssertionError();
//    }
}
