package com.epherical.professions.client.entry;

import com.epherical.professions.client.button.SmallIconButton;
import com.epherical.professions.client.screen.CommonDataScreen;
import com.epherical.professions.client.widgets.CommandButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class ArrayEntry<T extends DatapackEntry> extends DatapackEntry {

    protected List<T> objects = new ArrayList<>();
    protected SmallIconButton addButton;
    protected SmallIconButton removeButton;
    private final String usage;

    protected boolean needsRefresh;

    public ArrayEntry(int x, int y, int width, String usage, BiFunction<Integer, Integer, T> addObject) {
        super(x, y, width, Optional.of(usage));
        this.usage = usage;
        addButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.ADD, button -> {
            T t = addEntry(addObject.apply(x, this.y + 2));
            t.addListener(button1 -> {
                if (button1.getType() == Type.REMOVE) {
                    objects.remove(t);
                    needsRefresh = true;
                }
            });
            for (AbstractWidget child : t.children) {
                if (child instanceof DatapackEntry entry) {
                    entry.addListener(button1 -> {
                        if (button1.getType() == Type.REMOVE) {
                            objects.remove(t);
                            needsRefresh = true;
                        }
                    });
                }
            }
            needsRefresh = true;
        });
       /* removeButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.BAD, button -> {
            // todo; dropdown and add little x to all entries. clicking again will remove them
            // todo; needsRefresh = true;
        });*/
        children.addAll(List.of(addButton/*, removeButton*/));
    }

    @Override
    public void tick(CommonDataScreen screen) {
        super.tick(screen);
        if (needsRefresh) {
            screen.markScreenDirty();
            needsRefresh = false;
        }
        for (T object : objects) {
            object.tick(screen);
        }
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        rebuildTinyButtons(screen);
        screen.addChild(addButton);
        //screen.addChild(removeButton);
        screen.addChild(this);
        for (T object : objects) {
            object.onRebuild(screen);
        }
    }

    @Override
    public void initPosition(int initialX, int initialY) {
        super.initPosition(initialX, initialY);
        setButtonPositions(0, 0);
    }

    public T addEntry(T entry) {
        objects.add(0, entry);
        entry.initPosition(this.x, this.y);
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
        /*removeButton.x = x + start + xScroll;
        removeButton.y = y + 2 + yScroll;*/
        /*start -= 17;*/
        addButton.x = x + start + xScroll;
        addButton.y = y + 2 + yScroll;
    }

    @Override
    public List<? extends AbstractWidget> children() {
        List<AbstractWidget> copy = new ArrayList<>(super.children());
        copy.addAll(objects);
        return copy;
    }

    @Override
    public String getType() {
        return "Array";
    }

    @Override
    public String toString() {
        return "ArrayEntry{" +
                "usage='" + usage + '\'' +
                "} " + super.toString();
    }
}
