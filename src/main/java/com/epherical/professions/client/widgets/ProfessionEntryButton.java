package com.epherical.professions.client.widgets;

import com.epherical.professions.profession.Profession;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

import static com.epherical.professions.client.screen.OccupationScreen.WINDOW_LOCATION;

public class ProfessionEntryButton extends Button {

    private final Profession profession;
    private final Component name;

    public ProfessionEntryButton(Profession profession, int i, int j, int k, int l, OnPress onPress, OnTooltip tooltip) {
        super(i, j, k, l, Component.nullToEmpty(""), onPress, tooltip);
        this.profession = profession;
        this.name = new TextComponent(profession.getDisplayName()).setStyle(Style.EMPTY.withColor(profession.getColor()));
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
        this.blit(poseStack, this.x, this.y, 58, 148 + i * 24, this.width, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 149 - this.width / 2, 148 + i * 24, this.width / 2, this.height);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawString(poseStack, font, name, this.x + 5, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }
}
