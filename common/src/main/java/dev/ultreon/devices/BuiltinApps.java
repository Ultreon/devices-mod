package dev.ultreon.devices;

import dev.architectury.platform.Platform;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.programs.BoatRacersApp;
import dev.ultreon.devices.programs.NoteStashApp;
import dev.ultreon.devices.programs.PixelPainterApp;
import dev.ultreon.devices.programs.auction.MineBayApp;
import dev.ultreon.devices.programs.email.EmailApp;
import dev.ultreon.devices.programs.gitweb.GitWebApp;
import dev.ultreon.devices.programs.snake.SnakeApp;
import dev.ultreon.devices.programs.system.*;
import dev.ultreon.devices.programs.themes.ThemesApp;

public class BuiltinApps {
    public static void registerBuiltinApps() {
        ApplicationManager.registerApplication(OmnixerioDevices.id("diagnostics"), () -> DiagnosticsApp::new, true);
        ApplicationManager.registerApplication(OmnixerioDevices.id("settings"), () -> SettingsApp::new, true);
        ApplicationManager.registerApplication(OmnixerioDevices.id("file_browser"), () -> FileBrowserApp::new, true);
        ApplicationManager.registerApplication(OmnixerioDevices.id("gitweb"), () -> GitWebApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevices.id("note_stash"), () -> NoteStashApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevices.id("pixel_painter"), () -> PixelPainterApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevices.id("ender_mail"), () -> EmailApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevices.id("app_store"), () -> AppStore::new, true);

        if (Platform.isDevelopmentEnvironment() || OmnixerioDevices.EARLY_CONFIG.enableBetaApps) {
            ApplicationManager.registerApplication(OmnixerioDevices.id("bank"), () -> BankApp::new, false);
            ApplicationManager.registerApplication(OmnixerioDevices.id("boat_racers"), () -> BoatRacersApp::new, false);
            ApplicationManager.registerApplication(OmnixerioDevices.id("mine_bay"), () -> MineBayApp::new, false);
            ApplicationManager.registerApplication(OmnixerioDevices.id("snake"), () -> SnakeApp::new, false);
            ApplicationManager.registerApplication(OmnixerioDevices.id("themes"), () -> ThemesApp::new, false);
        }
    }
}
