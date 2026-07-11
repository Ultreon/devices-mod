package dev.ultreon.devices.programs.gitweb.component.container;

import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

/**
 * @author MrCrayfish
 */
public class FurnaceBox extends ContainerBox {
    public static final int HEIGHT = 68;

    private int progressTimer;
    private int fuelTimer;
    private final int fuelTime;

    public FurnaceBox(ItemStack input, ItemStack fuel, ItemStack result) {
        super(0, 0, 0, 68, HEIGHT, new ItemStack(Blocks.FURNACE), "Furnace");
        slots.add(new Slot(26, 8, input));
        slots.add(new Slot(26, 44, fuel));
        slots.add(new Slot(85, 26, result));
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            fuelTime = 0;
            return;
        }
        this.fuelTime = OmnixerioDevices.getInstance().getBurnTime(fuel, RecipeType.SMELTING, level.registryAccess());
    }

    @Override
    protected void handleTick() {
        if (++progressTimer == 200) {
            progressTimer = 0;
        }
        if (--fuelTimer <= 0) {
            fuelTimer = fuelTime;
        }
    }

    @Override
    protected void extractRenderState(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.extractRenderState(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

        int burnProgress = this.getBurnLeftScaled(13);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BOXES_TEXTURE, x + 26, y + 52 - burnProgress, 128, 238 - burnProgress, 14, burnProgress + 1, 256, 256);

        int cookProgress = this.getCookProgressScaled(24);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BOXES_TEXTURE, x + 49, y + 37, 128, 239, cookProgress + 1, 16, 256, 256);
    }

    private int getCookProgressScaled(int pixels) {
        return this.progressTimer * pixels / 200;
    }

    private int getBurnLeftScaled(int pixels) {
        int i = this.fuelTime;
        if (i == 0) {
            i = 200;
        }
        return this.fuelTimer * pixels / i + 1;
    }
}
