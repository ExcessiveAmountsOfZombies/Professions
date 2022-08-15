package com.epherical.professions.client.button;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.epherical.professions.client.widgets.CommandButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class SmallIconButton extends Button {

    public boolean opened = false;
    public CommandButton.SmallIcon icon;
    protected final ButtonPress<SmallIconButton> onPress;


    public SmallIconButton(int i, int j, int k, int l, Component component, CommandButton.SmallIcon icon, ButtonPress<SmallIconButton> onPress) {
        super(i, j, k, l, component, button -> {
        });
        this.onPress = onPress;
        this.icon = icon;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        onPress.onPress(this);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CommonDataScreen.WINDOW_LOCATION);
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
