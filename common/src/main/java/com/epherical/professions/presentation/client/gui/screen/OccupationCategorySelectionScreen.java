package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.networking.client.C2SCategorySelectionPayload;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.presentation.client.RenderHelperUtil;
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

import java.util.Collection;
import java.util.List;

import static com.epherical.professions.presentation.client.RenderHelperUtil.*;

public class OccupationCategorySelectionScreen extends Screen {

    private final int imageWidth = 320;
    private final int imageHeight = 238;

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/category/selection_menu");
    public static final ResourceLocation INFO_ICON = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/blue_i");


    public static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_icon_button"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button_disabled"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_icon_button_highlighted"));


    private int leftPos;
    private int topPos;


    private OccupationCategoryList occupationInfoList;
    private final Collection<ProfessionCategory> categories;
    private OccupationMenuButton closeButton;
    private OccupationMenuButton confirmSelection;


    public OccupationCategorySelectionScreen(Collection<ProfessionCategory> categories) {
        super(Component.translatable("professions.screen.occupation_category_selection.title"));
        this.categories = categories;
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


        closeButton = OccupationMenuButton.omButton(Component.empty(), button -> {
            minecraft.setScreen(null);
        }).pos(leftPos + 296, topPos + 2).size(18, 18)
                .background(SPRITES)
                .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.translatable("professions.screen.common.close_menu")))
                .build();

        confirmSelection = OccupationMenuButton.omButton(Component.translatable("professions.screen.occupation_category_selection.confirm_selection"), pButton -> {
            ProfessionCategory selectedCategory = occupationInfoList.getProfessionCategory();
            if (selectedCategory != null) {
                ResourceLocation categoryId = ProfessionsCommon.INSTANCE.getCategoryManager().getCategoryId(selectedCategory);
                if (categoryId != null) {
                    NetworkPayloadDispatcher.sendToServer(new C2SCategorySelectionPayload(categoryId));
                }
            }
            minecraft.setScreen(null);
        }).pos(leftPos + 220, topPos + 211).size(90, 16)
                .build();

        confirmSelection.active = false;


        occupationInfoList = new OccupationCategoryList(this.minecraft, 307, topPos + 40, 207 + topPos, 48, categories);
        occupationInfoList.setX(leftPos + 6);
        addRenderableWidget(occupationInfoList);
        addRenderableWidget(closeButton);
        addRenderableWidget(confirmSelection);
       /* occupationMenuButton = addRenderableWidget(OccupationMenuButton.omButton(Component.translatable("professions.screen.occupation_menu.details"), button -> {
            minecraft.setScreen(new OccupationInfoScreen(occupationList.getSelected().getOccupation()));
        }).pos(leftPos + 112, topPos + 108).size(94, 24).build());
        occupationMenuButton.visible = false;*/
    }


    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gfx, pMouseX, pMouseY, pPartialTick);


        drawScaledString(gfx, font, Component.translatable("professions.screen.occupation_category_selection.header"),
                leftPos + 5, topPos + 5, 1.5f, 0xd5af47, false);
        drawScaledString(gfx, font, Component.translatable("professions.screen.occupation_category_selection.subtitle"),
                leftPos + 8, topPos + 29, 0.75f, 0x777777, false);



        drawScaled(gfx, leftPos + 8, topPos + 209, 0.75f, () -> {
            gfx.blitSprite(INFO_ICON, 0, 0, 18, 18);
        });


        if (occupationInfoList.getProfessionCategory() != null) {
            confirmSelection.active = true;
        }

        drawWrappedScaledString(gfx, font, Component.translatable("professions.screen.occupation_category_selection.warning"),
                leftPos + 23, topPos + 210, 0.5f, 400, 0x777777
        );
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }
}
