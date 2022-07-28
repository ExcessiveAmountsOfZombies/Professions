package com.epherical.professions.client.screen;

import com.epherical.professions.client.screen.button.ButtonPress;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class SaveSidebarWidget extends AbstractWidget {

    private final ButtonPress<SaveSidebarWidget> buttonPress;

    public SaveSidebarWidget(int x, int y, int width, int height, ButtonPress<SaveSidebarWidget> buttonPress) {
        super(x, y, width, height, Component.nullToEmpty(""));
        this.buttonPress = buttonPress;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.buttonPress.onPress(this);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        hLine(stack, x, width + x, y, 0xFFFFFFFF); // top
        hLine(stack, x, width + x, y + height, 0xFFFFFFFF); // bottom
        vLine(stack, width + x, y, y + height, 0xFFFFFFFF); // right
        fill(stack, x, y, x + width + 1, y + height + 1, 0xAA333333);
    }
}
