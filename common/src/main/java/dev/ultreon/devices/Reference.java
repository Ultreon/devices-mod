package dev.ultreon.devices;

import dev.architectury.platform.Platform;

public class Reference {
    public static final String MOD_ID = "omnixerio_devices";
    public static final String MOD_NAME = "Omnixerio Devices";
    public static final String VERSION;
    private static String[] verInfo;
    static {
        VERSION = getVersion();
    }

    public static String getVersion() {
        return Platform.getMod(OmnixerioDevices.MOD_ID).getVersion();
    }

    public static String[] getVerInfo() {
        if (verInfo == null) {
            if (getVersion().split("\\+").length == 1) {
                return verInfo = new String[]{getVersion(), "unknown"};
            }
            var version = getVersion().split("\\+")[0];
            var build = getVersion().split("\\+")[1];
            verInfo = new String[]{version, build};
        }
        return verInfo;
    }
}
