package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.presentation.client.gui.components.OccupationCategoryList;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class OccupationCategorySelectionScreen extends Screen {

    private final int imageWidth = 320;
    private final int imageHeight = 238;

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/category/selection_menu");
    public static final ResourceLocation INFO_ICON = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/blue_i");


    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_icon_button"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button_disabled"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_icon_button_highlighted"));


    private int leftPos;
    private int topPos;


    private OccupationCategoryList occupationInfoList;
    private final List<ProfessionCategory> categories;
    private OccupationMenuButton closeButton;
    private OccupationMenuButton confirmSelection;


    public OccupationCategorySelectionScreen(List<ProfessionCategory> categories) {
        super(Component.literal("Occupation Category Selection"));
        this.categories = categories;
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


        closeButton = OccupationMenuButton.omButton(Component.literal(""), button -> {
            minecraft.setScreen(null);
        }).pos(leftPos + 296, topPos + 2).size(18, 18)
                .background(SPRITES)
                .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.literal("Close Menu")))
                .build();

        confirmSelection = OccupationMenuButton.omButton(Component.literal("Confirm Selection"), pButton -> {
            // todo; we need to finish this and assign the category to the profession player
            minecraft.setScreen(null);
        }).pos(leftPos + 220, topPos + 211).size(90, 16)
                .build();

        confirmSelection.active = false;


        occupationInfoList = new OccupationCategoryList(this.minecraft, 307, topPos + 40, 207 + topPos, 48, categories);
        occupationInfoList.setX(leftPos + 6);
        addRenderableWidget(occupationInfoList);
        addRenderableWidget(closeButton);
        addRenderableWidget(confirmSelection);
       /* occupationMenuButton = addRenderableWidget(OccupationMenuButton.omButton(Component.literal("Details"), button -> {
            minecraft.setScreen(new OccupationInfoScreen(occupationList.getSelected().getOccupation()));
        }).pos(leftPos + 112, topPos + 108).size(94, 24).build());
        occupationMenuButton.visible = false;*/
    }


    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gfx, pMouseX, pMouseY, pPartialTick);


        drawScaledString(gfx, font, "Profession Progression Picker", leftPos + 5, topPos + 5, 1.5f, 0xd5af47, false);
        // todo; translation
        drawScaledString(gfx, font, "Select the progression system you'll use for your world.", leftPos + 8, topPos + 29, 0.75f, 0x777777, false);



        drawScaled(gfx, leftPos + 8, topPos + 209, 0.75f, () -> {
            gfx.blitSprite(INFO_ICON, 0, 0, 18, 18);
        });


        if (occupationInfoList.getProfessionCategory() != null) {
            confirmSelection.active = true;
        }

        // todo; translation
        drawWrappedScaledString(gfx, font, "You can only have one progression system per world. You can only change this with a command later!!", leftPos + 23, topPos + 210, 0.5f, 400, 0x777777);
    }

    private void drawScaled(GuiGraphics gfx, int x, int y, float scale, Runnable voidConsumer) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        voidConsumer.run();
        gfx.pose().popPose();
    }

    private void drawScaledString(GuiGraphics gfx, Font font, String text, int x, int y, float scale,
                                  int color, boolean dropShadow) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawString(font, text, 0, 0, color, dropShadow);
        gfx.pose().popPose();
    }

    private void drawWrappedScaledString(GuiGraphics gfx, Font font, String text, int x, int y, float scale, int lineWidth, int color) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawWordWrap(font, FormattedText.of(text), 0, 0, lineWidth, color);
        gfx.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }
}
