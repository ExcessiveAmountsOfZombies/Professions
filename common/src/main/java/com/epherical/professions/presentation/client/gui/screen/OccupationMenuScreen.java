package com.epherical.professions.presentation.client.gui.screen;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.presentation.client.gui.components.OccupationList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class OccupationMenuScreen extends Screen {

    private int imageWidth = 214;
    private int imageHeight = 238;


    private int leftPos;
    private int topPos;

    private OccupationList occupationList;


    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_menu");

    public OccupationMenuScreen() {
        super(Component.literal("Occupation Menu"));
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


        occupationList = new OccupationList(this.minecraft, 96, topPos + 42, 228 + topPos, 33);
        occupationList.setX(leftPos + 6);
        addRenderableWidget(occupationList);
    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blitSprite(TEXTURE, leftPos, topPos, imageWidth, imageHeight);
    }
}
