package com.epherical.professions.client.gui.screen;

import com.epherical.professions.Constants;
import com.epherical.professions.client.gui.components.OccupationInfoList;
import com.epherical.professions.core.rewards.Reward;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class OccupationInfoScreen extends Screen {


    private int imageWidth = 214;
    private int imageHeight = 238;

    private int leftPos;
    private int topPos;


    private OccupationInfoList occupationInfoList;


    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "occupation/occupation_info");

    public OccupationInfoScreen() {
        super(Component.literal("Occupation Info"));
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        occupationInfoList = new OccupationInfoList(this.minecraft, 93, topPos + 43, 229 + topPos, 18);
        occupationInfoList.setX(leftPos + 9);
        addRenderableWidget(occupationInfoList);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        OccupationInfoList.Entry focused = occupationInfoList.getHovered();
        if (focused != null) {
            float s = 0.75f;
            int paintX = leftPos + 115;
            int paintY = topPos + 89;

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(paintX, paintY, 0);
            pGuiGraphics.pose().scale(s, s, 1);
            pGuiGraphics.pose().translate(-paintX, -paintY, 0);
            pGuiGraphics.drawString(
                    minecraft.font,
                    "Actions to earn",
                    paintX, paintY,
                    0xFF025E66, false);
            pGuiGraphics.pose().popPose();


            s = 2.5f;
            paintX = leftPos + 141;
            paintY = topPos + 23;
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(paintX, paintY, 0);
            pGuiGraphics.pose().scale(s, s, 1);
            pGuiGraphics.pose().translate(-paintX, -paintY, 0);
            pGuiGraphics.renderFakeItem(focused.getHolder(), paintX, paintY, 1000);
            pGuiGraphics.pose().popPose();


            int boxL = leftPos + 115;
            int boxR = leftPos + 200;
            int boxT = paintY + 46;
            int boxB = topPos + 86;

            drawLabelAutoScale(pGuiGraphics,
                    minecraft.font,
                    focused.getHolder().getHoverName(),
                    boxL, boxT, boxR, boxB,
                    0xFF025E66);


            paintX = leftPos + 115;
            paintY = topPos + 100;
            s = 0.75f;

            // todo; we'll turn this into a button object or something in the future

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(paintX, paintY, 0);
            pGuiGraphics.pose().scale(s, s, 1);
            pGuiGraphics.pose().translate(-paintX, -paintY, 0);
            pGuiGraphics.renderFakeItem(focused.getActionItem(), paintX, paintY, 1000);

            paintX = leftPos + 134;
            paintY += 4;

            pGuiGraphics.drawString(minecraft.font,
                    Component.translatable(focused.getAction().getType().translationKey()),
                    paintX, paintY, 0xFFFFFFFF, false);

            pGuiGraphics.pose().popPose();


            paintX = leftPos + 115;
            paintY += 16;

            pGuiGraphics.drawString(minecraft.font,
                    "Rewards", paintX, paintY, 0xFF025E66, false);


            paintX = leftPos + 112;
            paintY += 16;

            /*for (Reward reward : focused.getAction().getRewards()) {
                pGuiGraphics.renderFakeItem(reward.getRewardIcon(), paintX, paintY, 1000);
                pGuiGraphics.drawString(minecraft.font, reward.getRewardName(), paintX + 18, paintY + 6, 0xFFFFFFFF, true);
                paintY+= 16;
            }*/

        }
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }


    public static void drawLabelAutoScale(GuiGraphics gfx,
                                          Font font,
                                          Component msg,
                                          int left, int top,
                                          int right, int bottom,
                                          int colour) {

        int boxW = right - left;
        int boxH = bottom - top;

        List<FormattedCharSequence> lines = font.split(msg, boxW);

        int widest = lines.stream().mapToInt(font::width).max().orElse(0);
        int totalH = lines.size() * font.lineHeight;

        float scaleX = widest > boxW ? boxW / (float) widest : 1.0f;
        float scaleY = totalH > boxH ? boxH / (float) totalH : 1.0f;
        float scale = Math.min(scaleX, scaleY);      // uniform scale; never >1

        int scaledW = (int) (widest * scale);
        int scaledH = (int) (totalH * scale);

        int startX = left + (boxW - scaledW) / 2;
        int startY = top + (boxH - scaledH) / 2;

        gfx.pose().pushPose();
        gfx.pose().translate(startX, startY, 0);
        gfx.pose().scale(scale, scale, 1);

        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);
            int w = font.width(line);
            int dx = (widest - w) / 2;
            int dy = i * font.lineHeight;
            gfx.drawString(font, line, dx, dy, colour, false);
        }

        gfx.pose().popPose();
    }
}
