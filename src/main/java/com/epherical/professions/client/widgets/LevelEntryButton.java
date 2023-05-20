package com.epherical.professions.client.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class LevelEntryButton extends AbstractEntryButton {

    public LevelEntryButton(Component component, int i, int j, int k, int l, OnPress onPress) {
        super(component, i, j, k, l, onPress, Tooltip.create(Component.nullToEmpty("")));
    }


    @Override
    public void renderWidget(GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        /*Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, 58, 148 + i * 24, this.width, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 149 - this.width / 2, 148 + i * 24, this.width / 2, this.height);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawString(poseStack, font, name, this.x + 24, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }*/
    }
}
