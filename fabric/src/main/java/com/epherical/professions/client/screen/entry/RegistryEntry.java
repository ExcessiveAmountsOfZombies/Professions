package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.client.screen.button.SmallIconButton;
import com.epherical.professions.client.widgets.CommandButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistryEntry<T> extends DatapackEntry {

    private final Minecraft minecraft = Minecraft.getInstance();

    private Registry<T> registry;
    private T value;
    private Component tooltip;
    private final SmallIconButton button;

    private boolean added = false;
    private List<RegistryObjectEntry<T>> registryEntries = new ArrayList<>();

    public RegistryEntry(int x, int y, int width, Registry<T> registry, T defaultValue) {
        super(x, y, width);
        this.value = defaultValue;
        this.tooltip = new TextComponent(registry.getKey(defaultValue).toString());
        this.registry = registry;

        int start = width - 25;
        this.button = new SmallIconButton(x + start, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.DROP_DOWN_OPEN, smallButton -> {
            smallButton.opened = !smallButton.opened;
            if (smallButton.opened) {
                smallButton.icon = CommandButton.SmallIcon.DROP_DOWN_CLOSE;
            } else {
                smallButton.icon = CommandButton.SmallIcon.DROP_DOWN_OPEN;
            }
        });
        children.add(button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, "Type:", x + 3 + getXScroll(), y + 8 + getYScroll(), 0xFFFFFF);
        drawCenteredString(poseStack, font, tooltip, (this.width / 2) + getXScroll(), (y + 8) + getYScroll(), 0x0095ba);
        if (isHoveredOrFocused()) {
            renderToolTip(poseStack, mouseX, mouseY, tooltip);
        }
        int start = width - 25;
        button.x = x + start + xScroll;
        button.y = y + 2 + yScroll;
    }

    @Override
    public void initPosition(int initialX, int initialY) {
        super.initPosition(initialX, initialY);
        setButtonPositions(0, 0);
    }

    @Override
    public JsonElement getSerializedValue() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void tick(DatapackScreen screen) {
        super.tick(screen);
        if (button.isOpened() && !added) {
            screen.markScreenDirty();
        } else if (!button.isOpened() && added) {
            screen.markScreenDirty();
        }

    }

    @Override
    public void onRebuild(DatapackScreen screen) {
        screen.addChild(button);
        screen.addChild(this);
        if (button.isOpened() && !added) {
            List<RegistryObjectEntry<T>> objects = new ArrayList<>();
            for (Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
                RegistryObjectEntry<T> objectEntry = new RegistryObjectEntry<>(x + 7, y + 23, 160, entry.getKey(), entry.getValue());
                objects.add(objectEntry);
                screen.addChild(objectEntry);
            }
            registryEntries.addAll(objects);
            added = true;
        } else if (!button.isOpened() && added) {
            added = false;
        }
    }

    @Override
    public String getType() {
        return "String";
    }

    private void setButtonPositions(int xScroll, int yScroll) {
        int start = width - 25;
        button.x = x + start + xScroll;
        button.y = y + 2 + yScroll;
    }
}
