package com.epherical.professions.client.widgets;

import com.epherical.professions.util.ActionDisplay;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.epherical.professions.client.screen.OccupationScreen.WINDOW_LOCATION;

public class InfoEntryButton extends AbstractEntryButton {

    private final ItemStack item;
    private final ActionDisplay.Icon icon;

    public InfoEntryButton(ActionDisplay.Icon component, int i, int j, int k, int l, OnPress onPress, Tooltip tooltip) {
        super(component.getName(), i, j, k, l, onPress, tooltip);
        item = component.getRepresentation();
        this.icon = component;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int i = this.getYImage();
        if (i == 1) {
            i = 0;
        } else {
            i = 1;
        }
        graphics.blit(WINDOW_LOCATION, getX(), getYImage(), 154 + i * width, 195, this.width, this.height);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.renderItem(item, getX(), getY());
    }

    public void drawText(GuiGraphics poseStack, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, icon.getActionType(), (float) (x / 2) + 20, (float) (y / 2) - 28, 0);
        List<FormattedCharSequence> nameSplit = font.split(icon.getName(), 130);
        for (int i = 0; i < nameSplit.size(); i++) {
            font.drawShadow(poseStack, nameSplit.get(i), (float) (x / 2) - 20, (float) (y / 2) - 8 + (i * 10), 0);
        }
        int i = 0;
        for (Component text : icon.getActionInformation().getSiblings()) {
            font.drawShadow(poseStack, text, (float) (x / 2) - 20, (float) (y / 2) + 24 + (i * 10), 0);
            i++;
        }
        font.drawShadow(poseStack, "Rewards", (float) (x / 2) - 20, (float) (y / 2) + 13, 0xFFFFFF);

    }
}
