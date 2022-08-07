package com.epherical.professions.client.screen;

import com.epherical.professions.client.FileBox;
import com.epherical.professions.client.SaveSideBar;
import com.epherical.professions.client.SaveSidebarWidget;
import com.epherical.professions.client.editors.DatapackEditor;
import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.datapack.AbstractProfessionLoader;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class DatapackScreen extends CommonDataScreen {

    private static final Logger LOGGER = LogUtils.getLogger();

    protected int imageWidth = 108;
    protected int imageHeight = 180;

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/datapack_screen.png");

    protected boolean pressedNew = false;

    private double scrollAmount;

    public List<DatapackEntry<?, ?>> datapackEntries = new ArrayList<>();
    public boolean adjustEntries = false;

    private DatapackEditor<?> datapackEditor;

    private SaveSidebarWidget saveSidebarWidget;
    private boolean sidebarWidgetOpen = false;
    private Instant clickedTime;
    private SaveSideBar component;
    private final List<Widget> specialRenders = new ArrayList<>();

    public DatapackScreen() {
        super(Component.nullToEmpty(""));
    }


    @Override
    protected void init() {
        super.init();

        saveSidebarWidget = new SaveSidebarWidget(0, 20, 10, 20, button -> {
            sidebarWidgetOpen = !sidebarWidgetOpen;

            //clickedTime = Instant.now().plus(750, ChronoUnit.MILLIS);
            children.clear();
            if (sidebarWidgetOpen) {
                addChild(saveSidebarWidget);
                specialRenders.add(component);
                saveSidebarWidget.x = component.width;
                component.x = 0;
                for (Widget child : component.children()) {
                    if (child instanceof AbstractWidget abstractWidget) {
                        this.addChild(abstractWidget);
                    }
                    this.specialRenders.add(child);
                }
            } else {
                saveSidebarWidget.x = 0;
                component.x = -(width / 3);
                rebuildScreen();
            }
        });
        component = new SaveSideBar(-(width / 3), 0, width / 3, height,
                new FileBox((this.width - 300) / 2, (this.height - 100) / 2, 300, 100,
                        button -> {
                            JsonObject object = new JsonObject();

                            String data = "data/" + component.getFileBox().getNamespace().getValue()
                                    + "/professions/occupations/";
                            datapackEditor.serialize(object);
                            try {

                                Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve("professions/" + data));
                                Files.writeString(FMLPaths.CONFIGDIR.get().resolve("professions/" + data +
                                        "/" + component.getFileBox().getOccupationName().getValue() + ".json"), AbstractProfessionLoader.serialize(object));
                            } catch (IOException e) {
                                LOGGER.warn("FILE ALREADY EXISTS", e);
                            }
                        }, button -> {
                    saveSidebarWidget.x = 0;
                    component.x = -(width / 3);
                    rebuildScreen();
                }));

        this.addChild(saveSidebarWidget);


        int ofx = 32;
        int ofy = 11;
        int width = this.width - 50;

        if (this.datapackEditor == null) {
           // TODO: fix this.datapackEditor = new ProfessionEditor(ofx, width);
        } else {
            this.datapackEditor.setWidth(width);
        }

        int increment = 0;
        //LOGGER.info("Setting x, y position on init entries.");
        for (DatapackEntry<?, ?> entry : datapackEditor.entries()) {
            entry.setX(ofx);
            entry.setY(ofy + (entry.getHeight() * increment));
            entry.setWidth(width);
            entry.initPosition(ofx, ofy);
            this.addDatapackWidget(entry);
            increment++;
        }
    }

    private int time = 0;

    @Override
    public void tick() {
        super.tick();
        // todo; would love animation
        /*if (sidebarWidgetOpen) {
            Instant now = Instant.now();
            if (now.isBefore(clickedTime)) {
                long duration = Duration.between(now, clickedTime).toMillis();
                double dddd = (double) duration / 750;
                component.x += Math.sin((dddd * Math.PI) / 2) * 42;
                saveSidebarWidget.x += Math.sin((dddd * Math.PI) / 2) * 42;
                if (component.x >= 0) {
                    component.x = 0;
                    saveSidebarWidget.x = component.width;
                }
                *//*component.x += 5;*//*
            } else {
                if (component.x <= 0) {
                    component.x = 0;
                    saveSidebarWidget.x = component.width;
                }
            }
        } else {
            Instant now = Instant.now();
            if (now.isBefore(clickedTime)) {
                long duration = Duration.between(now, clickedTime).toMillis();
                double dddd = (double) duration / 750;
                component.x -= Math.sin((dddd * Math.PI) / 2) * 36;
                saveSidebarWidget.x -= Math.sin((dddd * Math.PI) / 2) * 42;
                *//*component.x += 5;*//*
            } else {
                if (component.x <= 0) {
                    renderables.remove(component);
                    rebuildScreen();
                }
            }
        }*/

        for (DatapackEntry datapackEntry : datapackEntries) {
            datapackEntry.tick(this);
        }

        if (adjustEntries) {
            rebuildScreen();
            int yOffset = 11;
            int increment = 0;
            for (GuiEventListener child : this.children()) {
                // todo; want to animate the dropdowns instead of it being instant at some point, no idea how.
                if (child instanceof DatapackEntry widget) {

                    widget.y = yOffset + ((widget.getHeight()) * increment);
                    if (widget.getHeight() == 0) {
                        increment--;
                    }
                    increment++;
                }
            }

            /*time += 2;
            if (time == 20) {
                time = 0;
            }*/
            adjustEntries = false;
        }

    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);

        int ofx = (this.width - imageWidth) / 2;
        int ofy = (this.height - imageHeight) / 2;
        if (pressedNew) {
            renderMainWindow(poseStack, ofx, ofy);
            for (GuiEventListener child : this.children) {
                if (child instanceof DatapackEntry entry) {
                    entry.setYScroll((int) -scrollAmount);
                }
            }

            double d = this.minecraft.getWindow().getGuiScale();
            RenderSystem.enableScissor(
                    (int) (0 * d),
                    (int) ((double) (11) * d),
                    (int) ((double) (this.getScrollbarPosition() + 6) * d),
                    (int) ((double) (this.height - 22) * d));
            super.render(poseStack, mouseX, mouseY, partialTick);
            RenderSystem.disableScissor();

            for (Widget specialRender : specialRenders) {
                poseStack.pushPose();
                specialRender.render(poseStack, mouseX, mouseY, partialTick);
                poseStack.popPose();
            }

            renderScrollBar();
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            font.drawShadow(poseStack, "press 'n' to create a new profession", ofx, 32, 0xFFFFFF);
        }

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void renderMainWindow(PoseStack stack, int offsetX, int offsetY) {
        hLine(stack, 30, width - 11, 10, 0xFFFFFFFF);
        hLine(stack, 30, width - 11, height - 11, 0xFFFFFFFF);
        vLine(stack, 30, 10, height - 10, 0xFFFFFFFF);
        vLine(stack, width - 11, 10, height - 10, 0xFFFFFFFF);
        fill(stack, 30, 10, width - 10, height - 10, 0xAA333333);
    }

    protected int getScrollbarPosition() {
        return this.width - 11;
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double scroll) {
        this.scrollAmount = Mth.clamp(scroll, 0.0, this.getMaxScroll());
    }

    private void renderScrollBar() {
        // just using the code from AbstractSelectionList to create a scrollbar.
        int i = this.getScrollbarPosition();
        int j = i - 5;
        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            int top = 20;
            int bottom = this.height - 20;
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int endOfScrollBar = (int) ((float) ((top - bottom) * (top - bottom)) / (float) this.getMaxPosition());
            endOfScrollBar = Mth.clamp(endOfScrollBar, 50, (top - bottom - 8));
            int newBottom = (int) this.getScrollAmount() * (top - bottom - endOfScrollBar) / maxScroll + bottom;
            if (newBottom < bottom) {
                newBottom = bottom;
            }

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(i, top - 8, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(j, top - 8, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(j, bottom + 8, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(i, bottom + 8, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(i, (newBottom + endOfScrollBar), 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(j, (newBottom + endOfScrollBar), 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(j, newBottom, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(i, newBottom, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(i, (newBottom + endOfScrollBar - 1), 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex((j - 1), (newBottom + endOfScrollBar - 1), 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex((j - 1), newBottom, 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(i, newBottom, 0.0).color(192, 192, 192, 255).endVertex();
            tesselator.end();
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_N) {
            // todo: remove this
            pressedNew = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return super.children();
    }

    public void markScreenDirty() {
        adjustEntries = true;
    }

    private void rebuildScreen() {
        datapackEntries.clear();
        children.clear();
        renderables.clear();
        narratables.clear();
        specialRenders.clear();
        rebuildEntries();
    }

    private void rebuildEntries() {
        addChild(saveSidebarWidget);
        for (DatapackEntry datapackEntry : this.datapackEditor.entries()) {
            this.addDatapackWidget(datapackEntry);
        }
    }

    public <T extends DatapackEntry> T addDatapackWidget(T widget) {
        datapackEntries.add(widget);
        widget.onRebuild(this);
        return widget;
    }

    @Override
    public <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
        return super.addRenderableWidget(widget);
    }

    @Override
    public <T extends GuiEventListener & NarratableEntry> T addWidget(T listener) {
        return super.addWidget(listener);
    }

    @Override
    public <T extends Widget> T addRenderableOnly(T widget) {
        return super.addRenderableOnly(widget);
    }

    @Override
    public void removeWidget(GuiEventListener listener) {
        super.removeWidget(listener);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!super.mouseScrolled(mouseX, mouseY, delta)) {
            // 23 as most entries will be at least 23.
            this.setScrollAmount(this.getScrollAmount() - delta * (double) 23 / 2.0);
            return true;
        } else {
            return false;
        }
    }

    protected int getMaxPosition() {
        return this.children.stream().mapToInt(value -> {
            if (value instanceof DatapackEntry entry) {
                return entry.getHeight();
            } else {
                return 0;
            }
        }).sum();
    }

    public int getMaxScroll() {                                         //0 for top height
        return Math.max(0, this.getMaxPosition() - ((this.height - 10) - (0 + 10) - 20));
    }

    public int indexOf(AbstractWidget widget) {
        return children.indexOf(widget);
    }

    public void addChild(AbstractWidget widget) {
        this.children.add(widget);
        this.renderables.add(widget);
        this.narratables.add(widget);
    }

    public void addChild(int index, AbstractWidget widget) {
        this.children.add(index, widget);
        this.renderables.add(index, widget);
        this.narratables.add(index, widget);
    }


}
