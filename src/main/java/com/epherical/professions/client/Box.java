package com.epherical.professions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

public abstract class Box implements Renderable, WidgetParent {

    public int x;
    public int y;
    public int width;
    public int height;

    public Box(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.hLine(x, width + x, y, 0xFFFFFFFF); // top
        graphics.hLine(x, width + x, y + height, 0xFFFFFFFF); // bottom
        graphics.vLine(x + width, y, y + height, 0xFFFFFFFF); // right
        graphics.vLine(x, y + height, y, 0xFFFFFFFF); // left
        graphics.fill(x, y, x + width + 1, y + height + 1, 0xAA333333);
    }

    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }


    public void beginAnimation() {

    }

    public void endAnimation() {

    }
}
