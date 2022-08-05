package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceKey;

public class RegistryObjectEntry<V, T> extends DatapackEntry<V, RegistryObjectEntry<V, T>> {

    private final ResourceKey<T> key;
    private final T object;
    private final RegistryEntry.ClickRegistryObjectEntry<V, T> click;

    public RegistryObjectEntry(int i, int j, int k, ResourceKey<T> key, T object, RegistryEntry.ClickRegistryObjectEntry<V, T> entry) {
        super(i, j, k);
        this.key = key;
        this.object = object;
        this.click = entry;
    }


    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        click.action(this);
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        rebuildTinyButtons(screen);
        screen.addChild(this);
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
