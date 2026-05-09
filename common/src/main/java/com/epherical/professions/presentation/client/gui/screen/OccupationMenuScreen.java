package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.C2SOccupationExperienceTrackingPayload;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import com.epherical.professions.presentation.client.gui.components.OccupationList;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

import static com.epherical.professions.presentation.client.gui.components.OccupationList.Entry.PROGRESS_BAR_EMPTY;
import static com.epherical.professions.presentation.client.gui.components.OccupationList.Entry.PROGRESS_BAR_FULL;
import static com.epherical.professions.presentation.client.gui.screen.OccupationCategorySelectionScreen.SPRITES;

public class OccupationMenuScreen extends Screen {

    private int imageWidth = 320;
    private int imageHeight = 239;


    private int leftPos;
    private int topPos;

    private OccupationList occupationList;

    private OccupationMenuButton occupationMenuButton;
    private OccupationMenuButton closeButton;
    private OccupationMenuButton trackButton;
    private OccupationMenuButton perkButton;
    private boolean trackEnabled;
    private ResourceLocation trackedProfessionId;


    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/main_menu/occupation_menu");

    private final List<Occupation> occupations;

    public OccupationMenuScreen(List<Occupation> occupations) {
        super(Component.literal("Occupation Menu"));
        this.occupations = occupations;
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


        occupationList = new OccupationList(this.minecraft, 117, topPos + 28, 214 + topPos, 32, occupations);
        occupationList.setX(leftPos + 6);
        addRenderableWidget(occupationList);
        // todo; add translation
        occupationMenuButton = addRenderableWidget(OccupationMenuButton.omButton(Component.literal("Details"), button -> {
            minecraft.setScreen(new OccupationInfoScreen(occupationList.getSelected().getOccupation()));
        }).pos(leftPos + 150, topPos + 177).size(74, 18).build());
        occupationMenuButton.visible = false;

        trackButton = addRenderableWidget(OccupationMenuButton.omToggleButton(Component.literal("Track"), pButton -> {
            Occupation selectedOccupation = getSelectedOccupation();
            if (selectedOccupation == null) {
                return;
            }
            trackEnabled = !trackEnabled;
            selectedOccupation.setExperienceGainTrackingEnabled(trackEnabled);
            NetworkPayloadDispatcher.sendToServer(new C2SOccupationExperienceTrackingPayload(selectedOccupation.getProfessionKey(), trackEnabled));
        }, () -> trackEnabled).pos(leftPos + 150 + 76, topPos + 104 + 80).size(74, 18)
                .tooltip(Tooltip.create(Component.literal("Display XP gains as they happen. (Toggle)")))
                .toggleTextOffset(20).build());

        trackButton.visible = false;

        perkButton = addRenderableWidget(OccupationMenuButton.omButton(Component.literal("Perks"), pButton -> {
            minecraft.setScreen(new OccupationPerkMenuScreen(occupationList.getSelected().getOccupation()));
        }).pos(leftPos + 150, topPos + 196).size(74, 18).build());

        perkButton.visible = false;

        closeButton = OccupationMenuButton.omButton(Component.literal(""), button -> {
                    minecraft.setScreen(null);
                }).pos(leftPos + 296, topPos + 2).size(18, 18)
                .background(SPRITES)
                .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.literal("Close Menu")))
                .build();

        addRenderableWidget(closeButton);


    }


    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gfx, pMouseX, pMouseY, pPartialTick);

        RenderHelperUtil.drawScaledString(gfx, font, "Profession Tracker", leftPos + 5, topPos + 5, 1.5f, 0xd5af47, false);


        if (occupationList.getSelected() != null) {
            syncTrackStateFromSelection();
            occupationMenuButton.visible = true;
            trackButton.visible = true;
            perkButton.visible = true;

            OccupationList.Entry selected = occupationList.getSelected();

            RenderHelperUtil.drawScaledString(gfx, minecraft.font, "Selected Overview", leftPos + 150, topPos + 32, 1.5f, 0x6f4d15, false);

            RenderHelperUtil.drawScaled(gfx, leftPos + 156, topPos + 50, 2f, () -> {
                gfx.renderItem(occupationList.getProfessionIcon(), 0, 0);
            });

            gfx.drawString(minecraft.font, occupationList.getProfessionName(), leftPos + 200, topPos + 50, 0xFFFFFF);


            int pX = leftPos + 200;
            int pY = topPos + 60;

            RenderHelperUtil.drawScaled(gfx, pX, pY, 0.8f, () -> {
                int translateY = 0;
                for(FormattedCharSequence seq : occupationList.getOrderedDescription()) {
                    gfx.drawString(minecraft.font, seq, 0, translateY, 0x6f4d15, false);
                    translateY += 10;
                }
            });

            int rgb = selected.getProfession().professionColor().getValue();

            RenderHelperUtil.drawScaledString(gfx, minecraft.font, "Progress", leftPos + 150, topPos + 90, 0.75f, 0x6f4d15, false);


            gfx.drawString(minecraft.font, String.format("Level %s", occupationList.getSelected().getOccupation().getLevel()), leftPos + 150, topPos + 99, rgb, true);


            double placeholderPercentage = getSelectedOccupation().getExpProgress() / getSelectedOccupation().getMaxExperience();
            float clampedPercentage = Mth.clamp((float) placeholderPercentage, 0.0f, 1.0f);

            int barX = leftPos + 150;
            int barY = topPos + 110;
            gfx.blitSprite(PROGRESS_BAR_EMPTY, barX, barY, 150, 7);

            int filledWidth = Mth.floor(150 * clampedPercentage);

            float red = ((rgb >> 16) & 0xFF) / 255.0f;
            float green = ((rgb >> 8) & 0xFF) / 255.0f;
            float blue = (rgb & 0xFF) / 255.0f;

            if (filledWidth > 0) {
                gfx.setColor(red, green, blue, 1.0f);
                gfx.blitSprite(PROGRESS_BAR_FULL, barX, barY, filledWidth, 7);
                //gfx.blitSprite(PROGRESS_BAR_FULL, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, 0, 0, barX, barY, filledWidth, PROGRESS_BAR_HEIGHT);
                gfx.setColor(1f, 1f, 1f, 1.2f);
            }

            RenderHelperUtil.drawScaledString(gfx, minecraft.font, String.format(" %.2f / %.2f XP", getSelectedOccupation().getExpProgress(), getSelectedOccupation().getMaxExperience()), leftPos + 190, topPos + 120, 0.75f, 0x6f4d15, false);


            RenderHelperUtil.drawScaledString(gfx, minecraft.font, "Base Rewards (WIP) (exp, abilities etc)", leftPos + 150, topPos + 130, 0.66f, 0x6f4d15, false);

            RenderHelperUtil.drawScaledString(gfx, minecraft.font, "Extras", leftPos + 150, topPos + 170, 0.75f, 0x6f4d15, false);


        } else {
            occupationMenuButton.visible = false;
            trackButton.visible = false;
        }

    }

    private Occupation getSelectedOccupation() {
        OccupationList.Entry selected = occupationList.getSelected();
        return selected == null ? null : selected.getOccupation();
    }

    private void syncTrackStateFromSelection() {
        Occupation selectedOccupation = getSelectedOccupation();
        if (selectedOccupation == null) {
            return;
        }

        ResourceLocation selectedProfessionId = selectedOccupation.getProfessionKey();
        if (!selectedProfessionId.equals(trackedProfessionId)) {
            trackedProfessionId = selectedProfessionId;
            trackEnabled = selectedOccupation.isExperienceGainTrackingEnabled();
        }
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }
}
