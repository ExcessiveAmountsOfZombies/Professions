package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.client.screen.button.SmallIconButton;
import com.epherical.professions.client.widgets.CommandButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ArrayEntry<T extends DatapackEntry> extends DatapackEntry {

    protected List<T> objects = new ArrayList<>();
    protected SmallIconButton dropdownButton;
    protected SmallIconButton addButton;
    protected SmallIconButton removeButton;
    private final String usage;

    protected boolean needsRefresh;

    public ArrayEntry(int x, int y, int width, String usage, BiFunction<Integer, Integer, T> addObject) {
        super(x, y, width);
        this.usage = usage;
        dropdownButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.DROP_DOWN_OPEN, button -> {
            button.opened = !button.opened;
            if (button.opened) {
                button.icon = CommandButton.SmallIcon.DROP_DOWN_CLOSE;
            } else {
                button.icon = CommandButton.SmallIcon.DROP_DOWN_OPEN;
            }
        });
        addButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.ADD, button -> {
            addEntry(addObject.apply(x, y + 2));
            needsRefresh = true;
        });
        removeButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.BAD, button -> {
            // todo; dropdown and add little x to all entries. clicking again will remove them
            // todo; needsRefresh = true;
        });
        children.addAll(List.of(dropdownButton, addButton, removeButton));
    }

    @Override
    public void tick(DatapackScreen screen) {
        super.tick(screen);
        if (needsRefresh) {
            for (T object : objects) {
                screen.removeWidget(object);
            }
            int index = screen.children.indexOf(this);
            for (T object : objects) {
                object.x = x + 7;
                object.y = y + 23;
                for (AbstractWidget child : object.children) {
                    // todo; hmm
                    screen.children.add(index + 1, child);
                    screen.addRenderableWidget(child);
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
        int start = width - 25;
        dropdownButton.x = x + start;
        dropdownButton.y = y + 2;
        start -= 17;
        removeButton.x = x + start;
        removeButton.y = y + 2;
        start -= 17;
        addButton.x = x + start;
        addButton.y = y + 2;
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
        font.drawShadow(poseStack, usage, x + 3, y + 8, 0xFFFFFF);
    }

}
