package com.epherical.professions.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import static com.epherical.professions.client.screen.OccupationScreen.WINDOW_LOCATION;

public abstract class AbstractEntryButton extends Button {

    protected final Component name;

    public AbstractEntryButton(Component component, int i, int j, int k, int l, OnPress onPress, OnTooltip tooltip) {
        super(i, j, k, l, Component.nullToEmpty(""), onPress, tooltip);
        this.name = component;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, 6, 152 + i * 24, this.width, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 160 - this.width / 2, 152 + i * 24, this.width / 2, this.height);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        float divide = font.width(name) / (float) this.width;
        if ((divide + 0.03) >= 1.0f) {
            if (divide > 1.0f) {
                float div = (float) (Math.floor(divide) - divide);
                divide = 0.95f + div;
            }
            poseStack.scale(divide, divide, divide);
            drawCenteredString(poseStack, font, name, (int) ((this.x + this.width / 2) / divide), (int) ((this.y + (this.height - 8) / 2) / divide), j | Mth.ceil(this.alpha * 255.0F) << 24);
            poseStack.scale(1 / divide, 1 / divide, 1 / divide);
        } else {
            drawCenteredString(poseStack, font, name, this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        }

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

}
