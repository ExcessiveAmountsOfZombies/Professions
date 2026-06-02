package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.actions.GateRequirement;
import com.epherical.professions.api.client.GateRenderer;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.GateReportData;
import com.epherical.professions.model.gating.GateType;
import com.epherical.professions.model.gating.requirements.GateRequirementType;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import com.epherical.professions.presentation.client.gui.components.OccupationGateList;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.epherical.professions.presentation.client.RenderHelperUtil.drawScaled;
import static com.epherical.professions.presentation.client.RenderHelperUtil.drawWrappedScaledString;
import static com.epherical.professions.presentation.client.gui.screen.OccupationPerkMenuScreen.INFO_ICON;

public class OccupationGateScreen extends Screen  {

    public static final ResourceLocation MENU_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/gates/menu");


    private final int imageWidth = 320;
    private final int imageHeight = 238;

    private int leftPos;
    private int topPos;


    private final Occupation occupation;

    private OccupationGateList gateList;


    private OccupationMenuButton closeButton;


    private Screen parent;

    protected OccupationGateScreen(Occupation occupation, Screen currentScreen) {
        super(Component.translatable("professions.screen.occupation_gate_menu.title"));
        this.occupation = occupation;
        this.parent = currentScreen;
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
                .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/red_x"))
                .tooltip(Tooltip.create(Component.translatable("professions.screen.common.close_menu")))
                .build();

        addRenderableWidget(OccupationMenuButton.omButton(Component.empty(), pButton -> {
                            minecraft.setScreen(parent);
                        }).pos(leftPos + 296 - 20, topPos + 2).size(18, 18)
                        .background(OccupationCategorySelectionScreen.SPRITES)
                        .icon(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/icons/grey_back"))
                        .tooltip(Tooltip.create(Component.translatable("professions.screen.common.back"))).build()
        );


        gateList = new OccupationGateList(this.minecraft, 130, topPos + 26, 214 + topPos, 32, occupation);
        gateList.setX(leftPos + 5);
        try {
            gateList.setSelected(gateList.getFirstElement());
        } catch (IndexOutOfBoundsException ignored) {}

        addRenderableWidget(gateList);
        addRenderableWidget(closeButton);


        setInitialFocus(gateList);

    }


    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gfx, pMouseX, pMouseY, pPartialTick);

        RenderHelperUtil.drawScaledString(gfx, font,
                Component.translatable("professions.screen.occupation_gate_menu.header"),
                leftPos + 5, topPos + 5, 1.5f, 0xd5af47, false);

        if (gateList.getSelected() != null) {
            RenderHelperUtil.drawScaledString(gfx, minecraft.font,
                    Component.translatable("professions.screen.occupation_gate_menu.selected_overview"),
                    leftPos + 155, topPos + 34, 1.25f, 0x6f4d15, false);


            ResourceLocation background = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/gates/selected_background_icon");
            gfx.blitSprite(background, leftPos + 155, topPos + 50, 32, 32);

            Component hoverName = gateList.getSelected().getCurrentTargetDisplay().icon().getHoverName();

            int size = gateList.getSelected().getTargetDisplays().size();

            Component msg;
            // todo; this piece of text should be based on the gate type, Unlock the ability to break, place, use etc.
            if (size > 1) {
                msg = Component.translatable("Unlock the ability to use %s and %s other items", hoverName, size - 1);
            } else {
                msg = Component.translatable("Unlock the ability to use %s", hoverName);
            }


            RenderHelperUtil.drawWrappedScaledString(gfx, minecraft.font, msg, leftPos + 155 + 34, topPos + 50, 0.8f, 150, 0x6f4d15);


            RenderHelperUtil.drawScaled(gfx, leftPos + 155, topPos + 50, 2f, () -> {
                gfx.renderFakeItem(gateList.getSelected().getCurrentTargetDisplay().icon(), 0, 0);
            });

            ResourceLocation menuButton = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button");
            gfx.blitSprite(menuButton, leftPos + 155, topPos + 84, 75, 20);
            RenderHelperUtil.drawScaledString(gfx, minecraft.font, "Type", leftPos + 160, topPos + 87, 0.75f, 0xFFFFFF, false);
            RenderHelperUtil.drawScaledString(gfx, minecraft.font, gateList.getSelected().getGateTypeLine(), leftPos + 160, topPos + 94, 0.8f, 0x6f4d15, false);


            gfx.blitSprite(menuButton, leftPos + 155 + 77, topPos + 84, 75, 20);
            RenderHelperUtil.drawScaledString(gfx, minecraft.font, "Status", leftPos + 160 + 77, topPos + 87, 0.75f, 0xFFFFFF, false);
            boolean allowed = gateList.getSelected().getGateReport().isAllowed();
            RenderHelperUtil.drawScaledString(gfx, minecraft.font, allowed ? "Unlocked" : "Locked", leftPos + 160 + 77, topPos + 94, 0.8f, allowed ? 0x00FF00 : 0xFF0000, false);

            RenderHelperUtil.drawScaledString(gfx, minecraft.font, Component.translatable("Requirements"),
                    leftPos + 155, topPos + 84 + 22, 1f, 0x6f4d15, false);


            Multimap<GateType, GateReportData> failuresMap = gateList.getSelected().getGateReport().getFailuresMap();
            Multimap<GateType, GateReportData> successMap = gateList.getSelected().getGateReport().getSuccessMap();


            int x = leftPos + 155;
            int y = topPos + 84 + 22 + 10;
            int width = 152;
            int height = 20;
            for (GateReportData value : successMap.values()) {
                gfx.blitSprite(menuButton, x, y, width, height);
                GateRenderer renderer = GateRenderer.getRenderer(value.gateRequirement().getRequirementType());
                if (renderer != null) {
                    renderer.render(gfx, minecraft, true, x, y, width, height, gateList.getSelected().getPlayer(), occupation, gateList.getSelected().getContext(), value.gateRequirement());
                } else {
                    gfx.drawString(minecraft.font, "Missing renderer for: " + value.gateRequirement().getClass().getName(), x + 4, y + 2, 0xFF0000);
                }
                y += height + 2;
            }

            for (GateReportData value : failuresMap.values()) {
                gfx.blitSprite(menuButton, x, y, width, height);
                GateRenderer renderer = GateRenderer.getRenderer(value.gateRequirement().getRequirementType());
                if (renderer != null) {
                    renderer.render(gfx, minecraft, false, x, y, width, height, gateList.getSelected().getPlayer(), occupation, gateList.getSelected().getContext(), value.gateRequirement());
                } else {
                    gfx.drawString(minecraft.font, "Missing renderer for: " + value.gateRequirement().getClass().getName(), x + 4, y + 2, 0xFF0000);
                }
                y += height + 2;
            }

            drawScaled(gfx, leftPos + 8, topPos + 217, 0.75f, () -> {
                gfx.blitSprite(INFO_ICON, 0, 0, 18, 18);
            });


            drawWrappedScaledString(gfx, font,
                    Component.translatable("The progression locker restricts particular actions until all requirements are met."),
                    leftPos + 23, topPos + 219, 0.75f, 320 - 20, 0x777777);
        }

    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(MENU_TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }
}
