package dev.ultreon.devices.core.client;

import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.app.IIcon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * @author MrCrayfish
 */
public class ClientNotification implements Toast {
    private static final Identifier TEXTURE_TOASTS = OmnixerioDevices.id("textures/gui/toast.png");

    private IIcon icon;
    private String title;
    private String subTitle;

    private ClientNotification() {
    }

    public static ClientNotification loadFromTag(CompoundTag tag) {
        return new ClientNotification();
    }

//    @NotNull
//    @Override
//    public Visibility render(@NotNull GuiGraphicsExtractor graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
//        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//        RenderSystem.setShaderTexture(0, TEXTURE_TOASTS);
//        graphics.blit(TEXTURE_TOASTS, 0, 0, 0, 0, 160, 32);
//        Font font = toastComponent.getMinecraft().font;
//
//        if (subTitle == null) {
//            graphics.textRenderer().accept(TextAlignment.RIGHT);
//            graphics.drawString(font, font.plainSubstrByWidth(I18n.get(title), 118), 38, 12, -1);
//        } else {
//            graphics.drawString(font, font.plainSubstrByWidth(I18n.get(title), 118), 38, 7, -1);
//            graphics.drawString(font, font.plainSubstrByWidth(I18n.get(subTitle), 118), 38, 18, -1, false);
//        }
//
//        RenderSystem.setShaderTexture(0, icon.getIconAsset());
//        graphics.blit(icon.getIconAsset(), 6, 6, icon.getGridWidth(), icon.getGridHeight(), icon.getU(), icon.getV(), icon.getIconSize(), icon.getIconSize(), icon.getSourceWidth(), icon.getSourceHeight());
//
//        return timeSinceLastVisible >= 5000L ? Visibility.HIDE : Visibility.SHOW;
//    }
//
//    public static ClientNotification loadFromTag(CompoundTag tag) {
//        ClientNotification notification = new ClientNotification();
//
//        int ordinal = tag.getCompound("icon").getInt("ordinal");
//        String className = tag.getCompound("icon").getString("className");
//
//        try {
//            notification.icon = (IIcon) Class.forName(className).getEnumConstants()[ordinal];
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        notification.title = tag.getString("title");
//        if (tag.contains("subTitle", Tag.TAG_STRING)) {
//            notification.subTitle = tag.getString("subTitle");
//        }
//
//        return notification;
//    }
//
//    public void push() {
//        Minecraft.getInstance().getToasts().addToast(this);
//    }


    @Override
    public @NonNull Visibility getWantedVisibility() {
        return Visibility.HIDE;
    }

    @Override
    public void update(@NonNull ToastManager manager, long fullyVisibleForMs) {

    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull Font font, long fullyVisibleForMs) {
        // TODO
    }

    public void push() {

    }
}
