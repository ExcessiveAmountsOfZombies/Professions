package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import com.epherical.professions.presentation.client.gui.widget.OccupationMenuButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.epherical.professions.presentation.client.RenderHelperUtil.*;

public class OccupationCategoryList extends AbstractOccupationSelector<OccupationCategoryList.Entry> {


    @Nullable
    private ProfessionCategory professionCategory;


    public OccupationCategoryList(Minecraft mc, int width, int top, int bottom, int height, Collection<ProfessionCategory> categories) {
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
    protected void extractListSeparators(GuiGraphicsExtractor pGuiGraphics) {
    }


    public void setProfessionCategory(@Nullable ProfessionCategory professionCategory) {
        this.professionCategory = professionCategory;
    }

    public @Nullable ProfessionCategory getProfessionCategory() {
        return professionCategory;
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private static final Identifier BACKGROUND_BUTTON = Identifier
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/category/selection_entry");
        private static final Identifier COLOR_BAND = Identifier
                .fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/category/selection_entry_color_band");

        private final ProfessionCategory category;

        private final OccupationMenuButton occupationMenuButton;

        public Entry(ProfessionCategory category) {
            this.category = category;


            occupationMenuButton = OccupationMenuButton.omButton(Component.translatable("professions.screen.occupation_category_list.select"), button -> {
                setProfessionCategory(category);
            }).pos(0, 0).size(47, 18).build();
        }





        @Override
        public void extractContent(GuiGraphicsExtractor gfx, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int y = this.getY();
            int x = this.getX();
            int rowWidth = this.getWidth();
            gfx.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_BUTTON, x, y, rowWidth, 47);


            drawScaled(gfx, x + 7, y + 4, 2.5f, () -> {
                gfx.item(new ItemStack(Items.BOOK), 0, 0);
            });


            int rgb = category.chatColor().getValue();
            float red = ((rgb >> 16) & 0xFF) / 255.0f;
            float green = ((rgb >> 8) & 0xFF) / 255.0f;
            float blue = (rgb & 0xFF) / 255.0f;

            int bandColor = (0xFF << 24)
                    | ((int) (255.0F * red) << 16)
                    | ((int) (255.0F * green) << 8)
                    | (int) (255.0F * blue);
            gfx.blitSprite(RenderPipelines.GUI_TEXTURED, COLOR_BAND, x, y, 4, 47, bandColor);

            gfx.text(minecraft.font, category.name(), x + 54, y + 5, 0xFFFFFFFF);

            final float scale = 0.5f;
            drawScaledString(gfx, minecraft.font,
                    Component.translatable("professions.screen.occupation_category_list.profession_count", category.professions().size()),
                    x + 54, y + 17, scale, 0xFF777777, false);
            renderProfessionColumns(gfx, x + 54, y + 22, scale);
            drawWrappedScaledString(gfx, minecraft.font, category.description(), x + 218, y + 3, scale, 172, 0xFFFFFFFF);




            occupationMenuButton.setPosition(x + 165, y + 3);
            occupationMenuButton.extractRenderState(gfx, mouseX, mouseY, partialTick);
        }

        // todo; split this off into a widget class so we can do a tooltip with it.
        private void renderProfessionColumns(GuiGraphicsExtractor gfx, int x, int y, float scale) {
            final int columnWidth = 50;
            final int maxRows = 3;
            final int maxVisible = 9;

            for (int i = 0; i < category.professions().size() && i < maxVisible; i++) {
                int column = i / maxRows;
                int row = i % maxRows;
                int drawX = x + (column * (columnWidth));
                int drawY = y + row * 8;

                HolderLookup.RegistryLookup<Profession> professionRegistryLookup = minecraft.level.registryAccess().lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY);
                Optional<Holder.Reference<Profession>> professionReference = professionRegistryLookup.get(category.professions().get(i));
                if (professionReference.isPresent()) {
                    Holder.Reference<Profession> professionReference1 = professionReference.get();
                    Profession value = professionReference1.value();
                    drawScaledString(gfx, minecraft.font,
                            Component.literal(value.displayNameRaw()),
                            drawX + 9, drawY + 1, scale, value.professionColor().getValue(), true);

                    drawScaled(gfx, drawX, drawY - 1, scale, () -> {
                        gfx.fakeItem(new ItemStack(value.formatting().icon()), 0, 0);
                    });
                }
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            boolean b = super.mouseClicked(event, doubleClick);
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
}
