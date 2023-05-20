package com.epherical.professions.client.button;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.epherical.professions.client.widgets.CommandButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SmallIconButton extends Button {

    public boolean opened = false;
    public CommandButton.SmallIcon icon;
    protected final ButtonPress<SmallIconButton> onPress;


    public SmallIconButton(int i, int j, int k, int l, Component component, CommandButton.SmallIcon icon, ButtonPress<SmallIconButton> onPress) {
        super(i, j, k, l, component, button -> {
        }, Button.DEFAULT_NARRATION);
        this.onPress = onPress;
        this.icon = icon;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        onPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage();
        if (i == 1) {
            i = 0;
        } else {
            i = 1;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        //this.blit(poseStack, this.x, this.y, i * 16, 187, 16, 16);
        poseStack.blit(CommonDataScreen.WINDOW_LOCATION, getX() + 3, getY() - 2, icon.ordinal() * 16, 240, this.width, this.height);
        if (this.isHoveredOrFocused()) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
                screen.setTooltipForNextRenderPass(this.getTooltip(), this.createTooltipPositioner(), this.isFocused());
            }
        }
    }

    private int getYImage() {
        int $$0 = 1;
        if (!this.active) {
            $$0 = 0;
        } else if (this.isHoveredOrFocused()) {
            $$0 = 2;
        }

        return 46 + $$0 * 20;
    }

    public boolean isOpened() {
        return opened;
    }
}
