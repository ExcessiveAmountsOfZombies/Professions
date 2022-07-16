package com.epherical.professions.client.screen.button;

import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.client.widgets.CommandButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class DropdownButton extends Button {

    private boolean opened = false;
    private CommandButton.SmallIcon icon;


    public DropdownButton(int i, int j, int k, int l, Component component, OnPress onPress) {
        super(i, j, k, l, component, onPress);
        icon = CommandButton.SmallIcon.DROP_DOWN_OPEN;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        opened = !opened;
        if (opened) {
            icon = CommandButton.SmallIcon.DROP_DOWN_CLOSE;
        } else {
            icon = CommandButton.SmallIcon.DROP_DOWN_OPEN;
        }
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, DatapackScreen.WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        if (i == 1) {
            i = 0;
        } else {
            i = 1;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        //this.blit(poseStack, this.x, this.y, i * 16, 187, 16, 16);
        this.blit(poseStack, this.x + 3, this.y - 2, icon.ordinal() * 16, 240, this.width, this.height);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    public boolean isOpened() {
        return opened;
    }
}
