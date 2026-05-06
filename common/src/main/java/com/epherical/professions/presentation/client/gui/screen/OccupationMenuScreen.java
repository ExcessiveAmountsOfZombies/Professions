package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.C2SOccupationExperienceTrackingPayload;
import com.epherical.professions.presentation.client.gui.components.OccupationList;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static com.epherical.professions.presentation.client.gui.screen.OccupationCategorySelectionScreen.SPRITES;
import static com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton.NO_HIGHLIGHT_SPRITES;

public class OccupationMenuScreen extends Screen {

    private int imageWidth = 214;
    private int imageHeight = 238;


    private int leftPos;
    private int topPos;

    private OccupationList occupationList;

    private OccupationMenuButton occupationMenuButton;
    private OccupationMenuButton closeButton;
    private OccupationMenuButton trackButton;
    private boolean trackEnabled;
    private ResourceLocation trackedProfessionId;


    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_menu");

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


        occupationList = new OccupationList(this.minecraft, 96, topPos + 42, 228 + topPos, 33, occupations);
        occupationList.setX(leftPos + 6);
        addRenderableWidget(occupationList);
        // todo; add translation
        occupationMenuButton = addRenderableWidget(OccupationMenuButton.omButton(Component.literal("Details"), button -> {
            minecraft.setScreen(new OccupationInfoScreen(occupationList.getSelected().getOccupation()));
        }).pos(leftPos + 112, topPos + 108).size(94, 24).build());
        occupationMenuButton.visible = false;

        trackButton = addRenderableWidget(OccupationMenuButton.omToggleButton(Component.literal("Track"), pButton -> {
            Occupation selectedOccupation = getSelectedOccupation();
            if (selectedOccupation == null) {
                return;
            }
            trackEnabled = !trackEnabled;
            selectedOccupation.setExperienceGainTrackingEnabled(trackEnabled);
            NetworkPayloadDispatcher.sendToServer(new C2SOccupationExperienceTrackingPayload(selectedOccupation.getProfessionKey(), trackEnabled));
        }, () -> trackEnabled).pos(leftPos + 112, topPos + 108 + 26).size(56, 24)
                .background(NO_HIGHLIGHT_SPRITES)
                .toggleTextOffset(20).build());

        trackButton.visible = false;

        closeButton = OccupationMenuButton.omButton(Component.literal(""), button -> {
                    minecraft.setScreen(null);
                }).pos(leftPos + 193, topPos + 1).size(18, 18)
                .background(SPRITES)
                .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.literal("Close Menu")))
                .build();

        addRenderableWidget(closeButton);


    }


    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gfx, pMouseX, pMouseY, pPartialTick);

        if (occupationList.getSelected() != null) {
            syncTrackStateFromSelection();
            occupationMenuButton.visible = true;
            trackButton.visible = true;

            OccupationList.Entry selected = occupationList.getSelected();
            gfx.drawString(minecraft.font, "Selected Overview", leftPos + 114, topPos + 28, 0x6f4d15, false); // todo; add translation
            gfx.renderItem(occupationList.getProfessionIcon(), leftPos + 150, topPos + 42);
            gfx.drawCenteredString(minecraft.font, occupationList.getProfessionName(), leftPos + 155, topPos + 62, 0xFFFFFF);


            int pX = leftPos + 114;
            int pY = topPos + 76;

            for(FormattedCharSequence seq : occupationList.getOrderedDescription()) {
                gfx.drawString(minecraft.font, seq, pX, pY, 0x6f4d15, false);
                pY += 9;
            }
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
