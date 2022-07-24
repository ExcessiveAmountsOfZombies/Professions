package com.epherical.professions.client.screen.entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceKey;

public class RegistryObjectEntry<T> extends DatapackEntry {

    private final ResourceKey<T> key;
    private final T object;

    public RegistryObjectEntry(int i, int j, int k, ResourceKey<T> key, T object) {
        super(i, j, k);
        this.key = key;
        this.object = object;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        drawCenteredString(poseStack, font, key.location().toString(), x + getXScroll() + width / 2, y + 8 + getYScroll(), 0xFFFFFF);
    }

    @Override
    public JsonElement getSerializedValue() {
        return JsonNull.INSTANCE;
    }

    public T getObject() {
        return object;
    }

    @Override
    public String getType() {
        return "String";
    }
}
