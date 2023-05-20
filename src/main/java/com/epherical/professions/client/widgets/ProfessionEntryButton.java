package com.epherical.professions.client.widgets;

import com.epherical.professions.profession.Profession;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class ProfessionEntryButton extends Button {

    private final Profession profession;
    private final Component name;
    // TODO; rewrite most widgets now that buttons are changed, the code is blegh

    public ProfessionEntryButton(Profession profession, int i, int j, int k, int l, OnPress onPress) {
        super(i, j, k, l, Component.nullToEmpty(""), onPress, Button.DEFAULT_NARRATION);
        this.profession = profession;
        this.name = Component.literal(profession.getDisplayName()).setStyle(Style.EMPTY.withColor(profession.getColor()));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        /*Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        graphics.blit(this.getX(), this.getY(), 58, 148 + i * 24, this.width, this.height);
        graphics.blit(this.getX() + this.width / 2, this.getX(), 149 - this.width / 2, 148 + i * 24, this.width / 2, this.height);
        this.renderBg(minecraft, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawString(font, name, this.x + 5, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(mouseX, mouseY);
        }*/
    }
}
