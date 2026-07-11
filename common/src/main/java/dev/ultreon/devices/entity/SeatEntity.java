package dev.ultreon.devices.entity;

import dev.ultreon.devices.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SeatEntity extends Entity {
    private double yOffset;
    private BlockPos blockPos;

    public SeatEntity(EntityType<SeatEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setBoundingBox(new AABB(0.001F, 0.001F, 0.001F, -0.001F, -0.001F, -0.001F));
        this.setInvisible(true);
    }

    @Override
    public double getEyeY() {
        return 0;
    }

    public SeatEntity(Level worldIn, BlockPos pos, double yOffset) {
        this(ModEntities.SEAT.get(), worldIn);
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
        this.blockPos = pos;
    }


    public void setYOffset(double offset) {
        this.yOffset = offset;
    }

    public void setViaYOffset(BlockPos pos) {
        blockPos = pos;
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        Level level = level();
        if (level instanceof ServerLevel serverLevel && (blockPos == null || !this.hasExactlyOnePlayerPassenger() || level.isEmptyBlock(blockPos))) {
            this.kill(serverLevel);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }


    public LivingEntity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.getFirst() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        Optional<Integer> optionalX = input.getInt("DevicesChairX");
        Optional<Integer> optionalY = input.getInt("DevicesChairY");
        Optional<Integer> optionalZ = input.getInt("DevicesChairZ");

        if (optionalX.isPresent() && optionalY.isPresent() && optionalZ.isPresent()) {
            blockPos = new BlockPos(optionalX.get(), optionalY.get(), optionalZ.get());
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (blockPos == null) return;
        output.putInt("DevicesChairX", blockPos.getX());
        output.putInt("DevicesChairY", blockPos.getY());
        output.putInt("DevicesChairZ", blockPos.getZ());
    }
}
