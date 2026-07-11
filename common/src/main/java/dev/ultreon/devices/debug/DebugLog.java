package dev.ultreon.devices.debug;

import dev.architectury.platform.Platform;
import dev.ultreon.devices.OmnixerioDevices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DebugLog {
    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;
    public static final int FATAL = 4;
    private static final Logger LOGGER = LoggerFactory.getLogger("Devices : Debugger");
    private static final Set<UUID> logged = new HashSet<>();

    public static void log(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info(message);
        }
    }

    public static void log(Object... message) {
        log(String.join(" ", Arrays.stream(message).map(Objects::toString).toList()));
    }

    public static void logTime(long ticks, String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info("(@{} ticks) {}", ticks, message);
        }
    }

    public static void logOnce(int level, String id, String message) {
        UUID uuid = UUID.fromString(id);
        if (logged.contains(uuid)) return;
        logged.add(uuid);

        switch (level) {
            case DEBUG -> OmnixerioDevices.LOGGER.debug(message);
            case INFO -> OmnixerioDevices.LOGGER.info(message);
            case WARN -> OmnixerioDevices.LOGGER.warn(message);
            case ERROR, FATAL -> OmnixerioDevices.LOGGER.error(message);
            default -> OmnixerioDevices.LOGGER.error("Unknown log level: {}", level, new Throwable());
        }
    }
}
