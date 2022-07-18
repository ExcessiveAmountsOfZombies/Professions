package com.epherical.professions.client.screen;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.client.screen.editors.DataTagEditor;
import com.epherical.professions.client.screen.entry.ArrayEntry;
import com.epherical.professions.client.screen.entry.CompoundEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.NumberEntry;
import com.epherical.professions.client.screen.entry.RegistryEntry;
import com.epherical.professions.client.screen.entry.StringEntry;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Actions;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;


public class DatapackScreen extends Screen {

    protected int imageWidth = 108;
    protected int imageHeight = 180;

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/datapack_screen.png");

    protected boolean pressedNew = false;

    public List<DatapackEntry> datapackEntries = new ArrayList<>();
    public boolean adjustEntries = false;

    public DatapackScreen() {
        super(Component.nullToEmpty("stop"));
    }


    @Override
    protected void init() {
        super.init();
        int ofx = 11;
        int ofy = 11;
        int width = this.width - 30;
        int height = 23;
        int distance = 23;
        DataTagEditor<Block> blockDataTagEditor = new DataTagEditor<>();
        int increment = 0;
        for (DatapackEntry entry : blockDataTagEditor.entries()) {
            entry.x = ofx;
            entry.y = ofy + (entry.getHeight() * increment);
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
                    increment++;
                }
            }

            time += 2;
            if (time == 20) {
                adjustEntries = false;
                time = 0;
            }
        }

    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);

        int ofx = (this.width - imageWidth) / 2;
        int ofy = (this.height - imageHeight) / 2;
        if (pressedNew) {
            renderMainWindow(poseStack, ofx, ofy);
            super.render(poseStack, mouseX, mouseY, partialTick);
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

    protected <T extends DatapackEntry> T addDatapackWidget(T widget) {
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
    public  <T extends Widget> T addRenderableOnly(T widget) {
        return super.addRenderableOnly(widget);
    }

    @Override
    public void removeWidget(GuiEventListener listener) {
        super.removeWidget(listener);
    }
}
