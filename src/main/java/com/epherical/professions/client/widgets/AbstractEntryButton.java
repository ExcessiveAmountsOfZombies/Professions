package com.epherical.professions.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import static com.epherical.professions.client.screen.OccupationScreen.WINDOW_LOCATION;

public abstract class AbstractEntryButton extends Button {

    protected final Component name;

    public AbstractEntryButton(Component component, int i, int j, int k, int l, OnPress onPress, Tooltip tooltip) {
        super(i, j, k, l, Component.nullToEmpty(""), onPress, Button.DEFAULT_NARRATION);
        setTooltip(tooltip);
        this.name = component;
    }



    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        graphics.blit(WINDOW_LOCATION, getX(), getY(), 58, 148 + i * 24, this.width, this.height);
        graphics.blit(WINDOW_LOCATION, getX() + this.width / 2, getY(), 149 - this.width / 2, 148 + i * 24, this.width / 2, this.height);
        //render(minecraft, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        float divide = font.width(name) / (float) this.width;
        if ((divide + 0.03) >= 1.0f) {
            if (divide > 1.0f) {
                float div = (float) (Math.floor(divide) - divide);
                divide = 0.95f + div;
            }
            graphics.pose().scale(divide, divide, divide);
            graphics.drawCenteredString(font, name, (int) ((getX() + this.width / 2) / divide), (int) ((getY() + (this.height - 8) / 2) / divide), j | Mth.ceil(this.alpha * 255.0F) << 24);
            graphics.pose().scale(1 / divide, 1 / divide, 1 / divide);
        } else {
            graphics.drawCenteredString(font, name, getX() + this.width / 2, getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        }

        if (this.isHoveredOrFocused()) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
                screen.setTooltipForNextRenderPass(this.getTooltip(), this.createTooltipPositioner(), this.isFocused());
            }
        }
    }

    public int getYImage() {
        int $$0 = 1;
        if (!this.active) {
            $$0 = 0;
        } else if (this.isHoveredOrFocused()) {
            $$0 = 2;
        }

        return 46 + $$0 * 20;
    }

}
