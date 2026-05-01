package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OccupationCategoryList extends AbstractOccupationSelector<OccupationCategoryList.Entry> {


    @Nullable
    private ProfessionCategory professionCategory;


    public OccupationCategoryList(Minecraft mc, int width, int top, int bottom, int height, List<ProfessionCategory> categories) {
        super(mc, width, top, bottom, height);

        for (ProfessionCategory category : categories) {
            addEntry(new Entry(category));
        }

        /*addEntry(new Entry(category));
        addEntry(new Entry(category));
        addEntry(new Entry(category));*/
    }

    @Override
    public void setSelected(@Nullable OccupationCategoryList.Entry pSelected) {
        super.setSelected(pSelected);
    }

    @Override
    protected void renderListSeparators(GuiGraphics pGuiGraphics) {
    }


    public void setProfessionCategory(@Nullable ProfessionCategory professionCategory) {
        this.professionCategory = professionCategory;
    }

    public @Nullable ProfessionCategory getProfessionCategory() {
        return professionCategory;
    }

    protected int getRowTop(int pIndex) {
        return this.getY() - (int)this.getScrollAmount() + pIndex * this.itemHeight;
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private static final ResourceLocation BACKGROUND_BUTTON = ResourceLocation
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/category/selection_entry");
        private static final ResourceLocation COLOR_BAND = ResourceLocation
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/category/selection_entry_color_band");

        private final ProfessionCategory category;

        private final OccupationMenuButton occupationMenuButton;

        private final ItemStack icon;


        public Entry(ProfessionCategory category) {
            this.category = category;

            this.icon = new ItemStack(Items.BOOK);


            occupationMenuButton = OccupationMenuButton.omButton(Component.literal("Select"), button -> {
                setProfessionCategory(category);
            }).pos(0, 0).size(47, 18).build();
        }





        @Override
        public void render(GuiGraphics gfx,
                           int index, int y, int x, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {
            gfx.blitSprite(BACKGROUND_BUTTON, 305, 47, 0, 0, x, y, rowWidth, 47);


            drawScaled(gfx, x + 7, y + 4, 2.5f, () -> {
                gfx.renderItem(new ItemStack(Items.BOOK), 0, 0); // todo; add something for scaling idk
            });


            int rgb = category.chatColor().getValue();
            float red = ((rgb >> 16) & 0xFF) / 255.0f;
            float green = ((rgb >> 8) & 0xFF) / 255.0f;
            float blue = (rgb & 0xFF) / 255.0f;

            gfx.setColor(red, green, blue, 1.0f);
            gfx.blitSprite(COLOR_BAND, 4, 47, 0, 0, x, y, 4, 47);
            gfx.setColor(1f, 1f, 1f, 1.0f);


            gfx.drawString(minecraft.font, category.name(), x + 54, y + 5, 0xFFFFFF);


            // todo; cache the literal
           // gfx.drawString(minecraft.font, Component.literal(profession.displayNameRaw()).withStyle(Style.EMPTY.withColor(profession.professionColor())), x + 4, y + 3, 0xFFFFFF);

            float scale = 0.5f;
            int translationX = x + 54;
            int translationY = y + 18;
            gfx.pose().pushPose();
            gfx.pose().translate(translationX, translationY, 0);
            gfx.pose().scale(scale, scale, 1);
            gfx.pose().translate(-translationX, -translationY, 0);
            gfx.drawString(minecraft.font, String.format("Professions: (%s)", category.professions().size()), translationX, translationY, 0x777777, false);
            gfx.pose().popPose();


            translationX = x + 215;
            translationY = y + 3;
            gfx.pose().pushPose();
            gfx.pose().translate(translationX, translationY, 0);
            gfx.pose().scale(scale, scale, 1);
            gfx.pose().translate(-translationX, -translationY, 0);
            gfx.drawWordWrap(minecraft.font, FormattedText.of(category.description()), translationX, translationY, 172, 0xFFFFFFFF);
            gfx.pose().popPose();




            occupationMenuButton.setPosition(x + 165, y + 3);
            occupationMenuButton.render(gfx, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            boolean b = super.mouseClicked(pMouseX, pMouseY, pButton);
            /*setSelected(this);
            playDownSound(Minecraft.getInstance().getSoundManager());*/
            return b;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(occupationMenuButton);
        }
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
}
