package dev.ultreon.devices;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import de.marhali.json5.Json5Primitive;
import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DeviceConfig {
    public static final Json5 JSON5 = Json5.builder(builder -> builder
            .quoteless()
            .allowInfinity()
            .allowNaN()
            .allowBinaryLiterals()
            .allowHexFloatingLiterals()
            .allowLongUnicodeEscapes()
            .parseComments()
            .writeComments()
            .build());
    public static final Path PATH = Platform.getConfigFolder().resolve("omnixerio_devices.json5");

    // Laptop
    public int pingRate = 20;

    // Router
    public int signalRange = 20;
    public int beaconInterval = 20;
    public int maxDevices = 16;

    // Printing
    public boolean overridePrintSpeed = false;
    public int customPrintSpeed = 20;
    public int maxPaperCount = 64;

    // Pixel Painter
    public boolean pixelPainterEnable = true;
    public boolean renderPrinted3D = false;

    // Debug
    public boolean debugButton = Platform.isDevelopmentEnvironment();


    public void readSyncTag(CompoundTag tag) {
        if (tag.contains("pingRate", Tag.TAG_INT)) pingRate = tag.getInt("pingRate");
        if (tag.contains("signalRange", Tag.TAG_INT)) signalRange = tag.getInt("signalRange");
    }

    public CompoundTag writeSyncTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pingRate", pingRate);
        tag.putInt("signalRange", signalRange);
        return tag;
    }

    public void init() {
        load();
        save();
    }

    public void restore() {
        // NO-OP
    }

    public void save() {
        Json5Object root = new Json5Object();
        root.setComment("""
                Omnixerio Device configuration!
                """);

        Json5Object laptop = addObject(root, "laptop", "Configuration for laptops");
        addInt(laptop, "pingRate", "The amount of ticks the laptop waits until sending another ping to it's connected router.", pingRate, 1, 200);

        Json5Object router = addObject(root, "router", "Configuration for routers");
        addInt(router, "signalRange", "The range that routers can produce a signal to devices. This is the radius in blocks. Be careful when increasing this value, the performance is O(n^3) and larger numbers will have a bigger impact on the server", signalRange, 10, 100);
        addInt(router, "beaconInterval", "The amount of ticks the router waits before sending out a beacon signal. Higher number will increase performance but devices won't know as quick if they lost connection.", beaconInterval, 1, 200);
        addInt(router, "maxDevices", "The maximum amount of devices that can be connected to the router.", maxDevices, 1, 64);

        Json5Object printer = addObject(root, "printer", "Configuration for printers");
        addBoolean(printer, "overridePrintSpeed", "If enable, overrides all printing times with customPrintSpeed property", overridePrintSpeed);
        addInt(printer, "customPrintSpeed", "The amount of seconds it takes to print a page. This is overridden if overridePrintSpeed is enabled.", customPrintSpeed, 1, 600);
        addInt(printer, "maxPaperCount", "The maximum amount of paper that can be used in the printer.", maxPaperCount, 1, 99);

        Json5Object pixelPainter = addObject(root, "pixelPainter", "Configuration for printers");
        addBoolean(pixelPainter, "enabled", "Enable or disable the Pixel Painter app.", pixelPainterEnable);
        addBoolean(pixelPainter, "renderPrinted3D", "Should the pixels on printed pictures be render in 3D? Warning, this will decrease the performance of the game. You shouldn't enable it if you have a slow computer.", renderPrinted3D);

        Json5Object debug = addObject(root, "debug", "Debugging options");
        addBoolean(debug, "debugButton", "Display a button to access a worldless laptop", debugButton);

        // --- Save File ---

        try (BufferedWriter writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            JSON5.serialize(root, writer);
        } catch (IOException e) {
            OmnixerioDevicesMod.LOGGER.error("Failed to save config:", e);
        }
    }

    public void load() {
        if (Files.notExists(PATH)) return;

        try (BufferedReader reader = Files.newBufferedReader(PATH)) {
            Json5Object object = JSON5.parse(reader).getAsJson5Object();
            Json5Object laptop = getObject(object, "laptop");
            pingRate = getInt(laptop, "pingRate", pingRate, 1, 200);

            Json5Object router = getObject(object, "router");
            signalRange = getInt(router, "signalRange", signalRange, 10, 100);
            beaconInterval = getInt(router, "beaconInterval", beaconInterval, 0, 200);
            maxDevices = getInt(router, "maxDevices", maxDevices, 1, 64);

            Json5Object printer = getObject(object, "printer");
            overridePrintSpeed = getBoolean(printer, "overridePrintSpeed", overridePrintSpeed);
            customPrintSpeed = getInt(printer, "customPrintSpeed", customPrintSpeed, 1, 600);
            maxPaperCount = getInt(printer, "maxPaperCount", maxPaperCount, 1, 99);

            Json5Object pixelPainter = getObject(object, "pixelPainter");
            pixelPainterEnable = getBoolean(pixelPainter, "enabled", pixelPainterEnable);
            renderPrinted3D = getBoolean(pixelPainter, "renderPrinted3D", renderPrinted3D);

            Json5Object debug = getObject(object, "debug");
            debugButton = getBoolean(pixelPainter, "debugButton", debugButton);
        } catch (IOException e) {

        }
    }

    private Json5Object getObject(Json5Object category, String name) {
        if (!category.has(name)) return new Json5Object();
        Json5Element value = category.get(name);
        if (!value.isJson5Object()) return new Json5Object();
        return value.getAsJson5Object();
    }

    private boolean getBoolean(Json5Object category, String name, boolean fallback) {
        if (!category.has(name)) return fallback;
        Json5Element value = category.get(name);
        if (!value.isJson5Primitive()) return fallback;
        Json5Primitive primitive = value.getAsJson5Primitive();
        if (!primitive.isBoolean()) return fallback;
        return primitive.getAsBoolean();
    }

    private float getFloat(Json5Object category, String name, float fallback, float minValue, float maxValue) {
        if (!category.has(name)) return fallback;
        Json5Element value = category.get(name);
        if (!value.isJson5Primitive()) return fallback;
        Json5Primitive primitive = value.getAsJson5Primitive();
        if (!primitive.isBoolean()) return fallback;
        return Math.clamp(primitive.getAsFloat(), minValue, maxValue);
    }

    private double getDouble(Json5Object category, String name, double fallback, double minValue, double maxValue) {
        if (!category.has(name)) return fallback;
        Json5Element value = category.get(name);
        if (!value.isJson5Primitive()) return fallback;
        Json5Primitive primitive = value.getAsJson5Primitive();
        if (!primitive.isNumber()) return fallback;
        return Math.clamp(primitive.getAsDouble(), minValue, maxValue);
    }

    private byte getByte(Json5Object category, String name, byte fallback, byte minValue, byte maxValue) {
        if (!category.has(name)) return fallback;
        Json5Element value = category.get(name);
        if (!value.isJson5Primitive()) return fallback;
        Json5Primitive primitive = value.getAsJson5Primitive();
        if (!primitive.isNumber()) return fallback;
        return (byte) Math.clamp(primitive.getAsByte(), minValue, maxValue);
    }

    private short getShort(Json5Object category, String name, short fallback, short minValue, short maxValue) {
        if (!category.has(name)) return fallback;
        Json5Element value = category.get(name);
        if (!value.isJson5Primitive()) return fallback;
        Json5Primitive primitive = value.getAsJson5Primitive();
        if (!primitive.isNumber()) return fallback;
        return (short) Math.clamp(primitive.getAsShort(), minValue, maxValue);
    }

    private int getInt(Json5Object category, String name, int fallback, int minValue, int maxValue) {
        if (!category.has(name)) return fallback;
        Json5Element value = category.get(name);
        if (!value.isJson5Primitive()) return fallback;
        Json5Primitive primitive = value.getAsJson5Primitive();
        if (!primitive.isNumber()) return fallback;
        return Math.clamp(primitive.getAsInt(), minValue, maxValue);
    }

    private long getLong(Json5Object category, String name, long fallback, long minValue, long maxValue) {
        if (!category.has(name)) return fallback;
        Json5Element value = category.get(name);
        if (!value.isJson5Primitive()) return fallback;
        Json5Primitive primitive = value.getAsJson5Primitive();
        if (!primitive.isNumber()) return fallback;
        return Math.clamp(primitive.getAsLong(), minValue, maxValue);
    }

    private Json5Object addObject(Json5Object category, String name, String comment) {
        Json5Object json = new Json5Object();
        json.setComment(comment);
        category.add(name, json);
        return json;
    }

    private void addBoolean(Json5Object category, String name, String comment, boolean value) {
        Json5Primitive json = Json5Primitive.fromBoolean(value);
        json.setComment(comment + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addFloat(Json5Object category, String name, String comment, float value, float minValue, float maxValue) {
        Json5Primitive json = Json5Primitive.fromNumber(Math.clamp(value, minValue, maxValue));
        json.setComment(comment + "\nRange: " + maxValue + ".." + maxValue + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addDouble(Json5Object category, String name, String comment, double value, double minValue, double maxValue) {
        Json5Primitive json = Json5Primitive.fromNumber(Math.clamp(value, minValue, maxValue));
        json.setComment(comment + "\nRange: " + maxValue + ".." + maxValue + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addByte(Json5Object category, String name, String comment, byte value, byte minValue, byte maxValue) {
        Json5Primitive json = Json5Primitive.fromNumber(Math.clamp(value, minValue, maxValue));
        json.setComment(comment + "\nRange: " + maxValue + ".." + maxValue + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addShort(Json5Object category, String name, String comment, short value, short minValue, short maxValue) {
        Json5Primitive json = Json5Primitive.fromNumber(Math.clamp(value, minValue, maxValue));
        json.setComment(comment + "\nRange: " + maxValue + ".." + maxValue + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addInt(Json5Object category, String name, String comment, int value, int minValue, int maxValue) {
        Json5Primitive json = Json5Primitive.fromNumber(Math.clamp(value, minValue, maxValue));
        json.setComment(comment + "\nRange: " + maxValue + ".." + maxValue + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addLong(Json5Object category, String name, String comment, long value, long minValue, long maxValue) {
        Json5Primitive json = Json5Primitive.fromNumber(Math.clamp(value, minValue, maxValue));
        json.setComment(comment + "\nRange: " + maxValue + ".." + maxValue + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addString(Json5Object category, String name, String comment, String value) {
        Json5Primitive json = Json5Primitive.fromString(value);
        json.setComment(comment + "\nDefault: " + value);
        category.add(name, json);
    }

    private void addResourceLocation(Json5Object category, String name, String comment, ResourceLocation value) {
        Json5Primitive json = Json5Primitive.fromString(value.toString());
        json.setComment(comment + "\nDefault: " + value);
        category.add(name, json);
    }

//    @SubscribeEvent
//    public void.json onConfigChanged(ModConfigEvent.Reloading event) {
//        // TODO // Implement config reloading if needed.
//    }
}
