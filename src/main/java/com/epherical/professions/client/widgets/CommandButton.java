package com.epherical.professions.client.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class CommandButton extends Button implements Hidden {

    private boolean hidden;
    private boolean small;
    private SmallIcon icon;


    public CommandButton(boolean hiddenByDefault, int i, int j, Component component, OnPress onPress, boolean small, int width, int height, SmallIcon icon) {
        super(i, j, width, height, component, onPress, Button.DEFAULT_NARRATION);
        this.hidden = hiddenByDefault;
        this.small = small;
        this.icon = icon;
    }

    public CommandButton(boolean hiddenByDefault, int i, int j, Component component, OnPress onPress) {
        this(hiddenByDefault, i, j, component, onPress, false, 38, 21, SmallIcon.GOOD);
    }

    @Override
    public void renderWidget(GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        // TODO; rewrite
        /*Minecraft minecraft = Minecraft.getInstance();
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

        if (small) {
            this.blit(poseStack, this.x, this.y, 154 + i * width, 195, this.width, this.height);
            this.blit(poseStack, this.x + 3, this.y, icon.ordinal() * 16, 179, this.width, this.height);
        } else {
            this.blit(poseStack, this.x, this.y, (154) + i * width, 172, this.width, this.height);
            this.blit(poseStack, this.x + this.width / 2, this.y, ((192 - this.width / 2)) + i * width, 172, this.width / 2, this.height);
            int j = this.active ? 16777215 : 10526880;
            drawCenteredString(poseStack, font, getMessage(), this.x + this.width / 2, (this.y + (this.height - 8) / 2) + 1, j | Mth.ceil(this.alpha * 255.0F) << 24);
        }
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }*/
        //minecraft.getItemRenderer().renderGuiItem(stack, (this.x + this.width / 2) - 8, (this.y + (this.height - 28) / 2));
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean value) {
        hidden = value;
    }

    public enum SmallIcon {
        GOOD,
        BAD,
        BACK,
        DROP_DOWN_OPEN,
        DROP_DOWN_CLOSE,
        ADD
    }
}
