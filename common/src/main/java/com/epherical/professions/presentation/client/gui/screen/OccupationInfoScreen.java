package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.actions.Reward;
import com.epherical.professions.presentation.client.gui.components.OccupationInfoList;
import com.epherical.professions.presentation.client.gui.components.OccupationXpBar;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static com.epherical.professions.presentation.client.gui.screen.OccupationCategorySelectionScreen.SPRITES;

public class OccupationInfoScreen extends Screen {

    private static final int XP_BAR_X = 115;
    private static final int XP_BAR_Y = 216;

    private int imageWidth = 214;
    private int imageHeight = 238;

    private int leftPos;
    private int topPos;


    private OccupationInfoList occupationInfoList;
    private OccupationMenuButton closeButton;
    private EditBox editBox;


    private final Occupation occupation;


    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_info");

    public OccupationInfoScreen(Occupation occupation) {
        super(Component.translatable("professions.screen.occupation_info.title"));
        this.occupation = occupation;
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        occupationInfoList = new OccupationInfoList(this.minecraft, 93, topPos + 43, 228 + topPos, 18, occupation);
        occupationInfoList.setX(leftPos + 9);
       // occupationInfoList.setY(topPos + 42);
        //occupationInfoList.setRectangle(94, 186, leftPos + 9, topPos + 42);

        addRenderableWidget(occupationInfoList);
        addRenderableOnly(createXpBar());

        closeButton = OccupationMenuButton.omButton(Component.empty(), button -> {
                    minecraft.setScreen(null);
                }).pos(leftPos + 193, topPos + 1).size(18, 18)
                .background(SPRITES)
                .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.translatable("professions.screen.common.close_menu")))
                .build();

        addRenderableWidget(closeButton);

        addRenderableWidget(OccupationMenuButton.omButton(Component.empty(), pButton -> {
                            List<Occupation> activeOccupations = ProfessionsCommon.INSTANCE.getPlayerManager()
                                    .getPlayer(minecraft.getUser().getProfileId()).getActiveOccupations();
                            minecraft.setScreen(new OccupationMenuScreen(activeOccupations));
                        }).pos(leftPos + 193 - 18, topPos + 1).size(18, 18)
                        .background(SPRITES)
                        .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/grey_back"))
                        .tooltip(Tooltip.create(Component.translatable("professions.screen.common.back"))).build()
        );

        editBox = new EditBox(minecraft.font, leftPos + 10, topPos + 26, 94, 16, Component.translatable("professions.screen.occupation_info.filter_actions"));
        editBox.setEditable(true);
        editBox.setSuggestion("Filter...");
        editBox.setBordered(false);
        editBox.setResponder(s -> {
            occupationInfoList.addEntries(s);
            editBox.setSuggestion("");
        });

        addRenderableWidget(editBox);

        setInitialFocus(occupationInfoList);
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
                    Component.translatable("professions.screen.occupation_info.activated_by_actions"),
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
                    Component.translatable("professions.screen.occupation_info.rewards"), paintX, paintY, 0xFF025E66, false);


            paintX = leftPos + 112;
            paintY += 16;

            for (Reward<?> reward : focused.getAction().getRewards()) {
                pGuiGraphics.renderFakeItem(reward.getRewardIcon(), paintX, paintY, 1000);
                pGuiGraphics.drawString(minecraft.font, reward.getRewardName(), paintX + 18, paintY + 6, 0xFFFFFFFF, true);
                paintY+= 16;
            }

        }
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }


    @Deprecated(forRemoval = true )
    private OccupationXpBar createXpBar() {
        Occupation profession = occupationInfoList.getProfession();
        double currentXp = profession.getExpProgress();
        double maxXp = profession.getMaxExperience();
        return new OccupationXpBar(currentXp, maxXp, leftPos + XP_BAR_X, topPos + XP_BAR_Y);
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
