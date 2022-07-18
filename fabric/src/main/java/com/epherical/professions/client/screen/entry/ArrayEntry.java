package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.button.SmallIconButton;
import com.epherical.professions.client.widgets.CommandButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ArrayEntry<T extends DatapackEntry> extends DatapackEntry {

    protected List<T> objects = new ArrayList<>();
    protected SmallIconButton dropdownButton;
    protected SmallIconButton addButton;
    protected SmallIconButton removeButton;
    private final String usage;

    public ArrayEntry(int x, int y, int width, String usage) {
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
            // todo: add an object to the list
        });
        removeButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.BAD, button -> {
            // todo; dropdown and add little x to all entries. clicking again will remove them
        });
        children.addAll(List.of(dropdownButton, addButton, removeButton));
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
