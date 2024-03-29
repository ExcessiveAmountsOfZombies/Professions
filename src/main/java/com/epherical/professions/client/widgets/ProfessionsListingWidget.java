package com.epherical.professions.client.widgets;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.util.ActionDisplay;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProfessionsListingWidget extends ContainerObjectSelectionList<ProfessionsListingWidget.AbstractEntry> {

    private final OccupationScreen<?> parentScreen;
    final Minecraft minecraft;

    public ProfessionsListingWidget(OccupationScreen<?> parent, Minecraft minecraft, int i, int j, int k, int l, int m) {
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
        return super.getRowWidth() + 21;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() - 153;
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft() + 10;
    }

    @Override
    public void render(@NotNull GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        double d = this.minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor(
                (int) ((double) (this.getRowLeft() - 25) * d),
                (int) ((double) (this.height - this.y1) * d - 5),
                (int) ((double) (this.getScrollbarPosition() + 6) * d),
                (int) ((double) (this.height - (this.height - this.y1) - this.y0 - 2) * d));
        super.render(poseStack, mouseX, mouseY, partialTick);
        RenderSystem.disableScissor();
        AbstractEntry entry = getEntryAtPosition(mouseX, mouseY);
        if (entry != null) {
            Optional<GuiEventListener> child = entry.getChildAt(mouseX, mouseY);
            if (child.isPresent()) {
                AbstractWidget widget = (AbstractWidget) child.get();
                //widget.renderToolTip(poseStack, mouseX, mouseY);
            }
        }
    }

    public void resetAndAddEntries(List<AbstractEntry> entries) {
        clearEntries();
        for (AbstractEntry entry : entries) {
            addEntry(entry);
        }
        setScrollAmount(0.0D);
    }

    public abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {

        public abstract Button getButton();


        @Override
        public void render(GuiGraphics poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            getButton().setX(left - 14);
            getButton().setY(top);
            getButton().render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }
    }

    public static class OccupationEntry extends AbstractEntry implements Selectable, HoldsProfession {

        private OccupationEntryButton button;
        private List<Component> toolTip;

        public OccupationEntry(OccupationScreen<?> parent, ProfessionsListingWidget listingWidget, Minecraft client, Occupation listing) {
            this.toolTip = new ArrayList<>();
            for (String s : listing.getProfession().getDescription()) {
                toolTip.add(Component.literal(s).setStyle(Style.EMPTY.withColor(listing.getProfession().getDescriptionColor())));
            }
            this.button = new OccupationEntryButton(listing, 0, 0, 92, 24, button1 -> {
                if (parent.getButton() != null) {
                    if (parent.getButton().equals(CommandButtons.LEAVE)) {
                        ProfessionPlatform.platform.getClientNetworking().attemptLeavePacket(button.getOccupation().getProfession().getKey());
                    }
                }
            });
        }

        @Override
        public void render(GuiGraphics poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
            Minecraft minecraft1 = Minecraft.getInstance();
            if (isMouseOver) {
                poseStack.renderComponentTooltip(minecraft1.font, toolTip, mouseX, mouseY);
            }
        }

        public OccupationEntryButton getButton() {
            return button;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(button);
        }

        @Override
        public int getYFactor() {
            return 0;
        }

        @Override
        public void setSelected(boolean selected) {
            button.setSelected(selected);
        }

        @Override
        public boolean isSelected() {
            return button.isSelected();
        }

        @Override
        public Profession getProfession() {
            return button.getProfession();
        }
    }

    public static class ProfessionEntry extends AbstractEntry {

        private ProfessionEntryButton button;
        private List<Component> toolTip;

        public ProfessionEntry(OccupationScreen<?> parent, ProfessionsListingWidget listingWidget, Minecraft client, Profession profession) {
            this.toolTip = new ArrayList<>();
            for (String s : profession.getDescription()) {
                toolTip.add(Component.literal(s).setStyle(Style.EMPTY.withColor(profession.getDescriptionColor())));
            }
            this.button = new ProfessionEntryButton(profession, 0, 0, 92, 24, button1 -> {
                if (parent.getButton() != null) {
                    switch (parent.getButton()) {
                        case JOIN -> {
                            ProfessionPlatform.platform.getClientNetworking().attemptJoinPacket(profession.getKey());
                            ProfessionPlatform.platform.getClientNetworking().sendOccupationPacket();
                        }
                        case LEAVE -> {
                            ProfessionPlatform.platform.getClientNetworking().attemptLeavePacket(profession.getKey());
                            ProfessionPlatform.platform.getClientNetworking().sendOccupationPacket();
                        }
                        case INFO -> {
                            ProfessionPlatform.platform.getClientNetworking().attemptInfoPacket(profession.getKey());
                        }
                    }
                }
            });
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

        OccupationScreen<?> screen;

        private final List<InfoEntryButton> buttons = new ArrayList<>();

        public InfoEntry(OccupationScreen<?> parent, ProfessionsListingWidget listingWidget, Minecraft client, List<ActionDisplay.Icon> component) {
            this.screen = parent;

            /*for (ActionDisplay.Icon icon : component) {
                buttons.add(new InfoEntryButton(icon, 0, 0, 16, 16, button -> {
                }, (button, poseStack, i, j) -> {
                    InfoEntryButton infoEntryButton = (InfoEntryButton) button;
                    List<Component> hoverComp = icon.getName().getStyle().getHoverEvent() != null
                            ? icon.getName().getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT).getSiblings()
                            : List.of(icon.getName());
                    parent.renderTooltip(poseStack, hoverComp, Optional.empty(), i, j);
                    infoEntryButton.drawText();
                    infoEntryButton.drawText(poseStack, parent.width, parent.height);
                }));
            }*/
        }

        @Override
        public void render(GuiGraphics poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).setX(left - 12 + (i * 18));
                buttons.get(i).setY( top);
                buttons.get(i).render(poseStack, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public Button getButton() {
            return null;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return buttons;
        }
    }

    public static class LevelEntry extends AbstractEntry {

        private final LevelEntryButton button;
        private final PlayerInfo info;
        private final MutableComponent level;

        public LevelEntry(OccupationScreen<?> parent, ProfessionsListingWidget listingWidget, Minecraft client, UUID uuid, int level) {
            PlayerInfo info = client.player.connection.getPlayerInfo(uuid);
            this.info = info;
            this.level = Component.translatable("professions.command.stats.level",
                            Component.literal(String.valueOf(level)).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            button = new LevelEntryButton(Component.literal(info.getProfile().getName()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)), 0, 0, 92, 24, button -> {
            });
        }

        @Override
        public void render(GuiGraphics poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
            int i = left - 10;
            int j = top + (height - 14) / 2;


            //RenderSystem.setShaderTexture(0, );
            PlayerFaceRenderer.draw(poseStack, this.info.getSkinLocation(), i, j, 8, false, false);
            //poseStack.blit(i, j, 16, 16, 8.0F, 8.0F, 8, 8, 64, 64);
            RenderSystem.enableBlend();
            //poseStack.blit(i, j, 16, 16, 40.0F, 8.0F, 8, 8, 64, 64);
            RenderSystem.disableBlend();
            int color = this.button.active ? 16777215 : 10526880;
            poseStack.drawCenteredString(Minecraft.getInstance().font, level,
                    (this.button.getX() + 35 + this.button.getWidth() / 2), this.button.getY() + (this.button.getHeight() - 8) / 2, color | Mth.ceil(255.0F) << 24);
        }

        @Override
        public Button getButton() {
            return button;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(button);
        }
    }
}
