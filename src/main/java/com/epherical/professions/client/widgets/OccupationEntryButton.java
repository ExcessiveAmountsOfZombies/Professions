package com.epherical.professions.client.widgets;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
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

public class OccupationEntryButton extends Button implements Selectable, HoldsProfession {

    private final Occupation occupation;
    private final Component name;

    private boolean selected = false;

    public OccupationEntryButton(Occupation occupation, int i, int j, int k, int l, OnPress onPress, OnTooltip tooltip) {
        super(i, j, k, l, Component.nullToEmpty(""), onPress, tooltip);
        this.occupation = occupation;
        this.name = new TextComponent(occupation.getProfession().getDisplayName()).setStyle(Style.EMPTY.withColor(occupation.getProfession().getColor()));
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = getYFactor();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, 58, 148 + i * 24, this.width, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 149 - this.width / 2, 148 + i * 24, this.width / 2, this.height);
        drawProgression(poseStack);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawString(poseStack, font, name, this.x + 5, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        drawCenteredString(poseStack, font, "" + occupation.getLevel(), this.x + this.width - 11, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    private void drawProgression(PoseStack stack) {
        double percentage = occupation.getExp() / occupation.getMaxExp();
        int progress = (int) (percentage * 16);
        this.blit(stack, this.x + this.width - 19, this.y + 2, 16, 238, 16, 18);
        this.blit(stack, this.x + this.width - 19, this.y + 2, 16 * 2, 238, progress, 18);
        /*int progress = (int) (percentage * 360);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        int segments = 360 + 90;
        double twoPI = Math.PI * 2;

        double innerRad = 6;
        double outerRad = innerRad + 3;

        double centerX = this.x + this.width - 11.2;
        double centerY = this.y + 12;

        for (int j = 90; j < segments; j++) {
            double sin = Math.sin(-(j * twoPI / 360));
            double cos = Math.cos(-(j * twoPI / 360));

            if (j >= (progress + 90)) {
                bufferBuilder.vertex(centerX + (innerRad * cos), centerY + (innerRad * sin), getBlitOffset()).color(56, 56, 56, 255).endVertex();
                bufferBuilder.vertex(centerX + (outerRad * cos), centerY + (outerRad * sin), getBlitOffset()).color(56, 56, 56, 255).endVertex();
            } else {
                bufferBuilder.vertex(centerX + (innerRad * cos), centerY + (innerRad * sin), getBlitOffset()).color(52, 235, 64, 255).endVertex();
                bufferBuilder.vertex(centerX + (outerRad * cos), centerY + (outerRad * sin), getBlitOffset()).color(52, 235, 64, 255).endVertex();
            }
        }
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);*/
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getYFactor() {
        int value = getYImage(this.isHoveredOrFocused());
        if (selected) {
            value += 2;
            if (this.isHoveredOrFocused()) {
                value -= 2;
            }
        }
        return value;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    @Override
    public Profession getProfession() {
        return occupation.getProfession();
    }
}
