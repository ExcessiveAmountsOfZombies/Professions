package com.epherical.professions.client.widgets;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import static com.epherical.professions.client.screen.OccupationScreen.WINDOW_LOCATION;

public class OccupationEntryButton extends Button implements Selectable, HoldsProfession {

    private final Occupation occupation;
    private final Component name;

    private boolean selected = false;

    public OccupationEntryButton(Occupation occupation, int i, int j, int k, int l, OnPress onPress) {
        super(i, j, k, l, Component.nullToEmpty(""), onPress, Button.DEFAULT_NARRATION);
        this.occupation = occupation;
        this.name = Component.literal(occupation.getProfession().getDisplayName()).setStyle(Style.EMPTY.withColor(occupation.getProfession().getColor()));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = getYFactor();
        graphics.blit(WINDOW_LOCATION, getX(), getY(), 58, 148 + i * 24, this.width, this.height);
        graphics.blit(WINDOW_LOCATION, getX() + this.width / 2, getY(), 149 - this.width / 2, 148 + i * 24, this.width / 2, this.height);
        drawProgression(graphics);
        //this.renderBg(poseStack, minecraft, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        graphics.drawString(font, name, getX() + 5, getYFactor() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        graphics.drawCenteredString(font, "" + occupation.getLevel(), getX() + this.width - 11, getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        if (this.isHoveredOrFocused()) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
                screen.setTooltipForNextRenderPass(this.getTooltip(), this.createTooltipPositioner(), this.isFocused());
            }
        }
    }

    private void drawProgression(GuiGraphics stack) {
        double percentage = occupation.getExp() / occupation.getMaxExp();
        int progress = (int) (percentage * 16);
        stack.blit(WINDOW_LOCATION, getX() + this.width - 19, getY() + 2, 16, 238, 16, 18);
        stack.blit(WINDOW_LOCATION, getX() + this.width - 19, getY() + 2, 16 * 2, 238, progress, 18);
        /*int progress = (int) (percentage * 360);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        int segments = 360 + 90;
        double twoPI = Math.PI * 2;

        double innerRad = 6;
        double outerRad = innerRad + 3;

        double centerX = getX() + this.width - 11.2;
        double centerY = getY() + 12;

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
        int value = getTextureY();
        if (selected) {
            value += 2;
            if (this.isHoveredOrFocused()) {
                value -= 2;
            }
        }
        return value;
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    @Override
    public Profession getProfession() {
        return occupation.getProfession();
    }
}
