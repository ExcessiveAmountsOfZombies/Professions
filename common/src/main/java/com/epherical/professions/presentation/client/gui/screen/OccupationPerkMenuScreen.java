package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.C2SOccupationPerkClaimPayload;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import com.epherical.professions.presentation.client.gui.components.OccupationPerkList;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;


public class OccupationPerkMenuScreen extends Screen {

    private final int imageWidth = 320;
    private final int imageHeight = 238;

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perks_menu");
    public static final ResourceLocation INFO_ICON = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/blue_i");


    public static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perk_menu_box"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perk_menu_box"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/perks/perk_menu_box_highlighted"));


    private int leftPos;
    private int topPos;

    private final Occupation occupation;


    private OccupationPerkList perkList;
    private OccupationMenuButton closeButton;
    private OccupationMenuButton confirmButton;


    public OccupationPerkMenuScreen(Occupation occupation) {
        super(Component.literal("Profession Perks"));
        this.occupation = occupation;
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


        closeButton = OccupationMenuButton.omButton(Component.literal(""), button -> {
                    minecraft.setScreen(null);
                }).pos(leftPos + 296, topPos + 2).size(18, 18)
                .background(OccupationCategorySelectionScreen.SPRITES)
                .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.literal("Close Menu")))
                .build();

        confirmButton = OccupationMenuButton.omButton(Component.literal("Confirm Selection"), pButton -> {
            Set<ResourceLocation> highlightedPerks = perkList.getIds();
            if (!highlightedPerks.isEmpty()) {
                NetworkPayloadDispatcher.sendToServer(new C2SOccupationPerkClaimPayload(occupation.getProfessionKey(), highlightedPerks));
                Minecraft.getInstance().setScreen(null);
            }
        }).pos(leftPos + 204, topPos + 219).size(100, 13).build();

        addRenderableWidget(OccupationMenuButton.omButton(Component.literal(""), pButton -> {
                            List<Occupation> activeOccupations = ProfessionsCommon.INSTANCE.getPlayerManager()
                                    .getPlayer(minecraft.getUser().getProfileId()).getActiveOccupations();
                            minecraft.setScreen(new OccupationMenuScreen(activeOccupations));
                        }).pos(leftPos + 296 - 20, topPos + 2).size(18, 18)
                        .background(OccupationCategorySelectionScreen.SPRITES)
                        .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/grey_back"))
                        .tooltip(Tooltip.create(Component.literal("Back"))).build()
        );


        perkList = new OccupationPerkList(this.minecraft, 291, topPos + 44, 212 + topPos, 48, occupation);
        perkList.setX(leftPos + 8);
        addRenderableWidget(perkList);
        addRenderableWidget(closeButton);
        addRenderableWidget(confirmButton);

    }


    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gfx, pMouseX, pMouseY, pPartialTick);

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

        drawScaledString(gfx, font, "Profession Perks", leftPos + 5, topPos + 5, 1.5f, 0xd5af47, false);
        // todo; translation
        drawScaledString(gfx, font, "Claim perks to activate the ones you've unlocked.", leftPos + 8, topPos + 30, 1f, 0x777777, false);


        drawScaled(gfx, leftPos + 8, topPos + 217, 0.75f, () -> {
            gfx.blitSprite(INFO_ICON, 0, 0, 18, 18);
        });


        // todo; translation
        drawWrappedScaledString(gfx, font, "Earn Perks by leveling your profession. Click on them to select!", leftPos + 23, topPos + 219, 0.75f, 200, 0x777777);
    }

    private void drawScaled(GuiGraphics gfx, int x, int y, float scale, Runnable voidConsumer) {
        RenderHelperUtil.drawScaled(gfx, x, y, scale, voidConsumer);
    }

    private void drawScaledString(GuiGraphics gfx, Font font, String text, int x, int y, float scale,
                                  int color, boolean dropShadow) {
        RenderHelperUtil.drawScaledString(gfx, font, text, x, y, scale, color, dropShadow);
    }

    private void drawWrappedScaledString(GuiGraphics gfx, Font font, String text, int x, int y, float scale, int lineWidth, int color) {
        RenderHelperUtil.drawWrappedScaledString(gfx, font, text, x, y, scale, lineWidth, color);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }
}
