package com.epherical.professions.client.screen;

import com.epherical.professions.client.screen.editors.DataTagEditor;
import com.epherical.professions.client.screen.entry.BooleanEntry;
import com.epherical.professions.client.screen.entry.CompoundEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.MultipleTypeEntry;
import com.epherical.professions.client.screen.entry.StringEntry;
import com.epherical.professions.client.screen.entry.TagEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;


public class DatapackScreen extends Screen {

    private static final Logger LOGGER = LogUtils.getLogger();

    protected int imageWidth = 108;
    protected int imageHeight = 180;

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/datapack_screen.png");

    protected boolean pressedNew = false;

    private double scrollAmount;

    public List<DatapackEntry> datapackEntries = new ArrayList<>();
    public boolean adjustEntries = false;

    private DataTagEditor<Block> blockDataTagEditor;

    public DatapackScreen() {
        super(Component.nullToEmpty(""));
    }


    @Override
    protected void init() {
        super.init();
        int ofx = 11;
        int ofy = 11;
        int width = this.width - 30;
        int height = 23;
        int distance = 23;
        //DataTagEditor<Block> blockDataTagEditor = new DataTagEditor<>((x, y) -> new TagEntry<>(x, y, width - 8, Registry.BLOCK, Blocks.STONE));
         this.blockDataTagEditor = new DataTagEditor<>((x, y) -> {
             MultipleTypeEntry required = new MultipleTypeEntry(ofx + 8, y, 90,
                     new StringEntry(ofx + 14, y, width - 14, "", "minecraft:stone"),
                     new CompoundEntry(0, 0, 0,
                             List.of(new StringEntry(ofx + 14, y, width - 14, "id", "minecraft:stone", Optional.of("id")),
                                     new BooleanEntry(ofx + 14, y, width - 14, "required", false, Optional.of("required")))));
             return required;
        });
        int increment = 0;
        //LOGGER.info("Setting x, y position on init entries.");
        for (DatapackEntry entry : blockDataTagEditor.entries()) {
            entry.setX(ofx);
            entry.setY(ofy + (entry.getHeight() * increment));
            entry.setWidth(width);
            entry.initPosition(ofx, ofy);
            this.addDatapackWidget(entry);
            increment++;
        }




        /*this.addDatapackWidget(new RegistryEntry<>(ofx, ofy, width, RegistryConstants.PROFESSION_SERIALIZER, ProfessionSerializer.DEFAULT_PROFESSION));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance, width, "Color:", "#FFFFFF"));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance + height, width, "Desc Color:", "#FFFFFF"));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance + (height * 2), width, "Display Name:", "Occupation"));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance + (height * 3), width, "Exp Scaling:", "1000*1.064^(lvl-1)"));
        //this.addRenderableWidget(new StringEntry(ofx, ofy + distance + (height * 4), width, "Income Scale", "base"));
        this.addDatapackWidget(new NumberEntry<>(ofx, ofy + distance + (height * 4), width, "Max Level:", 100));*/
    }

    private int time = 0;

    @Override
    public void tick() {
        super.tick();
        for (DatapackEntry datapackEntry : datapackEntries) {
            datapackEntry.tick(this);
        }

        if (adjustEntries) {
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

            time += 2;
            if (time == 20) {
                adjustEntries = false;
                time = 0;
            }
            blockDataTagEditor.serialize(null);
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
                    entry.setYScroll((int) - scrollAmount);
                }
            }
            double d = this.minecraft.getWindow().getGuiScale();
            RenderSystem.enableScissor(
                    (int) (10 * d),
                    (int) ((double) (11) * d),
                    (int) ((double) (this.getScrollbarPosition() + 6) * d),
                    (int) ((double) (this.height - 22) * d));
            super.render(poseStack, mouseX, mouseY, partialTick);
            RenderSystem.disableScissor();
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
        hLine(stack, 10, width - 11, 10, 0xFFFFFFFF);
        hLine(stack, 10, width - 11, height - 11, 0xFFFFFFFF);
        vLine(stack, 10, 10, height - 10, 0xFFFFFFFF);
        vLine(stack, width - 11, 10, height - 10, 0xFFFFFFFF);
        fill(stack, 10, 10, width - 10, height - 10, 0xAA333333);
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
            int endOfScrollBar = (int)((float)((top - bottom) * (top - bottom)) / (float)this.getMaxPosition());
            endOfScrollBar = Mth.clamp(endOfScrollBar, 32, (top - bottom - 8));
            int newBottom = (int)this.getScrollAmount() * (top - bottom - endOfScrollBar) / maxScroll + bottom;
            if (newBottom < bottom) {
                newBottom = bottom;
            }

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(i, top - 8, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(j , top - 8, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(j , bottom +8, 0.0).color(0, 0, 0, 255).endVertex();
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

    public <T extends DatapackEntry> T addDatapackWidget(T widget) {
        datapackEntries.add(widget);
        for (AbstractWidget child : widget.children()) {
            super.addRenderableWidget(child);
        }
        return super.addRenderableWidget(widget);
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
            this.setScrollAmount(this.getScrollAmount() - delta * (double)23 / 2.0);
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
}
