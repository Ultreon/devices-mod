package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.init.ModBlockEntities;
import dev.ultreon.devices.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import static dev.ultreon.devices.block.entity.PrinterBlockEntity.State.*;

/**
 * @author MrCrayfish
 */
public class PrinterBlockEntity extends NetworkDeviceBlockEntity.Colored {
    private State state = IDLE;

    private final Deque<IPrint> printQueue = new ArrayDeque<>();
    private IPrint currentPrint;

    private int totalPrintTime;
    private int remainingPrintTime;
    private int paperCount = 0;

    public PrinterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.PRINTER.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        assert level != null;
        if (!level.isClientSide()) {
            if (remainingPrintTime > 0) {
                if (remainingPrintTime % 20 == 0 || state == LOADING_PAPER) {
                    pipeline.putInt("remainingPrintTime", remainingPrintTime);
                    sync();
                    if (remainingPrintTime != 0 && state == PRINTING) {
                        level.playSound(null, worldPosition, ModSounds.PRINTER_PRINTING.get(), SoundSource.BLOCKS, 0.5f, 1f);
                    }
                }
                remainingPrintTime--;
            } else {
                setState(state.next());
            }
        }

        if (state == IDLE && remainingPrintTime == 0 && currentPrint != null) {
            if (!level.isClientSide()) {
//                BlockState state = level.getBlockState(worldPosition);
//                double[] fixedPosition = CollisionHelper.fixRotation(state.getValue(PrinterBlock.FACING), 0.15, 0.5, 0.15, 0.5);
                ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 0.0625, worldPosition.getZ(), IPrint.generateItem(currentPrint));
                entity.setDeltaMovement(new Vec3(0, 0, 0));
                level.addFreshEntity(entity);
            }
            currentPrint = null;
        }

        if (state == IDLE && currentPrint == null && !printQueue.isEmpty() && paperCount > 0) {
            print(printQueue.poll());
        }
    }

    @Override
    public String getDeviceName() {
        return "Printer";
    }

    @Override
    public void loadAdditional(ValueInput in) {
        super.loadAdditional(in);

        // Get optional values
        Optional<ValueInput> optionalCurrentPrint = in.child("currentPrint");
        Optional<Integer> optionalTotalPrintTime = in.getInt("totalPrintTime");
        Optional<Integer> optionalRemainingPrintTime = in.getInt("remainingPrintTime");
        Optional<Integer> optionalState = in.getInt("state");
        Optional<Integer> optionalPaperCount = in.getInt("paperCount");
        Optional<ValueInput.ValueInputList> optionalQueue = in.childrenList("queue");

        // Set values when present
        optionalCurrentPrint.ifPresent(currentPrint -> this.currentPrint = IPrint.readInput(currentPrint));
        optionalTotalPrintTime.ifPresent(totalPrintTime -> this.totalPrintTime = totalPrintTime);
        optionalRemainingPrintTime.ifPresent(remainingPrintTime -> this.remainingPrintTime = remainingPrintTime);
        optionalState.ifPresent(state -> {
            if (state < 0 || state >= State.values().length) state = 0;
            this.state = State.values()[state];
        });
        optionalPaperCount.ifPresent(paperCount -> this.paperCount = paperCount);
        optionalQueue.ifPresent(queue -> {
            for (ValueInput print : queue) {
                IPrint print1 = IPrint.readInput(print);
                printQueue.offer(print1);
            }
        });
    }

    @Override
    public void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("totalPrintTime", totalPrintTime);
        out.putInt("remainingPrintTime", remainingPrintTime);
        out.putInt("state", state.ordinal());
        out.putInt("paperCount", paperCount);
        if (currentPrint != null) {
            IPrint.store(currentPrint, out.child("currentPrint"));
        }
        if (!printQueue.isEmpty()) {
            ValueOutput.ValueOutputList queue = out.childrenList("queue");
            for (IPrint print : printQueue) {
                IPrint.store(print, queue.addChild());
            }
        }
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
        tag.putInt("paperCount", paperCount);
        return tag;
    }

    public void setState(State newState) {
        if (newState == null) return;

        state = newState;
        if (state == PRINTING) {
            if (DeviceConfig.OVERRIDE_PRINT_SPEED.get()) {
                remainingPrintTime = DeviceConfig.CUSTOM_PRINT_SPEED.get() * 20;
            } else {
                remainingPrintTime = currentPrint.speed() * 20;
            }
        } else {
            remainingPrintTime = state.animationTime;
        }
        totalPrintTime = remainingPrintTime;

        pipeline.putInt("state", state.ordinal());
        pipeline.putInt("totalPrintTime", totalPrintTime);
        pipeline.putInt("remainingPrintTime", remainingPrintTime);
        sync();
    }

    public void addToQueue(IPrint print) {
        printQueue.offer(print);
    }

    private void print(IPrint print) {
        assert level != null;
        level.playSound(null, worldPosition, ModSounds.PRINTER_LOADING_PAPER.get(), SoundSource.BLOCKS, 0.5f, 1f);

        setState(LOADING_PAPER);
        currentPrint = print;
        paperCount--;

        pipeline.putInt("paperCount", paperCount);
        pipeline.put("currentPrint", IPrint.save(currentPrint));
        sync();
    }

    public boolean isLoading() {
        return state == LOADING_PAPER;
    }

    public boolean isPrinting() {
        return state == PRINTING;
    }

    public int getTotalPrintTime() {
        return totalPrintTime;
    }

    public int getRemainingPrintTime() {
        return remainingPrintTime;
    }

    public boolean addPaper(ItemStack stack, boolean addAll) {
        if (!stack.isEmpty() && stack.getItem() == Items.PAPER && paperCount < DeviceConfig.MAX_PAPER_COUNT.get()) {
            if (!addAll) {
                paperCount++;
                stack.shrink(1);
            } else {
                paperCount += stack.getCount();
                stack.setCount(Math.max(0, paperCount - 64));
                paperCount = Math.min(64, paperCount);
            }
            pipeline.putInt("paperCount", paperCount);
            sync();
            assert level != null;
            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_BREAK, SoundSource.BLOCKS, 1f, 1f);
            return true;
        }
        return false;
    }

    public boolean hasPaper() {
        return paperCount > 0;
    }

    public int getPaperCount() {
        return paperCount;
    }

    public IPrint getPrint() {
        return currentPrint;
    }

    public enum State {
        LOADING_PAPER(30), PRINTING(0), IDLE(0);

        final int animationTime;

        State(int time) {
            this.animationTime = time;
        }

        public State next() {
            if (ordinal() + 1 >= values().length) return null;
            return values()[ordinal() + 1];
        }
    }
}
