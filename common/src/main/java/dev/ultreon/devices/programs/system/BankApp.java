package dev.ultreon.devices.programs.system;

import com.google.common.base.Suppliers;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.Label;
import dev.ultreon.devices.api.app.component.Text;
import dev.ultreon.devices.api.app.component.TextField;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.programs.system.task.TaskDeposit;
import dev.ultreon.devices.programs.system.task.TaskWithdraw;
import dev.ultreon.devices.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("FieldCanBeLocal")
public class BankApp extends Application {
    private static final Supplier<ItemStack> EMERALD = Suppliers.memoize(() -> new ItemStack(Items.EMERALD));
    private static final Identifier BANK_ASSETS = OmnixerioDevices.id("textures/gui/bank.png");
    private Layout layoutStart;
    private Label labelTeller;
    private Text textWelcome;
    private Button btnDepositWithdraw;
    private Button btnTransfer;
    private Layout layoutMain;
    private Label labelBalance;
    private Label labelAmount;
    private TextField amountField;
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private Button btnFive;
    private Button btnSix;
    private Button btnSeven;
    private Button btnEight;
    private Button btnNine;
    private Button btnZero;
    private Button btnClear;
    private Button buttonDeposit;
    private Button buttonWithdraw;
    private Label labelEmeraldAmount;
    private Label labelInventory;
    private int emeraldAmount;
    private int rotation;
    private net.minecraft.world.entity.npc.villager.Villager villager;

    public BankApp() {
    }

    @Override
    public void onTick() {
        super.onTick();
        rotation++;
        if (rotation >= 100) {
            rotation = 0;
        }
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        if (Minecraft.getInstance().level != null) {
            villager = EntityTypes.VILLAGER.create(Minecraft.getInstance().level, EntitySpawnReason.TRIGGERED);
            if (villager != null) {
                villager.setVillagerData(new VillagerData(BuiltInRegistries.VILLAGER_TYPE.getOrThrow(VillagerType.PLAINS), BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NITWIT), 1));
            }
        }

        layoutStart = new Layout();
        layoutStart.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            if (villager != null) {
                InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, x + 2, y + 2, x + 48, y + 78, 30, 0.0625F, mouseX, mouseY, villager);
            }

            RenderUtil.drawRectWithTexture3(BANK_ASSETS, graphics, x + 46, y + 19, 0, 0, 146, 52, 146, 52);
        });

        labelTeller = new Label(ChatFormatting.YELLOW + "Casey The Teller", 60, 7);
        layoutStart.addComponent(labelTeller);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            textWelcome = new Text(ChatFormatting.BLACK + "Hello " + player.getGameProfile().name() + ", welcome to The Emerald Bank! How can I help you?", 62, 25, 125);
        }
        layoutStart.addComponent(textWelcome);

        btnDepositWithdraw = new Button(54, 74, "View Account");
        btnDepositWithdraw.setSize(76, 20);
        btnDepositWithdraw.setToolTip("View Account", "Shows your balance");
        layoutStart.addComponent(btnDepositWithdraw);

        btnTransfer = new Button(133, 74, "Transfer");
        btnTransfer.setSize(58, 20);
        btnTransfer.setToolTip("Transfer", "Withdraw and deposit emeralds");
        btnTransfer.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                setCurrentLayout(layoutMain);
            }
        });
        layoutStart.addComponent(btnTransfer);

        setCurrentLayout(layoutStart);

        layoutMain = new Layout(120, 143) {
            @Override
            public void handleTick() {
                super.handleTick();
                int amount = InventoryUtil.getItemAmount(player, Items.EMERALD);
                labelEmeraldAmount.setText("x " + amount);
            }
        };
        layoutMain.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 40, Color.GRAY.getRGB());
            graphics.fill(x, y + 39, x + width, y + 40, Color.DARK_GRAY.getRGB());
            graphics.fill(x + 62, y + 103, x + 115, y + 138, Color.BLACK.getRGB());
            graphics.fill(x + 63, y + 104, x + 114, y + 113, Color.DARK_GRAY.getRGB());
            graphics.fill(x + 63, y + 114, x + 114, y + 137, Color.GRAY.getRGB());
            RenderUtil.renderItem(graphics, x + 65, y + 118, EMERALD.get(), false);
        });

        labelBalance = new Label("Balance", 60, 5);
        labelBalance.setAlignment(Label.ALIGN_CENTER);
        labelBalance.setShadow(false);
        layoutMain.addComponent(labelBalance);

        labelAmount = new Label("Loading balance...", 60, 18);
        labelAmount.setAlignment(Label.ALIGN_CENTER);
        labelAmount.setScale(2);
        layoutMain.addComponent(labelAmount);

        amountField = new TextField(5, 45, 110);
        amountField.setText("0");
        amountField.setEditable(false);
        layoutMain.addComponent(amountField);

        for (int i = 0; i < 9; i++) {
            int posX = 5 + (i % 3) * 19;
            int posY = 65 + (i / 3) * 19;
            Button button = new Button(posX, posY, Integer.toString(i + 1));
            button.setSize(16, 16);
            addNumberClickListener(button, amountField, i + 1);
            layoutMain.addComponent(button);
        }

        btnZero = new Button(5, 122, "0");
        btnZero.setSize(16, 16);
        addNumberClickListener(btnZero, amountField, 0);
        layoutMain.addComponent(btnZero);

        btnClear = new Button(24, 122, "Clr");
        btnClear.setSize(35, 16);
        btnClear.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                amountField.setText("0");
            }
        });
        layoutMain.addComponent(btnClear);

        buttonDeposit = new Button(62, 65, "Deposit");
        buttonDeposit.setSize(53, 16);
        buttonDeposit.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (amountField.getText().equals("0")) {
                    return;
                }

                try {
                    final int amount = Integer.parseInt(amountField.getText());
                    deposit(amount, (tag, success) -> {
                        if (success) {
                            assert tag != null;
                            int balance = tag.getIntOr("balance", 0);
                            labelAmount.setText("$" + balance);
                            amountField.setText("0");
                        }
                    });
                } catch (NumberFormatException e) {
                    amountField.setText("0");
                    openDialog(new Dialog.Message("Invalid amount. The maximum that you can deposit is " + Integer.MAX_VALUE));
                }
            }
        });
        layoutMain.addComponent(buttonDeposit);

        buttonWithdraw = new Button(62, 84, "Withdraw");
        buttonWithdraw.setSize(53, 16);
        buttonWithdraw.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (amountField.getText().equals("0")) {
                    return;
                }

                try {
                    final int amount = Integer.parseInt(amountField.getText());
                    withdraw(amount, (tag, success) -> {
                        if (success) {
                            assert tag != null;
                            int balance = tag.getIntOr("balance", 0);
                            labelAmount.setText("$" + balance);
                            amountField.setText("0");
                        }
                    });
                } catch (NumberFormatException e) {
                    amountField.setText("0");
                    openDialog(new Dialog.Message("Invalid amount. The maximum that you can withdraw is " + Integer.MAX_VALUE));
                }
            }
        });
        layoutMain.addComponent(buttonWithdraw);

        labelEmeraldAmount = new Label("x 0", 83, 123);
        layoutMain.addComponent(labelEmeraldAmount);

        labelInventory = new Label("Wallet", 74, 105);
        labelInventory.setShadow(false);
        layoutMain.addComponent(labelInventory);

        BankUtil.getBalance((tag, success) -> {
            if (success) {
                assert tag != null;
                int balance = tag.getIntOr("balance", 0);
                labelAmount.setText("$" + balance);
            }
        });
    }

    public void addNumberClickListener(Button btn, final TextField field, final int number) {
        btn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (!(field.getText().equals("0") && number == 0)) {
                    if (field.getText().equals("0")) field.clear();
                    field.writeText(Integer.toString(number));
                }
            }
        });
    }

    private void deposit(int amount, Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskDeposit(amount).setCallback(callback));
    }

    private void withdraw(int amount, Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskWithdraw(amount).setCallback(callback));
    }

    private static EntityRenderState extractRenderState(LivingEntity entity) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        return renderState;
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }
}
