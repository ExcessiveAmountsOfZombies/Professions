package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.C2SOccupationPerkClaimPayload;
import com.epherical.professions.presentation.client.gui.components.OccupationPerkList;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Set;

import static com.epherical.professions.presentation.client.RenderHelperUtil.*;


public class OccupationPerkMenuScreen extends Screen {

    private final int imageWidth = 320;
    private final int imageHeight = 238;

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perks_menu");
    public static final Identifier INFO_ICON = Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/blue_i");


    public static final WidgetSprites SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perk_menu_box"),
            Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perk_menu_box"),
            Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perk_menu_box_highlighted"));


    private int leftPos;
    private int topPos;

    private final Occupation occupation;


    private OccupationPerkList perkList;
    private OccupationMenuButton closeButton;
    private OccupationMenuButton confirmButton;


    public OccupationPerkMenuScreen(Occupation occupation) {
        super(Component.translatable("professions.screen.occupation_perk_menu.title"));
        this.occupation = occupation;
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


        closeButton = OccupationMenuButton.omButton(Component.empty(), button -> {
                    minecraft.setScreen(null);
                }).pos(leftPos + 296, topPos + 2).size(18, 18)
                .background(OccupationCategorySelectionScreen.SPRITES)
                .icon(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.translatable("professions.screen.common.close_menu")))
                .build();

        confirmButton = OccupationMenuButton.omButton(Component.translatable("professions.screen.occupation_perk_menu.confirm_selection"), pButton -> {
            Set<Identifier> highlightedPerks = perkList.getIds();
            if (!highlightedPerks.isEmpty()) {
                NetworkPayloadDispatcher.sendToServer(new C2SOccupationPerkClaimPayload(occupation.getProfessionKey(), highlightedPerks));
                Minecraft.getInstance().setScreen(null);
            }
        }).pos(leftPos + 204, topPos + 219).size(100, 13).build();

        addRenderableWidget(OccupationMenuButton.omButton(Component.empty(), pButton -> {
                            List<Occupation> activeOccupations = ProfessionsCommon.INSTANCE.getPlayerManager()
                                    .getPlayer(minecraft.getUser().getProfileId()).getActiveOccupations();
                            minecraft.setScreen(new OccupationMenuScreen(activeOccupations));
                        }).pos(leftPos + 296 - 20, topPos + 2).size(18, 18)
                        .background(OccupationCategorySelectionScreen.SPRITES)
                        .icon(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/grey_back"))
                        .tooltip(Tooltip.create(Component.translatable("professions.screen.common.back"))).build()
        );


        perkList = new OccupationPerkList(this.minecraft, 291, topPos + 44, 212 + topPos, 48, occupation);
        perkList.setX(leftPos + 10);
        perkList.setScrollAmount(0.0d); // This keeps the list from being pushed to the left for some reason.
        addRenderableWidget(perkList);
        addRenderableWidget(closeButton);
        addRenderableWidget(confirmButton);

    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int pMouseX, int pMouseY, float pPartialTick) {
        super.extractRenderState(gfx, pMouseX, pMouseY, pPartialTick);

        if (perkList.getActivatedButtons().size() > 0) {
            confirmButton.active = true;
        } else {
            confirmButton.active = false;
        }

        for (Button allButton : perkList.getAllButtons()) {
            allButton.setFocused(false);
        }

        for (Button button : perkList.getActivatedButtons()) {
            button.setFocused(true);
        }

        drawScaledString(gfx, font, Component.translatable("professions.screen.occupation_perk_menu.header"), leftPos + 5, topPos + 5, 1.5f, 0xFFd5af47, false);
        drawScaledString(gfx, font,
                Component.translatable("professions.screen.occupation_perk_menu.subtitle"), leftPos + 8, topPos + 30, 1f, 0xFF777777, false);


        drawScaled(gfx, leftPos + 8, topPos + 217, 0.75f, () -> {
            gfx.blitSprite(RenderPipelines.GUI_TEXTURED, INFO_ICON, 0, 0, 18, 18);
        });


        drawWrappedScaledString(gfx, font,
                Component.translatable("professions.screen.occupation_perk_menu.info"),
                leftPos + 23, topPos + 219, 0.75f, 200, 0xFF777777);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.extractBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }
}
