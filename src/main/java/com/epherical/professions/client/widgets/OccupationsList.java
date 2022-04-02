package com.epherical.professions.client.widgets;

import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.networking.ClientHandler;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Poorly named class, it could either be an OccupationList or a ProfessionList, depending on what the user is doing.
 */
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

    public void reset(List<AbstractEntry> entries) {
        clearEntries();
        for (AbstractEntry entry : entries) {
            addEntry(entry);
        }
        setScrollAmount(0.0D);
    }

    public abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {

        public abstract Button getButton();
    }

    public static class OccupationEntry extends AbstractEntry {

        private OccupationEntryButton button;
        private List<Component> toolTip;

        public OccupationEntry(OccupationScreen parent, OccupationsList listingWidget, Minecraft client, Occupation listing) {
            this.toolTip = new ArrayList<>();
            for (String s : listing.getProfession().getDescription()) {
                toolTip.add(new TextComponent(s).setStyle(Style.EMPTY.withColor(listing.getProfession().getDescriptionColor())));
            }
            this.button = new OccupationEntryButton(listing, 0, 0, 154, 24, button1 -> {
                if (parent.getButton() != null) {
                    if (parent.getButton().equals(CommandButtons.LEAVE)) {
                        ClientHandler.attemptLeavePacket(button.getOccupation().getProfession().getKey());
                    }
                }

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

        public OccupationEntryButton getButton() {
            return button;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(button);
        }
    }

    public static class ProfessionEntry extends AbstractEntry {

        private ProfessionEntryButton button;
        private List<Component> toolTip;

        public ProfessionEntry(OccupationScreen parent, OccupationsList listingWidget, Minecraft client, Profession profession) {
            this.toolTip = new ArrayList<>();
            for (String s : profession.getDescription()) {
                toolTip.add(new TextComponent(s).setStyle(Style.EMPTY.withColor(profession.getDescriptionColor())));
            }
            this.button = new ProfessionEntryButton(profession, 0, 0, 154, 24, button1 -> {
                if (parent.getButton() != null) {
                    switch (parent.getButton()) {
                        case JOIN -> {
                            ClientHandler.attemptJoinPacket(profession.getKey());
                            ClientHandler.sendOccupationPacket();
                        }
                        case LEAVE -> {
                            ClientHandler.attemptLeavePacket(profession.getKey());
                            ClientHandler.sendOccupationPacket();
                        }
                        case INFO -> {
                            ClientHandler.attemptInfoPacket(profession.getKey());
                        }
                    }
                }
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

    public static class InfoEntry extends AbstractEntry {

        private final InfoEntryButton button;

        public InfoEntry(OccupationScreen parent, OccupationsList listingWidget, Minecraft client, Component component) {
            button = new InfoEntryButton(component, 0, 0, 154, 24, button -> {

            }, (button, poseStack, i, j) -> {
                List<Component> hoverComp = component.getStyle().getHoverEvent() != null
                        ? component.getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT).getSiblings()
                        : List.of(component);

                parent.renderTooltip(poseStack, hoverComp, Optional.empty(), i, j);
            });
        }

        @Override
        public Button getButton() {
            return button;
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
    }
}
