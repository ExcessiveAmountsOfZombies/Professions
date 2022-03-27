package com.epherical.professions.client.widgets;

import com.epherical.professions.client.ProfessionsClient;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.networking.PacketIdentifiers;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OccupationsList extends ContainerObjectSelectionList<OccupationsList.AbstractEntry> {

    private final OccupationScreen parentScreen;
    private final Minecraft minecraft;


    public OccupationsList(OccupationScreen parent, Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.parentScreen = parent;
        this.minecraft = minecraft;
        // y0 = top -    k
        // y1 = bottom - l
        // x1 = left -   i
        // x0 = right -  0

        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);

    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth();
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() - 90;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        double d = this.minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor(
                (int)((double)(this.getRowLeft() - 25) * d),
                (int)((double)(this.height - this.y1) * d + 2),
                (int)((double)(this.getScrollbarPosition() + 6) * d),
                (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * d));
        super.render(poseStack, mouseX, mouseY, partialTick);
        RenderSystem.disableScissor();
    }

    public void reset(CommandButtons button) {
        clearEntries();
        if (button != null) {
            for (Profession profession : parentScreen.getProfessions()) {
                addEntry(new ProfessionEntry(parentScreen, this, minecraft, profession));
            }
        } else {
            for (Occupation entry : parentScreen.getOccupations()) {
                addEntry(new OccupationEntry(parentScreen, this, minecraft, entry));
            }
        }

        setScrollAmount(0.0D);
    }

    public abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {

        public Button getButton() {
            return null;
        }
    }

    public static class OccupationEntry extends AbstractEntry {

        private final OccupationScreen parent;
        private final Minecraft client;
        private final OccupationsList widget;
        private final Occupation occupation;

        private OccupationButton button;
        private List<Component> toolTip;

        public OccupationEntry(OccupationScreen parent, OccupationsList listingWidget, Minecraft client, Occupation listing) {
            this.parent = parent;
            this.client = client;
            this.widget = listingWidget;
            this.occupation = listing;
            this.toolTip = new ArrayList<>();
            for (String s : listing.getProfession().getDescription()) {
                toolTip.add(new TextComponent(s).setStyle(Style.EMPTY.withColor(listing.getProfession().getDescriptionColor())));
            }
            this.button = new OccupationButton(listing, 0, 0, 154, 24, button1 -> {

            }, (button1, poseStack, i, j) -> {
                parent.renderTooltip(poseStack, toolTip, Optional.empty(), i, j);
            });

        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            button.x = left - 14;
            button.y = top;
            button.render(poseStack, mouseX, mouseY, partialTick);
        }

        public OccupationButton getButton() {
            return button;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(button);
        }
    }

    public static class ProfessionEntry extends AbstractEntry {

        private final OccupationScreen parent;
        private final Minecraft client;
        private final OccupationsList widget;
        private final Profession profession;

        private ProfessionEntryButton button;
        private List<Component> toolTip;

        public ProfessionEntry(OccupationScreen parent, OccupationsList listingWidget, Minecraft client, Profession profession) {
            this.parent = parent;
            this.client = client;
            this.widget = listingWidget;
            this.profession = profession;
            this.toolTip = new ArrayList<>();
            for (String s : profession.getDescription()) {
                toolTip.add(new TextComponent(s).setStyle(Style.EMPTY.withColor(profession.getDescriptionColor())));
            }
            this.button = new ProfessionEntryButton(profession, TextColor.fromLegacyFormat(ChatFormatting.GREEN), 0, 0, 154, 24, button1 -> {
                PacketIdentifiers.attemptJoin(profession.getKey());
                ProfessionsClient.sendOccupationPacket();
                // TODO: send message BACK to server requesting to join this profession
            }, (button1, poseStack, i, j) -> {
                parent.renderTooltip(poseStack, toolTip, Optional.empty(), i, j);
            });

        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            button.x = left - 14;
            button.y = top;
            button.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(button);
        }

        @Override
        public ProfessionEntryButton getButton() {
            return button;
        }
    }
}
