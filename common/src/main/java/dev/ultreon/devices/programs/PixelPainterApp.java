package dev.ultreon.devices.programs;

import com.mojang.blaze3d.platform.NativeImage;
import dev.ultreon.devices.OmnixerioDevices;
import dev.ultreon.devices.api.app.*;
import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.component.*;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.Label;
import dev.ultreon.devices.api.app.component.TextField;
import dev.ultreon.devices.api.app.renderer.ListItemRenderer;
import dev.ultreon.devices.api.io.File;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.object.Canvas;
import dev.ultreon.devices.object.ColorGrid;
import dev.ultreon.devices.object.Picture;
import dev.ultreon.devices.programs.system.layout.StandardLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class PixelPainterApp extends Application {
    private static final Identifier PIXEL_PAINTER_ICONS = OmnixerioDevices.id("textures/gui/pixel_painter.png");

    private static final Color ITEM_BACKGROUND = new Color(170, 176, 194);
    private static final Color ITEM_SELECTED = new Color(200, 176, 174);
    private static final Color AUTHOR_TEXT = new Color(114, 120, 138);

    /* Main Menu */
    private StandardLayout layoutMainMenu;
    private Label labelLogo;
    private Button btnNewPicture;
    private Button btnLoadPicture;

    /* New Picture */
    private Layout layoutNewPicture;
    private Label labelName;
    private TextField fieldName;
    private Label labelAuthor;
    private TextField fieldAuthor;
    private Label labelSize;
    private CheckBox checkBox16x;
    private CheckBox checkBox32x;
    private Button btnCreatePicture;

    /* Load Picture */
    private Layout layoutLoadPicture;
    private ItemList<Picture> listPictures;
    private Button btnLoadSavedPicture;
    private Button btnBrowseSavedPicture;
    private Button btnDeleteSavedPicture;
    private Button btnBackSavedPicture;

    /* Drawing */
    private Layout layoutDraw;
    private Canvas canvas;
    private ButtonToggle btnPencil;
    private ButtonToggle btnBucket;
    private ButtonToggle btnEraser;
    private ButtonToggle btnEyeDropper;
    private Button btnCancel;
    private Button btnSave;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private Component colorDisplay;
    private ColorGrid colorGrid;
    private CheckBox displayGrid;

    public PixelPainterApp() {
        //super("pixel_painter", "Pixel Painter");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void init(@Nullable CompoundTag intent) {
        /* Main Menu */
        layoutMainMenu = new StandardLayout("Main Menu", 201, 125, this, null);
        layoutMainMenu.setIcon(Icons.HOME);

        ItemList<Picture> pictureList = new ItemList<>(5, 43, 80, 4);
        pictureList.setListItemRenderer(new ListItemRenderer<>(18) {
            @Override
            public void render(GuiGraphicsExtractor graphics, Picture picture, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                RenderUtil.drawStringClipped(graphics, "Henlo", x, y, 100, AUTHOR_TEXT.getRGB(), true);
            }
        });
        layoutMainMenu.addComponent(pictureList);

        btnNewPicture = new Button(5, 25, "New", Icons.PICTURE);
        btnNewPicture.setSize(40, 16);
        btnNewPicture.setToolTip("New Picture", "Start a new masterpiece!");
        btnNewPicture.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                setCurrentLayout(layoutNewPicture);
            }
        });
        layoutMainMenu.addComponent(btnNewPicture);

        btnLoadPicture = new Button(48, 25, Icons.IMPORT);
        btnLoadPicture.setToolTip("Load External", "Open a picture from file");
        btnLoadPicture.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutLoadPicture));
        layoutMainMenu.addComponent(btnLoadPicture);

        Button btnDeletePicture = new Button(67, 25, Icons.TRASH);
        btnDeletePicture.setToolTip("Delete", "Removes the selected image");
        layoutMainMenu.addComponent(btnDeletePicture);


        /* New Picture */

        layoutNewPicture = new Layout(180, 65);

        labelName = new Label("Name", 5, 5);
        layoutNewPicture.addComponent(labelName);

        fieldName = new TextField(5, 15, 100);
        layoutNewPicture.addComponent(fieldName);

        labelAuthor = new Label("Author", 5, 35);
        layoutNewPicture.addComponent(labelAuthor);

        fieldAuthor = new TextField(5, 45, 100);
        layoutNewPicture.addComponent(fieldAuthor);

        labelSize = new Label("Size", 110, 5);
        layoutNewPicture.addComponent(labelSize);

        RadioGroup sizeGroup = new RadioGroup();

        checkBox16x = new CheckBox("16x", 110, 17);
        checkBox16x.setSelected(true);
        checkBox16x.setRadioGroup(sizeGroup);
        layoutNewPicture.addComponent(checkBox16x);

        checkBox32x = new CheckBox("32x", 145, 17);
        checkBox32x.setRadioGroup(sizeGroup);
        layoutNewPicture.addComponent(checkBox32x);

        btnCreatePicture = new Button(110, 40, "Create");
        btnCreatePicture.setSize(65, 20);
        btnCreatePicture.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            setCurrentLayout(layoutDraw);
            canvas.createPicture(fieldName.getText(), fieldAuthor.getText(), checkBox16x.isSelected() ? Picture.Size.X16 : Picture.Size.X32);
        });
        layoutNewPicture.addComponent(btnCreatePicture);


        /* Load Picture */

        layoutLoadPicture = new Layout(165, 116);
        layoutLoadPicture.setInitListener(() ->
        {
            listPictures.removeAll();
            FileSystem.getApplicationFolder(this, (folder, success) ->
            {
                if (success) {
                    assert folder != null;
                    folder.search(file -> file.isForApplication(this)).forEach(file ->
                    {
                        Picture picture = Picture.fromFile(file);
                        listPictures.addItem(picture);
                    });
                }
            });
        });

        listPictures = new ItemList<>(5, 5, 80, 5);
        listPictures.setListItemRenderer(new ListItemRenderer<>(20) {
            @Override
            public void render(GuiGraphicsExtractor graphics, Picture picture, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                graphics.fill(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());
                graphics.textRenderer().accept(x + 2, y + 2, net.minecraft.network.chat.Component.literal(picture.getName()));
                graphics.textRenderer().accept(x + 2, y + 11, net.minecraft.network.chat.Component.literal(picture.getAuthor()).withColor(0xff000000 | AUTHOR_TEXT.getRGB()));
            }
        });
        listPictures.setItemClickListener((picture, index, mouseButton) ->
        {
            if (mouseButton == 0) {
                btnLoadSavedPicture.setEnabled(true);
                btnDeleteSavedPicture.setEnabled(true);
            }
        });
        layoutLoadPicture.addComponent(listPictures);

        btnLoadSavedPicture = new Button(110, 5, "Load");
        btnLoadSavedPicture.setSize(50, 20);
        btnLoadSavedPicture.setEnabled(false);
        btnLoadSavedPicture.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (listPictures.getSelectedIndex() != -1) {
                canvas.setPicture(Objects.requireNonNull(listPictures.getSelectedItem()));
                setCurrentLayout(layoutDraw);
            }
        });
        layoutLoadPicture.addComponent(btnLoadSavedPicture);

        btnBrowseSavedPicture = new Button(110, 30, "Browse");
        btnBrowseSavedPicture.setSize(50, 20);
        btnBrowseSavedPicture.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            Dialog.OpenFile dialog = new Dialog.OpenFile(this);
            dialog.setResponseHandler((success, file) ->
            {
                if (file.isForApplication(this)) {
                    Picture picture = Picture.fromFile(file);
                    canvas.setPicture(picture);
                    setCurrentLayout(layoutDraw);
                    return true;
                } else {
                    Dialog.Message dialog2 = new Dialog.Message("Invalid file for Pixel Painter");
                    openDialog(dialog2);
                }
                return false;
            });
            openDialog(dialog);
        });
        layoutLoadPicture.addComponent(btnBrowseSavedPicture);

        btnDeleteSavedPicture = new Button(110, 55, "Delete");
        btnDeleteSavedPicture.setSize(50, 20);
        btnDeleteSavedPicture.setEnabled(false);
        btnDeleteSavedPicture.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (listPictures.getSelectedIndex() != -1) {
                Picture picture = listPictures.getSelectedItem();
                assert picture != null;
                File file = picture.getSource();
                if (file != null) {
                    file.delete((o, success) ->
                    {
                        if (success) {
                            listPictures.removeItem(listPictures.getSelectedIndex());
                            btnDeleteSavedPicture.setEnabled(false);
                            btnLoadSavedPicture.setEnabled(false);
                        } else {
                            //TODO error dialog
                        }
                    });
                } else {
                    //TODO error dialog
                }
            }
        });
        layoutLoadPicture.addComponent(btnDeleteSavedPicture);

        btnBackSavedPicture = new Button(110, 80, "Back");
        btnBackSavedPicture.setSize(50, 20);
        btnBackSavedPicture.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutMainMenu));
        layoutLoadPicture.addComponent(btnBackSavedPicture);


        /* Drawing */

        layoutDraw = new Layout(213, 140);

        canvas = new Canvas(5, 5);
        layoutDraw.addComponent(canvas);

        RadioGroup toolGroup = new RadioGroup();

        btnPencil = new ButtonToggle(138, 5, PIXEL_PAINTER_ICONS, 0, 0, 10, 10);
        btnPencil.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setCurrentTool(Canvas.PENCIL));
        btnPencil.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnPencil);

        btnBucket = new ButtonToggle(138, 24, PIXEL_PAINTER_ICONS, 10, 0, 10, 10);
        btnBucket.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setCurrentTool(Canvas.BUCKET));
        btnBucket.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnBucket);

        btnEraser = new ButtonToggle(138, 43, PIXEL_PAINTER_ICONS, 20, 0, 10, 10);
        btnEraser.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setCurrentTool(Canvas.ERASER));
        btnEraser.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnEraser);

        btnEyeDropper = new ButtonToggle(138, 62, PIXEL_PAINTER_ICONS, 30, 0, 10, 10);
        btnEyeDropper.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            canvas.setCurrentTool(Canvas.EYE_DROPPER);
            Color color = new Color(canvas.getCurrentColor());
            redSlider.setPercentage(color.getRed() / 255F);
            greenSlider.setPercentage(color.getGreen() / 255F);
            blueSlider.setPercentage(color.getBlue() / 255F);
        });
        btnEyeDropper.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnEyeDropper);

        Button button = new Button(138, 81, Icons.PRINTER);
        button.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            DebugLog.log("Print action triggered in pixel painter");
            if (mouseButton == 0) {
                Dialog.Print dialog = new Dialog.Print(new PicturePrint(canvas.picture.getName(), canvas.getPixels(), canvas.picture.getWidth()));
                openDialog(dialog);
            }
        });
//        button.setEnabled(false); // FIXME: WHY THE ACTUAL HELL IS THIS EVEN HERE :skull:
        layoutDraw.addComponent(button);

        btnCancel = new Button(138, 100, PIXEL_PAINTER_ICONS, 50, 0, 10, 10);
        btnCancel.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (canvas.isExistingImage())
                setCurrentLayout(layoutLoadPicture);
            else
                setCurrentLayout(layoutMainMenu);
            canvas.clear();
        });
        layoutDraw.addComponent(btnCancel);

        btnSave = new Button(138, 119, PIXEL_PAINTER_ICONS, 40, 0, 10, 10);
        btnSave.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            canvas.picture.pixels = canvas.copyPixels();

            CompoundTag pictureTag = new CompoundTag();
            canvas.picture.writeToNBT(pictureTag);

            if (canvas.isExistingImage()) {
                File file = canvas.picture.getSource();
                if (file != null) {
                    file.setData(pictureTag, (response, success) ->
                    {
                        assert response != null;
                        if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                            canvas.clear();
                            setCurrentLayout(layoutLoadPicture);
                        } else {
                            //TODO error dialog
                        }
                    });
                }
            } else {
                Dialog.SaveFile dialog = new Dialog.SaveFile(PixelPainterApp.this, pictureTag);
                dialog.setResponseHandler((success, file) ->
                {
                    if (success) {
                        canvas.clear();
                        setCurrentLayout(layoutLoadPicture);
                        return true;
                    } else {
                        //TODO error dialog
                    }
                    return false;
                });
                openDialog(dialog);
            }
        });
        layoutDraw.addComponent(btnSave);

        redSlider = new Slider(158, 30, 50);
        redSlider.setSlideListener(percentage -> canvas.setRed(percentage));
        layoutDraw.addComponent(redSlider);

        greenSlider = new Slider(158, 46, 50);
        greenSlider.setSlideListener(percentage -> canvas.setGreen(percentage));
        layoutDraw.addComponent(greenSlider);

        blueSlider = new Slider(158, 62, 50);
        blueSlider.setSlideListener(percentage -> canvas.setBlue(percentage));
        layoutDraw.addComponent(blueSlider);

        colorDisplay = new Component(158, 5) {
            @Override
            public void extractRenderState(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                graphics.fill(xPosition, yPosition, xPosition + 50, yPosition + 20, Color.DARK_GRAY.getRGB());
                graphics.fill(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 19, canvas.getCurrentColor());
            }
        };
        layoutDraw.addComponent(colorDisplay);

        colorGrid = new ColorGrid(157, 82, 50, canvas, redSlider, greenSlider, blueSlider);
        layoutDraw.addComponent(colorGrid);

        displayGrid = new CheckBox("Grid", 166, 120);
        displayGrid.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setShowGrid(displayGrid.isSelected()));
        layoutDraw.addComponent(displayGrid);

        setCurrentLayout(layoutMainMenu);
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {

    }

    @Override
    public void onClose() {
        super.onClose();
        listPictures.removeAll();
    }

    public static class PicturePrint implements IPrint {
        private String name;
        private int[] pixels;
        private int resolution;
        private boolean cut;

        public PicturePrint() {
        }

        public PicturePrint(String name, int[] pixels, int resolution) {
            this.name = name;
            this.setPicture(pixels);
        }

        private void setPicture(int[] pixels) {
            int resolution = (int) Math.sqrt(pixels.length);
            Picture.Size size = Picture.Size.getFromSize(resolution);
            if (size == null) {
                throw new IllegalArgumentException("Invalid pixels");
            }
            this.resolution = resolution;
            this.pixels = pixels;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int speed() {
            return resolution;
        }

        @Override
        public boolean requiresColor() {
            for (int pixel : pixels) {
                int r = (pixel >> 16 & 255);
                int g = (pixel >> 8 & 255);
                int b = (pixel & 255);
                if (r != g || r != b) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void store(ValueOutput data) {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", name);
            tag.putIntArray("pixels", pixels);
            tag.putInt("resolution", resolution);
            tag.putBoolean("cut", cut);
        }

        @Override
        public void read(@UnknownNullability ValueInput tag) {
            name = tag.getStringOr("name", "Untitled Print");
            cut = tag.getBooleanOr("cut", false);
            Optional<int[]> optionalPixels = tag.getIntArray("pixels");
            if (optionalPixels.isEmpty()) {
                int resolution = tag.getIntOr("resolution", 16);
                setPicture(new int[resolution * resolution]);
            } else {
                int[] pixels = optionalPixels.get();
                int resolution = tag.getIntOr("resolution", pixels.length == 32 * 32 ? 32 : 16);
                setPicture(pixels);
            }
        }

        @Override
        public void saveTag(CompoundTag tag) {
            tag.putString("name", name);
            tag.putIntArray("pixels", pixels);
            tag.putInt("resolution", resolution);
            tag.putBoolean("cut", cut);
        }

        @Override
        public void loadTag(CompoundTag tag) {
            Optional<String> optionalName = tag.getString("name");
            Optional<int[]> optionalPixels = tag.getIntArray("pixels");
            Optional<Integer> optionalResolution = tag.getInt("resolution");
            Optional<Boolean> optionalCut = tag.getBoolean("cut");
            name = optionalName.orElse("Untitled Print");
            cut = optionalCut.orElse(false);
            if (optionalPixels.isEmpty() && optionalResolution.isEmpty()) {
                setPicture(new int[16 * 16]);
                setResolution(16);
            } else if (optionalPixels.isEmpty()) {
                int[] pixels = new int[optionalResolution.get() * optionalResolution.get()];
                setPicture(pixels);
            } else {
                int[] pixels = optionalPixels.get();
                int resolution = optionalResolution.orElse(pixels.length == 32 * 32 ? 32 : 16);
                setPicture(pixels);
                setResolution(resolution);
            }
        }

        @Override
        public int[] getPixels() {
            return pixels;
        }

        @Override
        public void setPixels(int[] pixels) {
            this.pixels = pixels;
        }

        @Override
        public int getResolution() {
            return resolution;
        }

        @Override
        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        @Override
        public Class<? extends Renderer> getRenderer() {
            return PictureRenderer.class;
        }
    }


    public static class PictureRenderer implements IPrint.Renderer {
        public static final Identifier TEXTURE = OmnixerioDevices.id("textures/model/paper.png");
        public static final int MAX_COLOR = 0xFFFFFF;
        private Identifier picture;

        @Override
        public boolean render(GuiGraphicsExtractor graphics, CompoundTag data, int packedLight, int packedOverlay, Direction direction) {
            if (data.contains("pixels") && data.contains("resolution")) {
                int[] pixels = data.getIntArray("pixels").orElseThrow(() -> new IllegalArgumentException("Invalid pixels"));
                int resolution = data.getIntOr("resolution", pixels.length == 32 * 32 ? 32 : 16);
                boolean cut = data.getBooleanOr("cut", false);

                if (pixels.length != resolution * resolution)
                    return false;

                // This is for the paper background
                if (!cut) {
                    RenderUtil.drawRectWithTexture(TEXTURE, graphics, 0, 0, 0, 0, 1, 1, resolution, resolution, resolution, resolution);
                }

                // This creates a flipped copy of the pixel array
                // as it otherwise would be mirrored
                // TODO This is not the best way to do it, causes performance issues. Consider caching native images.
                NativeImage image = new NativeImage(resolution, resolution, false);
                for (int i = 0; i < resolution; i++) {
                    for (int j = 0; j < resolution; j++) {
                        image.setPixel(resolution - i - 1, resolution - j - 1, getPx(pixels, i, j, resolution));
                    }
                }

                Identifier oldPictureId = picture;
                if (oldPictureId != null)
                    Minecraft.getInstance().getTextureManager().release(picture);

                picture = OmnixerioDevices.id("picture/" + UUID.randomUUID().toString().replace("-", ""));
                DynamicTexture texture = new DynamicTexture(() -> "picture", image);
                Minecraft.getInstance().getTextureManager().register(picture, texture);

                graphics.blit(RenderPipelines.GUI_TEXTURED, picture, 0, 0, 0, 0, resolution, resolution, resolution, resolution);
                return true;
            }
            return false;
        }

        @Override
        public void delete() {
            if (picture != null)
                Minecraft.getInstance().getTextureManager().release(picture);
        }

        private static int getPx(int[] pixels, int i, int j, int resolution) {
            int pixel = pixels[i + j * resolution];
            int r = 255 - (pixel & 255);
            int g = 255 - (pixel >> 8 & 255);
            int b = 255 - (pixel >> 16 & 255);
            return MAX_COLOR - (r << 16 | g << 8 | b) & MAX_COLOR | (pixel >> 24 & 0xFF) << 24;
        }
    }
}
