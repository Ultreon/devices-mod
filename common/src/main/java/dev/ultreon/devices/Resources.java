package dev.ultreon.devices;

import net.minecraft.resources.Identifier;

public final class Resources {
    private Resources() {
        throw new UnsupportedOperationException("Instantiating utility class");
    }


    public static final Identifier ENDER_MAIL_ICONS = OmnixerioDevices.id("textures/gui/ender_mail.png");
    public static final Identifier ENDER_MAIL_BACKGROUND = OmnixerioDevices.id("textures/gui/ender_mail_background.png");
}
