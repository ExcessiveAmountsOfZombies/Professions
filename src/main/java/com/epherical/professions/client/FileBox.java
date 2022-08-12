package com.epherical.professions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FileBox extends Box {

    private final EditBox namespace;
    private final EditBox occupationName;
    private final Button okButton;
    private final Button cancelButton;

    public FileBox(int x, int y, int width, int height, Button.OnPress save, Button.OnPress cancel) {
        super(x, y, width, height);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        this.namespace = new EditBox(font, x + 5, y + 40, 100, 20, Component.nullToEmpty("Namespace"));
        this.occupationName = new EditBox(font, x + 115, y + 40, 100, 20, Component.nullToEmpty("Occupation Name"));
        this.okButton = new Button(x + 5, y + 79, 30, 20, Component.nullToEmpty("Save"), save);
        this.cancelButton = new Button(x + 40, y + 79, 40, 20, Component.nullToEmpty("Cancel"), cancel);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        super.render(stack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(stack, "Namespace", x + 5, y + 20, 0xFFFFFF);
        font.drawShadow(stack, "Name", x + 120, y + 20, 0xFFFFFF);
    }

    @Override
    public List<Widget> children() {
        return List.of(namespace, occupationName, okButton, cancelButton);
    }

    public EditBox getNamespace() {
        return namespace;
    }

    public EditBox getPath() {
        return occupationName;
    }
}
