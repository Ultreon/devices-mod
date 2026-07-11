package dev.ultreon.devices.programs.auction;

import com.google.common.base.Suppliers;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.component.*;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.Label;
import dev.ultreon.devices.api.app.renderer.ListItemRenderer;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.programs.auction.object.AuctionItem;
import dev.ultreon.devices.programs.auction.task.TaskAddAuction;
import dev.ultreon.devices.programs.auction.task.TaskBuyItem;
import dev.ultreon.devices.programs.auction.task.TaskGetAuctions;
import dev.ultreon.devices.programs.system.layout.StandardLayout;
import dev.ultreon.devices.util.TimeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class MineBayApp extends Application {
    private static final Identifier CHEST_GUI_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final Identifier MINEBAY_ASSETS = OmnixerioDevices.id("textures/gui/minebay.png");

    private static final Supplier<ItemStack> EMERALD = Suppliers.memoize(() -> new ItemStack(Items.EMERALD));
    public static final MutableComponent SElECT_AN_ITEM = Component.literal("Select an Item...");
    public static final MutableComponent SET_AMOUNT_AND_PRICE = Component.literal("Set amount and price...");
    public static final MutableComponent SET_DURATION = Component.literal("Set duration...");
    private static Label labelMoney;

    private final String[] categories = {"Building", "Combat", "Tools", "Food", "Materials", "Redstone", "Alchemy", "Rare", "Misc"};

    private Layout layoutMyAuctions;
    private ItemList<AuctionItem> items;

    /* Add Item Layout */
    private Layout layoutSelectItem;
    private Inventory inventory;
    private Button buttonAddCancel;
    private Button buttonAddNext;

    /* Set Amount and Price Layout */
    private Layout layoutAmountAndPrice;
    private Label labelAmount;
    private NumberSelector selectorAmount;
    private Label labelPrice;
    private NumberSelector selectorPrice;
    private Button buttonAmountAndPriceBack;
    private Button buttonAmountAndPriceCancel;
    private Button buttonAmountAndPriceNext;

    /* Set Duration Layout */
    private Layout layoutDuration;
    private Label labelHours;
    private Label labelMinutes;
    private Label labelSeconds;
    private NumberSelector selectorHours;
    private NumberSelector selectorMinutes;
    private NumberSelector selectorSeconds;
    private Button buttonDurationBack;
    private Button buttonDurationCancel;
    private Button buttonDurationAdd;
    private StandardLayout layoutMain;

    public MineBayApp() {
        //super(Reference.MOD_ID + "MineBay", "MineBay");
    }

    private static void balanceCallback(CompoundTag nbt, boolean success) {
        if (success) {
            labelMoney.setText("$" + Objects.requireNonNull(nbt, "Expected to get a requestData from the get-balance task response.").getInt("balance"));
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        AuctionManager.INSTANCE.tick();
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        // Set up layouts and components
        createMainLayout();
        createSelectItemLayout();
        createAmountAndPriceLayout();
        createDurationLayout(intent, layoutMain);

        // Set the initial layout
        setInitialLayout(layoutMain);

        // Retrieve the data needed for display
        retrieveData();
    }

    private void retrieveData() {
        // Retrieve the balance
        BankUtil.getBalance(MineBayApp::balanceCallback);

        // Retrieve the auctions
        TaskGetAuctions task = new TaskGetAuctions();
        task.setCallback(this::auctionCallback);
        TaskManager.sendTask(task);
    }

    private void setInitialLayout(StandardLayout layoutMain) {
        setCurrentLayout(layoutMain);
        getCurrentLayout().setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 25, Color.GRAY.getRGB());
            graphics.fill(x, y + 24, x + width, y + 25, Color.DARK_GRAY.getRGB());
            graphics.fill(x, y + 25, x + 95, y + height, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x + 94, y + 25, x + 95, y + height, Color.GRAY.getRGB());

            RenderUtil.drawRectWithTexture3(MINEBAY_ASSETS, graphics, x + 5, y + 6, 0, 0, 61, 11, 61, 12);
        });
    }

    private @NonNull StandardLayout createMainLayout() {
        layoutMain = new StandardLayout(ChatFormatting.BOLD + "Icons", 330, 153, this, null);

        layoutMain.addComponent(createMainAddItem());
        layoutMain.addComponent(createMainYourAuctionsButton());
        layoutMain.addComponent(createMainBalanceLabel());
        layoutMain.addComponent(createMainMoneyLabel());
        layoutMain.addComponent(createMainCategoriesLabel());
        layoutMain.addComponent(createMainCategoriesList());
        layoutMain.addComponent(createMainItemsLabel());
        layoutMain.addComponent(createMainAuctionsList());
        layoutMain.addComponent(createMainBuyButton());
        return layoutMain;
    }

    private @NonNull Button createMainAddItem() {
        Button btnAddItem = new Button(70, 5, "Add Item");
        btnAddItem.setSize(60, 15);
        btnAddItem.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutSelectItem));
        return btnAddItem;
    }

    private void createDurationLayout(@org.jspecify.annotations.Nullable CompoundTag intent, StandardLayout layoutMain) {
        layoutDuration = new Layout(172, 87);
        layoutDuration.setTitle("Add Item");
        layoutDuration.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
            graphics.textRenderer().accept(x + 5, y + 7, SET_DURATION);
        });

        buttonDurationBack = new Button(122, 4, MINEBAY_ASSETS, 8, 12, 8, 8);
        buttonDurationBack.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutAmountAndPrice));
        layoutDuration.addComponent(buttonDurationBack);

        buttonDurationCancel = new Button(138, 4, MINEBAY_ASSETS, 0, 12, 8, 8);
        buttonDurationCancel.setClickListener((mouseX, mouseY, mouseButton) -> this.setCurrentLayout(layoutMain));
        layoutDuration.addComponent(buttonDurationCancel);

        buttonDurationAdd = new Button(154, 4, MINEBAY_ASSETS, 24, 12, 8, 8);
        buttonDurationAdd.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            final Dialog.Confirmation dialog = new Dialog.Confirmation();
            dialog.setMessageText("Are you sure you want to auction this item?");
            dialog.setPositiveText("Yes");
            dialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) ->
            {
                int ticks = (int) TimeUtil.getRealTimeToTicks(selectorHours.getNumber(), selectorMinutes.getNumber(), selectorSeconds.getNumber());
                TaskAddAuction task = new TaskAddAuction(inventory.getSelectedSlotIndex(), selectorAmount.getNumber(), selectorPrice.getNumber(), ticks);
                task.setCallback((nbt, success) ->
                {
                    if (success) {
                        List<AuctionItem> auctionItems = AuctionManager.INSTANCE.getItems();
                        items.addItem(auctionItems.getLast());
                    }
                });
                TaskManager.sendTask(task);
                dialog.close();
                init(intent);
            });
            openDialog(dialog);
        });
        layoutDuration.addComponent(buttonDurationAdd);

        labelHours = new Label("Hrs", 45, 30);
        layoutDuration.addComponent(labelHours);

        labelMinutes = new Label("Mins", 76, 30);
        layoutDuration.addComponent(labelMinutes);

        labelSeconds = new Label("Secs", 105, 30);
        layoutDuration.addComponent(labelSeconds);

        DecimalFormat format = new DecimalFormat("00");

        selectorHours = new NumberSelector(45, 42, 20);
        selectorHours.setMax(23);
        selectorHours.setMin(0);
        selectorHours.setFormat(format);
        layoutDuration.addComponent(selectorHours);

        selectorMinutes = new NumberSelector(76, 42, 20);
        selectorMinutes.setMax(59);
        selectorMinutes.setMin(0);
        selectorMinutes.setFormat(format);
        layoutDuration.addComponent(selectorMinutes);

        selectorSeconds = new NumberSelector(107, 42, 20);
        selectorSeconds.setMax(59);
        selectorSeconds.setMin(1);
        selectorSeconds.setFormat(format);
        layoutDuration.addComponent(selectorSeconds);
    }

    private void createAmountAndPriceLayout() {
        layoutAmountAndPrice = new Layout(172, 87);
        layoutAmountAndPrice.setTitle("Add Item");
        layoutAmountAndPrice.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
            graphics.textRenderer().accept(x + 5, y + 7, SET_AMOUNT_AND_PRICE);

            int offsetX = 14;
            int offsetY = 40;
            graphics.fill(x + offsetX, y + offsetY, x + offsetX + 38, y + offsetY + 38, Color.BLACK.getRGB());
            graphics.fill(x + offsetX + 1, y + offsetY + 1, x + offsetX + 37, y + offsetY + 37, Color.DARK_GRAY.getRGB());

            offsetX = 90;
            graphics.fill(x + offsetX, y + offsetY, x + offsetX + 38, y + offsetY + 38, Color.BLACK.getRGB());
            graphics.fill(x + offsetX + 1, y + offsetY + 1, x + offsetX + 37, y + offsetY + 37, Color.DARK_GRAY.getRGB());

            if (inventory.getSelectedSlotIndex() != -1) {
                assert mc.player != null;
                ItemStack stack = mc.player.getInventory().getItem(inventory.getSelectedSlotIndex());
                if (!stack.isEmpty()) {
                    graphics.pose().pushMatrix();
                    {
                        graphics.pose().translate(x + 17, y + 43);
                        graphics.pose().scale(2, 2);
                        RenderUtil.renderItem(graphics, 0, 0, stack, false);
                    }
                    graphics.pose().popMatrix();
                }
            }

            graphics.pose().pushMatrix();
            {
                graphics.pose().translate(x + 92, y + 43);
                graphics.pose().scale(2, 2);
                RenderUtil.renderItem(graphics, 0, 0, EMERALD.get(), false);
            }
            graphics.pose().popMatrix();
        });

        buttonAmountAndPriceBack = new Button(122, 4, MINEBAY_ASSETS, 8, 12, 8, 8);
        buttonAmountAndPriceBack.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutSelectItem));
        layoutAmountAndPrice.addComponent(buttonAmountAndPriceBack);

        buttonAmountAndPriceCancel = new Button(138, 4, MINEBAY_ASSETS, 0, 12, 8, 8);
        buttonAmountAndPriceCancel.setClickListener((mouseX, mouseY, mouseButton) -> restoreDefaultLayout());
        layoutAmountAndPrice.addComponent(buttonAmountAndPriceCancel);

        buttonAmountAndPriceNext = new Button(154, 4, MINEBAY_ASSETS, 16, 12, 8, 8);
        buttonAmountAndPriceNext.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutDuration));
        layoutAmountAndPrice.addComponent(buttonAmountAndPriceNext);

        labelAmount = new Label("Amount", 16, 30);
        layoutAmountAndPrice.addComponent(labelAmount);

        selectorAmount = new NumberSelector(55, 42, 18);
        selectorAmount.setMax(64);
        layoutAmountAndPrice.addComponent(selectorAmount);

        labelPrice = new Label("Price", 96, 30);
        layoutAmountAndPrice.addComponent(labelPrice);

        selectorPrice = new NumberSelector(131, 42, 24);
        selectorPrice.setMax(999);
        layoutAmountAndPrice.addComponent(selectorPrice);
    }

    private void createAddNextButton() {
        buttonAddNext = new Button(154, 4, MINEBAY_ASSETS, 16, 12, 8, 8);
        buttonAddNext.setToolTip("Next Page", "Set price and amount");
        buttonAddNext.setEnabled(false);
        buttonAddNext.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            selectorAmount.updateButtons();
            selectorPrice.updateButtons();
            setCurrentLayout(layoutAmountAndPrice);
        });
        layoutSelectItem.addComponent(buttonAddNext);
    }

    private void createAddCancelButton() {
        buttonAddCancel = new Button(138, 4, MINEBAY_ASSETS, 0, 12, 8, 8);
        buttonAddCancel.setToolTip("Cancel", "Go back to main page");
        buttonAddCancel.setClickListener((mouseX, mouseY, mouseButton) -> restoreDefaultLayout());
        layoutSelectItem.addComponent(buttonAddCancel);
    }

    private void createInventory() {
        inventory = new Inventory(5, 28);
        inventory.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (inventory.getSelectedSlotIndex() != -1) {
                assert Minecraft.getInstance().player != null;
                ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(inventory.getSelectedSlotIndex());
                if (!stack.isEmpty()) {
                    buttonAddNext.setEnabled(true);
                    selectorAmount.setMax(stack.getCount());
                    selectorAmount.setNumber(stack.getCount());
                } else {
                    buttonAddNext.setEnabled(false);
                }
            }
        });
        layoutSelectItem.addComponent(inventory);
    }

    private void createSelectItemLayout() {
        layoutSelectItem = new Layout(172, 87);
        layoutSelectItem.setTitle("Add Item");
        layoutSelectItem.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
            graphics.textRenderer().accept(x + 5, y + 7, SElECT_AN_ITEM);
        });

        createInventory();
        createAddCancelButton();
        createAddNextButton();
    }

    private static @NonNull Label createMainItemsLabel() {
        Label labelItems = new Label("Items", 100, 29);
        labelItems.setShadow(false);
        return labelItems;
    }

    private ItemList<AuctionItem> createMainAuctionsList() {
        items = new ItemList<>(100, 40, 180, 4);
        items.setListItemRenderer(new ListItemRenderer<>(20) {
            @Override
            public void render(GuiGraphicsExtractor graphics, AuctionItem e, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                if (selected) {
                    graphics.fill(x, y, x + width, y + height, Color.DARK_GRAY.getRGB());
                } else {
                    graphics.fill(x, y, x + width, y + height, Color.GRAY.getRGB());
                }

                RenderUtil.renderItem(graphics, x + 2, y + 2, e.getStack(), true);

                graphics.pose().pushMatrix();
                {
                    graphics.pose().translate(x + 24, y + 4);
                    graphics.pose().scale(0.666f, 0.666f);
                    graphics.textRenderer().accept(TextAlignment.LEFT, 0, 0, e.getStack().getDisplayName());
                    graphics.textRenderer().accept(TextAlignment.LEFT, 0, 11, Component.literal(TimeUtil.getTotalRealTime(e.getTimeLeft())).withColor(Color.LIGHT_GRAY.getRGB()));
                }
                graphics.pose().popMatrix();

                MutableComponent price = Component.literal("$" + e.getPrice()).withColor(Color.YELLOW.getRGB());
                graphics.textRenderer().accept(TextAlignment.RIGHT, x + width - 5, y + 6, price);
            }
        });
        return items;
    }

    private @NonNull Button createMainBuyButton() {
        Button btnBuy = new Button(100, 127, "Buy");
        btnBuy.setSize(50, 15);
        btnBuy.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            final Dialog.Confirmation dialog = new Dialog.Confirmation();
            dialog.setPositiveText("Buy");
            dialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
                final int index = items.getSelectedIndex();
                if (index == -1) return;

                AuctionItem item = items.getItem(index);
                if (item != null) {
                    TaskBuyItem task = new TaskBuyItem(item.getId());
                    task.setCallback((nbt, success) ->
                    {
                        if (success) {
                            items.removeItem(index);
                        }
                    });
                    TaskManager.sendTask(task);
                }
            });
            dialog.setNegativeText("Cancel");
            dialog.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> dialog.close());
            MineBayApp.this.openDialog(dialog);
        });
        return btnBuy;
    }

    private @NonNull ItemList<String> createMainCategoriesList() {
        ItemList<String> categories = new ItemList<>(5, 40, 70, 7);
        for (String category : this.categories) {
            categories.addItem(category);
        }
        return categories;
    }

    private static @NonNull Label createMainCategoriesLabel() {
        Label labelCategories = new Label("Categories", 5, 29);
        labelCategories.setShadow(false);
        return labelCategories;
    }

    private static @NonNull Label createMainMoneyLabel() {
        labelMoney = new Label("$0", 295, 13);
        labelMoney.setAlignment(Label.ALIGN_RIGHT);
        labelMoney.setScale(1);
        labelMoney.setShadow(false);
        return labelMoney;
    }

    private static @NonNull Label createMainBalanceLabel() {
        Label labelBalance = new Label("Balance", 295, 3);
        labelBalance.setAlignment(Label.ALIGN_RIGHT);
        return labelBalance;
    }

    private @NonNull Button createMainYourAuctionsButton() {
        Button btnViewItem = new Button(135, 5, "Your Auctions");
        btnViewItem.setSize(80, 15);
        btnViewItem.setClickListener((mouseX, mouseY, mouseButton) -> {
            assert Minecraft.getInstance().player != null;
            TaskGetAuctions task = new TaskGetAuctions(Minecraft.getInstance().player.getUUID());
            task.setCallback((nbt, success) -> {
                auctionCallback(null, false);
            });
            TaskManager.sendTask(task);
        });
        return btnViewItem;
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {

    }

    private void auctionCallback(CompoundTag nbt, boolean success) {
        items.removeAll();
        for (AuctionItem item : AuctionManager.INSTANCE.getItems()) {
            items.addItem(item);
        }
    }
}
