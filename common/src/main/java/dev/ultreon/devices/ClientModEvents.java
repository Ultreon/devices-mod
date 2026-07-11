package dev.ultreon.devices;

import com.mojang.blaze3d.platform.NativeImage;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.block.entity.renderer.*;
import dev.ultreon.devices.client.RenderRegistry;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.debug.DebugFlags;
import dev.ultreon.devices.debug.DebugUtils;
import dev.ultreon.devices.debug.DumpType;
import dev.ultreon.devices.init.Battery;
import dev.ultreon.devices.init.ModBlockEntities;
import dev.ultreon.devices.init.ModDataComponents;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.system.object.ColorSchemePresets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class ClientModEvents {
    private static final Marker SETUP = MarkerFactory.getMarker("SETUP");
    private static final Logger LOGGER = OmnixerioDevices.LOGGER;

    public static void clientSetup() {
        LOGGER.info("Doing some client setup.");

        if (OmnixerioDevices.DEVELOPER_MODE) {
            LOGGER.info(SETUP, "Adding developer wallpaper.");
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/developer_wallpaper.png"));
        } else {
            LOGGER.info(SETUP, "Adding default wallpapers.");
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_1.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_2.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_3.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_4.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_5.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_6.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_7.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_8.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_9.png"));
            Laptop.addWallpaper(OmnixerioDevices.id("textures/gui/laptop_wallpaper_10.png"));
        }


        // Register other stuff.
        registerRenderLayers();
        registerLayerDefinitions();

        registerOSContent();

        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new ReloaderListener(), OmnixerioDevices.id("reloader"));
    }

    private static void registerOSContent() {
        ColorSchemePresets.init();
    }

    @ApiStatus.Internal
    public static class ReloaderListener implements PreparableReloadListener {
        @Override
        public @NonNull CompletableFuture<Void> reload(@NonNull SharedState currentReload, @NonNull Executor taskExecutor, PreparationBarrier preparationBarrier, @NonNull Executor reloadExecutor) {
            LOGGER.debug("Reloading resources from the Device Mod.");

            return preparationBarrier.wait(CompletableFuture.<Void>completedFuture(null))
                    .thenCompose(_ -> CompletableFuture.runAsync(() -> {
                        try {
                            if (!ApplicationManager.getAllApplications().isEmpty()) {
                                ApplicationManager.getAllApplications().forEach(AppInfo::reload);
                                generateIconAtlas(currentReload.resourceManager());
                            }
                        } catch (Exception e) {
                            OmnixerioDevices.LOGGER.error("Failed to reload application icons", e);
                        }
                    }, reloadExecutor));
        }
    }

    private static void registerRenderLayers() {
        OmnixerioDevices.LOGGER.warn("Registering render layers is not yet implemented.");
//        if (true) return;
//        DeviceBlocks.getAllLaptops().forEach(block -> {
//            LOGGER.debug(SETUP, "Setting render layer for laptop {}", RegistrarManager.getId(block, Registries.BLOCK));
//            RenderTypeRegistry.register(RenderType.cutout(), block);
//        });
//
//        DeviceBlocks.getAllPrinters().forEach(block -> {
//            LOGGER.debug(SETUP, "Setting render layer for printer {}", RegistrarManager.getId(block, Registries.BLOCK));
//            RenderTypeRegistry.register(RenderType.cutout(), block);
//        });
//
//        DeviceBlocks.getAllRouters().forEach(block -> {
//            LOGGER.debug(SETUP, "Setting render layer for router {}", RegistrarManager.getId(block, Registries.BLOCK));
//            RenderTypeRegistry.register(RenderType.cutout(), block);
//        });
//
//        LOGGER.debug(SETUP, "Setting render layer for paper {}", RegistrarManager.getId(DeviceBlocks.PAPER.get(), Registries.BLOCK));
//        RenderTypeRegistry.register(RenderType.cutout(), DeviceBlocks.PAPER.get());
    }

    public static void generateIconAtlas(ResourceManager resourceManager) {
        final int ICON_SIZE = 14;
        var imageWriter = new Object() {
            final BufferedImage atlas = new BufferedImage(ICON_SIZE * 16, ICON_SIZE * 16, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = atlas.createGraphics();
            int index = 0;
            int mode = 0;
            ResourceManager rm = resourceManager;

            public boolean writeImage(AppInfo info, Identifier location, boolean silent) {
                try {
                    if (rm == null) {
                        rm = Minecraft.getInstance().getResourceManager();
                    }
                    InputStream input = null;
                    try {
                        input = getClass().getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath());
                    } catch (Exception ignored) {
                    }
                    if (input == null) {
                        Resource resource = rm.getResource(location).orElse(null);
                        if (resource == null) {
                            if (silent) return false;
                            throw new FileNotFoundException("Resource for " + location + " wasn't found");
                        }
                        input = resource.open();
                    }
                    BufferedImage icon = ImageIO.read(input);
                    if (icon.getWidth() != ICON_SIZE || icon.getHeight() != ICON_SIZE) {
                        OmnixerioDevices.LOGGER.error("Incorrect icon size for {} (Must be 14 by 14 pixels)", info == null ? null : info.getAppId());
                        return false;
                    }
                    int iconU = (index % 16) * ICON_SIZE;
                    int iconV = (index / 16) * ICON_SIZE;
                    g.drawImage(icon, iconU, iconV, ICON_SIZE, ICON_SIZE, null);
                    if (info != null) {
                        AppInfo.Icon.Glyph glyph = switch (mode) {
                            case 0 -> info.getIcon().getBase();
                            case 1 -> info.getIcon().getOverlay0();
                            case 2 -> info.getIcon().getOverlay1();
                            default -> throw new IllegalStateException("Unexpected value: " + mode);
                        };
                        glyph.setU(iconU);
                        glyph.setV(iconV);
                    }
                    index++;
                    if (DebugFlags.LOG_APP_ICON_STITCHES) {
                        OmnixerioDevices.LOGGER.info("Stitching texture: {}", location);
                    }
                    return true;
                } catch (FileNotFoundException e) {
                    if (silent) return false;
                    OmnixerioDevices.LOGGER.error("Unable to load icon for '{}': {}", info == null ? null : info.getAppId(), e.getMessage(), e);
                } catch (Exception e) {
                    if (silent) return false;
                    OmnixerioDevices.LOGGER.error("Unable to load icon for {}", info == null ? null : info.getAppId(), e);
                }
                return false;
            }

            public void finish() {
                g.dispose();

                if (DebugFlags.DUMP_APP_ICON_ATLAS) {
                    try {
                        DebugUtils.dump(DumpType.ATLAS, Laptop.ICON_TEXTURES, (stream) -> ImageIO.write(atlas, "png", stream));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    ImageIO.write(atlas, "png", output);
                    byte[] bytes = output.toByteArray();
                    if (Platform.isDevelopmentEnvironment())
                        Files.write(Paths.get("app_icons.png"), bytes);
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    Minecraft.getInstance().submit(() -> {
                        try {
                            Minecraft.getInstance().getTextureManager().register(Laptop.ICON_TEXTURES, new DynamicTexture(() -> "laptop_app_icons", NativeImage.read(input)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        imageWriter.writeImage(null, OmnixerioDevices.id("textures/app/icon/base/missing.png"), false);

        for (AppInfo info : ApplicationManager.getAllApplications()) {
            if (info.getIcon() == null) continue;

            //Identifier identifier = info.getId();
            //Identifier iconResource = new Identifier(info.getIcon());
            imageWriter.mode = 0;
            imageWriter.writeImage(info, info.getIcon().getBase().getIdentifier(), false);
            imageWriter.mode = 1;
            imageWriter.writeImage(info, info.getIcon().getOverlay0().getIdentifier(), true);
            imageWriter.mode = 2;
            imageWriter.writeImage(info, info.getIcon().getOverlay1().getIdentifier(), true);
        }
        imageWriter.mode = 0;
        imageWriter.finish();
    }

//    @ExpectPlatform
//    private static void.json updateIcon(AppInfo info, int iconU, int iconV) {
//        throw new AssertionError();

    /// /        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
    /// /        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
//    }
    public static void setRenderLayer(Block block, RenderType type) {
        RenderRegistry.register(block, type
        );
    }

    public static void registerRenderers() {
        LOGGER.info("Registering renderers.");

        BlockEntityRendererRegistry.register(ModBlockEntities.LAPTOP.get(), LaptopRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.PRINTER.get(), PrinterRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.PAPER.get(), PaperRenderer::new);
//        BlockEntityRendererRegistry.register(ModBlockEntities.ROUTER.get(), RouterRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.SEAT.get(), OfficeChairRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.CLOCK.get(), ClockRenderer::new);
    }

    public static void registerItemProperties() {
        LOGGER.info("Registering item properties.");

        // Register the item properties.
//        ItemPropertiesAccessor.register(
//                ModItems.BATTERY_CELL.get(),
//                OmnixerioDevicesMod.id("charge"),
//                (stack, level, livingEntity, i) -> {
//                    float chargeRatio = getChargeRatio(stack);
//                    if (chargeRatio <= 0) {
//                        return -1.0f;
//                    }
//                    return chargeRatio;
//                });
//        ItemPropertiesAccessor.register(
//                ModItems.BATTERY_CELL.get(),
//                OmnixerioDevicesMod.id("overcharged"),
//                (stack, level, livingEntity, i) -> {
//                    if (getChargeRatio(stack) > 1f) {
//                        return 1f;
//                    }
//                    return 0f;
//                });
//        ItemPropertiesAccessor.register(
//                ModItems.BATTERY_CELL.get(),
//                OmnixerioDevicesMod.id("empty"),
//                (stack, level, livingEntity, i) -> {
//                    if (getChargeRatio(stack) == 0f) {
//                        return 1f;
//                    }
//                    return 0f;
//                });
//        ItemPropertiesAccessor.register(
//                ModItems.BATTERY_CELL.get(),
//                OmnixerioDevicesMod.id("broken"),
//                (stack, level, livingEntity, i) -> {
//                    if (getChargeRatio(stack) < 0f) {
//                        return 1f;
//                    }
//                    return 0f;
//                });
    }

    private static float getChargeRatio(ItemStack stack) {
        Battery battery = stack.get(ModDataComponents.BATTERY.get());
        if (battery == null) return 0f;
        return battery.getChargeRatio();
    }

    public static void registerLayerDefinitions() {
        LOGGER.info("Registering layer definitions.");
        EntityModelLayerRegistry.register(PrinterRenderer.PaperModel.LAYER_LOCATION, PrinterRenderer.PaperModel::createBodyLayer);
    }
}
