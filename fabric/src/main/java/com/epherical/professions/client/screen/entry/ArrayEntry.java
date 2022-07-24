package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.client.screen.button.SmallIconButton;
import com.epherical.professions.client.widgets.CommandButton;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ArrayEntry<T extends DatapackEntry> extends DatapackEntry {

    protected List<T> objects = new ArrayList<>();
    protected SmallIconButton addButton;
    protected SmallIconButton removeButton;
    private final String usage;

    protected boolean needsRefresh;

    public ArrayEntry(int x, int y, int width, String usage, BiFunction<Integer, Integer, T> addObject) {
        super(x, y, width);
        this.usage = usage;
        addButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.ADD, button -> {
            addEntry(addObject.apply(x, y + 2));
            needsRefresh = true;
        });
        removeButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.BAD, button -> {
            // todo; dropdown and add little x to all entries. clicking again will remove them
            // todo; needsRefresh = true;
        });
        children.addAll(List.of(addButton, removeButton));
    }

    @Override
    public void tick(DatapackScreen screen) {
        super.tick(screen);
        if (needsRefresh) {
            for (T object : objects) {
                for (AbstractWidget child : object.children) {
                    for (AbstractWidget widget : flattenEntries(Lists.newArrayList(child), child)) {
                        screen.removeWidget(widget);
                    }
                }
                screen.removeWidget(object);
            }
            int index = screen.children.indexOf(this);
            for (T object : objects) {
                object.setX(x + 7);
                object.setY(y + object.getHeight());
                for (AbstractWidget child : object.children) {
                    for (AbstractWidget widget : flattenEntries(Lists.newArrayList(child), child)) {
                        // todo; hmm
                        screen.children.add(index + 1, widget);
                        screen.renderables.add(index + 1, widget);
                        //screen.addRenderableWidget(child);
                    }
                }
                screen.children.add(index + 1, object);
                screen.addRenderableOnly(object);
            }
            screen.adjustEntries = true;
            needsRefresh = false;
        }
        for (T object : objects) {
            object.tick(screen);
        }
    }

    @Override
    public void initPosition(int initialX, int initialY) {
        super.initPosition(initialX, initialY);
        setButtonPositions(0, 0);
    }

    public T addEntry(T entry) {
        objects.add(entry);
        return entry;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, usage, (x + 3) + getXScroll(), (y + 8) + getYScroll(), 0xFFFFFF);
        setButtonPositions(getXScroll(), getYScroll());
    }

    @Override
    public JsonElement getSerializedValue() {
        JsonArray array = new JsonArray();
        for (T object : objects) {
            array.add(object.getSerializedValue());
        }

        return array;
    }

    private void setButtonPositions(int xScroll, int yScroll) {
        int start = width - 25;
        removeButton.x = x + start + xScroll;
        removeButton.y = y + 2 + yScroll;
        start -= 17;
        addButton.x = x + start + xScroll;
        addButton.y = y + 2 + yScroll;
    }

    @Override
    public String getType() {
        return "Array";
    }
}
