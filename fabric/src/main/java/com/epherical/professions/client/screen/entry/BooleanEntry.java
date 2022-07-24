package com.epherical.professions.client.screen.entry;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class BooleanEntry extends DatapackEntry {

    private String usage;
    private boolean defaultBoolean;

    public BooleanEntry(int i, int j, int k, String usage, boolean defaultValue) {
        super(i, j, k);
        this.usage = usage;
        this.defaultBoolean = defaultValue;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, usage, x + 3 + getXScroll(), y + 8 + getYScroll(), 0xFFFFFF);
        drawCenteredString(poseStack, font, String.valueOf(defaultBoolean), (this.width / 2) + getXScroll(), (y + 8) + getYScroll(), 0x0095ba);
        //this.box.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.defaultBoolean = !defaultBoolean;
    }
}
