package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.Occupation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OccupationList extends AbstractOccupationSelector<OccupationList.Entry> {


    public OccupationList(Minecraft mc, int width, int top, int bottom, int height, List<Occupation> occupations) {
        super(mc, width, top, bottom, height);


        for (Occupation occupation : occupations) {
            try {
                addEntry(new Entry(occupation));
            } catch (IllegalStateException ignored) {} // we could do something maybe idk
        }
    }

    private ItemStack professionIcon;
    private Component professionName;
    private FormattedText professionDescription;
    private List<FormattedCharSequence> orderedDescription;

    @Override
    public void setSelected(@Nullable OccupationList.Entry pSelected) {
        super.setSelected(pSelected);
        professionIcon = new ItemStack(pSelected.profession.formatting().icon());
        professionName = Component.literal(pSelected.profession.displayNameRaw()).withStyle(Style.EMPTY.withColor(pSelected.profession.professionColor()));
        String description = String.join(" ", pSelected.profession.description());
        professionDescription = FormattedText.of(description);

        List<FormattedCharSequence> visualOrder = new ArrayList<>(Language.getInstance().getVisualOrder(this.minecraft.font.getSplitter().splitLines(professionDescription, 98, Style.EMPTY)));
        if (visualOrder.size() >= 4) {
            visualOrder = visualOrder.subList(0, 4);
            visualOrder.removeLast();
            FormattedCharSequence last = visualOrder.getLast();
            visualOrder.removeLast();
            visualOrder.addLast(FormattedCharSequence.fromPair(last, FormattedCharSequence.forward("...", Style.EMPTY)));
        }

        orderedDescription = visualOrder;
    }

    @Override
    protected void renderListSeparators(GuiGraphics pGuiGraphics) {
    }

    protected int getRowTop(int pIndex) {
        return this.getY() - (int)this.getScrollAmount() + pIndex * this.itemHeight;
    }

    public ItemStack getProfessionIcon() {
        return professionIcon;
    }

    public Component getProfessionName() {
        return professionName;
    }

    public FormattedText getProfessionDescription() {
        return professionDescription;
    }

    public List<FormattedCharSequence> getOrderedDescription() {
        return orderedDescription;
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private static final ResourceLocation BACKGROUND_BUTTON = ResourceLocation
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_menu_profession_button_enabled");
        private static final ResourceLocation BACKGROUND_BUTTON_HOVERED = ResourceLocation
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_menu_profession_button_enabled_hovered");
        private static final ResourceLocation PROGRESS_BAR_EMPTY = ResourceLocation
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_small_xp_bar_empty");
        private static final ResourceLocation PROGRESS_BAR_FULL = ResourceLocation
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_small_xp_bar_full");
        private static final int PROGRESS_BAR_WIDTH = 22;
        private static final int PROGRESS_BAR_HEIGHT = 7;

        Profession profession;
        Occupation occupation;

        public Entry(Occupation occupation) {
            this.occupation = occupation;
            this.profession = occupation.getProfession().value();
        }

        public Profession getProfession() {
            return profession;
        }

        public Occupation getOccupation() {
            return occupation;
        }

        @Override
        public void render(GuiGraphics gfx,
                           int index, int y, int x, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {




            double placeholderPercentage = occupation.getExpProgress() / occupation.getMaxExperience();
            float clampedPercentage = Mth.clamp((float) placeholderPercentage, 0.0f, 1.0f);
            int percentageText = Mth.floor(clampedPercentage * 100.0f);


            if (hovering || this.equals(getSelected())) {
                gfx.blitSprite(BACKGROUND_BUTTON_HOVERED, 94, 32, 0, 0, x, y, rowWidth, 32);
            } else {
                gfx.blitSprite(BACKGROUND_BUTTON, 94, 32, 0, 0, x, y, rowWidth, 32);
            }

            // todo; cache the literal
            gfx.drawString(minecraft.font, Component.literal(profession.displayNameRaw()).withStyle(Style.EMPTY.withColor(profession.professionColor())), x + 4, y + 3, 0xFFFFFF);

            float scale = 0.5f;
            int translationX = x + 4;
            int translationY = y + 22;
            gfx.pose().pushPose();
            gfx.pose().translate(translationX, translationY, 0);
            gfx.pose().scale(scale, scale, 1);
            gfx.pose().translate(-translationX, -translationY, 0);
            gfx.drawString(minecraft.font, String.format("LvL %s", occupation.getLevel()), translationX, translationY, 0xFFFFFF);
            gfx.pose().popPose();

            int barX = x + 42;
            int barY = y + 20;
            gfx.blitSprite(PROGRESS_BAR_EMPTY, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, 0, 0, barX, barY, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);

            int filledWidth = Mth.floor(PROGRESS_BAR_WIDTH * clampedPercentage);
            if (filledWidth > 0) {
                gfx.blitSprite(PROGRESS_BAR_FULL, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, 0, 0, barX, barY, filledWidth, PROGRESS_BAR_HEIGHT);
            }

            gfx.drawString(minecraft.font, percentageText + "%", barX + PROGRESS_BAR_WIDTH + 4, barY - 2, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            boolean b = super.mouseClicked(pMouseX, pMouseY, pButton);
            setSelected(this);
            playDownSound(Minecraft.getInstance().getSoundManager());
            return b;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }
}
