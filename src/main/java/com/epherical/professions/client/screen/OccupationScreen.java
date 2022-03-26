package com.epherical.professions.client.screen;

import com.epherical.professions.client.widgets.OccupationsList;
import com.epherical.professions.client.widgets.ProfessionButton;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.util.PacketIdentifiers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.epherical.professions.util.PacketIdentifiers.ProfessionButtons.*;

public class OccupationScreen extends Screen {

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/occupation_menu.png");
    protected int imageWidth = 256;
    protected int imageHeight = 170;

    private OccupationsList list;
    private List<Occupation> occupations;
    private PacketIdentifiers.ProfessionButtons button;
    private List<Profession> professions;

    // todo: this is ugly and messy and awful
    public OccupationScreen(List<Occupation> occupations, PacketIdentifiers.ProfessionButtons button, List<Profession> professions) {
        super(Component.nullToEmpty(""));
        this.occupations = occupations;
        this.button = button;
        this.professions = professions;
    }

    public OccupationScreen(List<Occupation> occupations) {
        this(occupations, null, Collections.emptyList());
    }

    @Override
    protected void init() {
        super.init();
        this.list = new OccupationsList(this, minecraft,
                this.width,
                (this.height), // height
                (this.height / 2 - 85), // top
                (this.height / 2 + 76), // bottom
                24);
        // row 1
        addRenderableWidget(new ProfessionButton(this.width / 2 + 43, this.height / 2 - 80, new TextComponent("Join"), button1 -> {
            PacketIdentifiers.clickProfessionButton(JOIN);
        }));
        addRenderableWidget(new ProfessionButton(this.width / 2 + 43 + 38 + 3, this.height / 2 - 80,new TextComponent("Leave"), button1 -> {
            PacketIdentifiers.clickProfessionButton(LEAVE);
        }));
        //row 2
        addRenderableWidget(new ProfessionButton(this.width / 2 + 43, this.height / 2 - 80 + 48 + 3, new TextComponent("Info"), button1 -> {
            PacketIdentifiers.clickProfessionButton(INFO);
        }));
        addRenderableWidget(new ProfessionButton(this.width / 2 + 43 + 38 + 3, this.height / 2 - 80 + 48 + 3,  new TextComponent("Top"), button1 -> {
            PacketIdentifiers.clickProfessionButton(TOP);
        }));
        // row 3
        addRenderableWidget(new ProfessionButton(this.width / 2 + 43 + (38 + 3) / 2, this.height / 2 - 80 + (48 + 3) * 2,  new TextComponent("Close"), button1 -> {
            this.minecraft.setScreen(null);
        }));
        this.addWidget(list);
        list.reset(button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        //System.out.println(this.width);
        int ofx = (this.width - imageWidth) / 2;
        int ofy = (this.height - imageHeight) / 2;
        renderOccupationWindow(poseStack, ofx, ofy, mouseX, mouseY);


        Optional<GuiEventListener> element = list.getChildAt(mouseX, mouseY);

        if (occupations.size() == 0 && button == null) {
            drawCenteredString(poseStack, font, "You aren't in any professions!", ((this.width - imageWidth) / 2) + 83, ofy + 50, -1);
        }

        list.render(poseStack, mouseX, mouseY, partialTick);
        super.render(poseStack, mouseX, mouseY, partialTick);

        if (element.isPresent()) {
            OccupationsList.AbstractEntry entry = (OccupationsList.AbstractEntry) element.get();
            entry.getButton().renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    public void renderOccupationWindow(PoseStack stack, int offsetX, int offsetY, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        this.blit(stack, offsetX, offsetY, 0, 0, imageWidth, imageHeight);
        RenderSystem.setShaderColor(1.0F, 0.0F, 1.0F, 1.0F);
    }

    private int adjustedHeight() {
        return (Math.max(52, this.height - 128 - 15) / 2);
    }

    private int scrollHeight() {
        return 80 + adjustedHeight() * 16 - 8;
    }

    private int centeredWidth() {
        return (this.width - 256) / 2;
    }

    public void setOccupations(List<Occupation> occupations) {
        this.occupations = occupations;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public List<Occupation> getOccupations() {
        return occupations;
    }

    public List<Profession> getProfessions() {
        return professions;
    }
}
