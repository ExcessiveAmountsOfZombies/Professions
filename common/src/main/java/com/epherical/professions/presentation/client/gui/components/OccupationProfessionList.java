package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.Occupation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.locale.Language;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OccupationProfessionList extends AbstractOccupationSelector<OccupationProfessionList.Entry> {


    public OccupationProfessionList(Minecraft mc, int width, int top, int bottom, int height, List<Occupation> occupations) {
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
    public void setSelected(@Nullable OccupationProfessionList.Entry pSelected) {
        super.setSelected(pSelected);
        if (pSelected == null) {
            professionIcon = ItemStack.EMPTY;
            professionName = Component.empty();
            professionDescription = FormattedText.EMPTY;
            orderedDescription = List.of();
            return;
        }

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
    protected void extractListSeparators(GuiGraphicsExtractor pGuiGraphics) {
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

        public static final Identifier BACKGROUND_BUTTON = Identifier
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_menu_profession_button_enabled");
        public static final Identifier BACKGROUND_BUTTON_HOVERED = Identifier
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_menu_profession_button_enabled_hovered");
        public static final Identifier PROGRESS_BAR_EMPTY = Identifier
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_small_xp_bar_empty");
        public static final Identifier PROGRESS_BAR_FULL = Identifier
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_small_xp_bar_full");
        private static final int PROGRESS_BAR_WIDTH = 22;
        private static final int PROGRESS_BAR_HEIGHT = 7;

        Profession profession;
        Occupation occupation;

        private Component professionName;

        public Entry(Occupation occupation) {
            this.occupation = occupation;
            this.profession = occupation.getProfession().value();
            this.professionName = Component.literal(profession.displayNameRaw()).withStyle(Style.EMPTY.withColor(profession.professionColor()));
        }

        public Profession getProfession() {
            return profession;
        }

        public Occupation getOccupation() {
            return occupation;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor gfx, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int y = this.getY();
            int x = this.getX();
            int rowWidth = this.getWidth();
            int rowHeight = this.getHeight();
            double placeholderPercentage = occupation.getExpProgress() / occupation.getMaxExperience();
            float clampedPercentage = Mth.clamp((float) placeholderPercentage, 0.0f, 1.0f);
            int percentageText = Mth.floor(clampedPercentage * 100.0f);


            if (hovering || this.equals(getSelected())) {
                gfx.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_BUTTON_HOVERED, x, y, rowWidth - 2, rowHeight - 1);
                //gfx.blitSprite(BACKGROUND_BUTTON_HOVERED, 0, 0, x, y, rowWidth);
            } else {
                gfx.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_BUTTON, x, y, rowWidth - 2, rowHeight -1);
            }

            gfx.text(minecraft.font, professionName, x + 4, y + 3, 0xFFFFFFFF);

            float scale = 0.5f;
            int translationX = x + 4;
            int translationY = y + 22;
            gfx.pose().pushMatrix();
            gfx.pose().translate(translationX, translationY);
            gfx.pose().scale(scale, scale);
            gfx.pose().translate(-translationX, -translationY);
            gfx.text(minecraft.font,
                    Component.translatable("professions.screen.occupation_list.level_short", occupation.getLevel()),
                    translationX, translationY, 0xFFFFFFFF);
            gfx.pose().popMatrix();

            int barX = x + 42;
            int barY = y + 20;
            gfx.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_BAR_EMPTY, barX, barY, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);

            int filledWidth = Mth.floor(PROGRESS_BAR_WIDTH * clampedPercentage);
            int rgb = ARGB.color(0xFF, occupation.getProfession().value().professionColor().getValue());
            float red = ((rgb >> 16) & 0xFF) / 255.0f;
            float green = ((rgb >> 8) & 0xFF) / 255.0f;
            float blue = (rgb & 0xFF) / 255.0f;
            if (filledWidth > 0) {
                int progressColor = (0xFF << 24)
                        | ((int) (255.0F * red) << 16)
                        | ((int) (255.0F * green) << 8)
                        | (int) (255.0F * blue);
                gfx.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_BAR_FULL, barX, barY, filledWidth, PROGRESS_BAR_HEIGHT, progressColor);
            }

            gfx.text(minecraft.font,
                    Component.translatable("professions.screen.occupation_list.percent", percentageText),
                    barX + PROGRESS_BAR_WIDTH + 4, barY - 2, rgb);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            boolean b = super.mouseClicked(event, doubleClick);
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
