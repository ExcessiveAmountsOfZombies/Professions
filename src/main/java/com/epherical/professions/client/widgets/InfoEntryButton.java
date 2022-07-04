package com.epherical.professions.client.widgets;

import com.epherical.professions.util.ActionDisplay;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.epherical.professions.client.screen.OccupationScreen.WINDOW_LOCATION;

public class InfoEntryButton extends AbstractEntryButton {

    private final ItemStack item;
    private final ActionDisplay.Icon icon;

    public InfoEntryButton(ActionDisplay.Icon component, int i, int j, int k, int l, OnPress onPress, OnTooltip tooltip) {
        super(component.getName(), i, j, k, l, onPress, tooltip);
        item = new ItemStack(component.getRepresentation());
        this.icon = component;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
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
        this.blit(poseStack, this.x, this.y, 154 + i * width, 195, this.width, this.height);
        minecraft.getItemRenderer().renderGuiItem(item, this.x, this.y);
    }

    public void drawText(PoseStack poseStack, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, icon.getActionType(), (float) (x / 2), (float) (y / 2) - 28, 0);
        List<FormattedCharSequence> nameSplit = font.split(icon.getName(), 55);
        for (int i = 0; i < nameSplit.size(); i++) {
            font.drawShadow(poseStack, nameSplit.get(i), (x / 2) - 18, y / 2 - 8 + (i * 10), 0);
        }
        int i = 0;
        for (Component text : icon.getActionInformation().getSiblings()) {
            font.drawShadow(poseStack, text, (float) (x / 2) + 55, (float) (y / 2) - 8 + (i * 10), 0);
            i++;
        }

    }
}
