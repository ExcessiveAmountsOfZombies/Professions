package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.actions.Action;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class OccupationInfoList extends AbstractOccupationSelector<OccupationInfoList.Entry> {

    private static final int ITEMS_PER_ROW = 5;

    private final Occupation occupation;

    public OccupationInfoList(Minecraft mc, int width, int top, int bottom, int height, Occupation occupation) {
        super(mc, width, top, bottom, height);
        this.occupation = occupation;

        addEntries("");
    }

    public void addEntries(String filter) {
        clearEntries();
        String normalizedFilter = filter.toLowerCase(Locale.ROOT).trim();
        boolean shouldNotFilter = normalizedFilter.isEmpty();

        // todo; this will need a specific place. things will get serialized to the player in some way so we probably wont be able to call it like this.
        Collection<Action<?>> actionsByProfession = ProfessionsCommon.INSTANCE.getActionManager().getActionsByProfession(occupation.getProfession());
        List<EntryItem> items = new ArrayList<>();
        for (Action<?> action : actionsByProfession) {
            Collection<Holder<?>> actionsByValue = ProfessionsCommon.INSTANCE.getActionManager().getValuesForAction(action);
            for (Holder<?> holder : actionsByValue) {
                EntryItem item = EntryItem.create(holder, action);
                if (item != null)
                    if (shouldNotFilter) {
                        items.add(item);
                    } else if (item.holder().getHoverName().getString().toLowerCase(Locale.ROOT).contains(normalizedFilter)) {
                        items.add(item);
                    }
            }
        }

        for (int i = 0; i < items.size(); i += ITEMS_PER_ROW) {
            addEntry(new Entry(items.subList(i, Math.min(i + ITEMS_PER_ROW, items.size()))));
        }
    }

    @Override
    protected void renderListSeparators(GuiGraphics pGuiGraphics) {
    }

    @Override
    protected int getRowTop(int pIndex) {
        return super.getRowTop(pIndex);
    }

    @Override
    public int getBottom() {
        return super.getBottom();
    }

    protected int getRowBottom(int pIndex) {
        return super.getRowBottom(pIndex);
    }

    public Occupation getProfession() {
        return occupation;
    }

    protected record EntryItem(ItemStack holder, Action<?> action) {

        @Nullable
        private static EntryItem create(Holder<?> holder, Action<?> action) {
            Optional<? extends ResourceKey<?>> resourceKey = holder.unwrapKey();
            if (resourceKey.isPresent() && (resourceKey.get().isFor(Registries.ITEM) || resourceKey.get().isFor(Registries.BLOCK))) {
                ItemLike like = (ItemLike) holder.value();
                return new EntryItem(new ItemStack(like), action);
            } else {
                return new EntryItem(action.getIconStack(holder), action);
            }
        }
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private static final int ICON_SIZE = 16;
        private static final int ICON_SPACING = 18;
        private static final int BORDER_COLOR = 0xFF3A291E;

        private final List<EntryItem> items;
        private int hoveredIndex;

        public Entry(List<EntryItem> items) {
            this.items = items;
            this.hoveredIndex = 0;
        }

        public ItemStack getHolder() {
            return items.get(hoveredIndex).holder();
        }

        public Action<?> getAction() {
            return items.get(hoveredIndex).action();
        }

        public ItemStack getActionItem() {
            return new ItemStack(getAction().getIcon());
        }

        @Override
        public boolean isMouseOver(double pMouseX, double pMouseY) {
            // todo; doesn't seem to be used at all
            boolean rowHovered = super.isMouseOver(pMouseX, pMouseY);
            if (rowHovered) {
                int index = getHoveredIndex(pMouseX, pMouseY, OccupationInfoList.this.getRowLeft());
                if (index >= 0) {
                    hoveredIndex = index;
                }
            }
            return rowHovered;
        }

        @Override
        public void render(GuiGraphics gfx,
                           int index, int y, int x, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {
            int baseY = y + (rowHeight - ICON_SIZE) / 2;

            //gfx.fill(x, y, rowWidth + x, rowHeight + y, 0xFFFFFFFF);

            for (int i = 0; i < items.size(); i++) {
                int iconX = x + i * ICON_SPACING;
                gfx.renderFakeItem(items.get(i).holder(), iconX, baseY);
                drawBorder(gfx, iconX, baseY, false);
            }

            if (hovering) {
                int hoveredItem = getHoveredIndex(mouseX, mouseY, x, baseY);
                if (hoveredItem >= 0) {
                    // todo; add the ability to click it, then you could find out more information about the rewards
                    hoveredIndex = hoveredItem;
                    int iconX = x + hoveredIndex * ICON_SPACING;
                    drawBorder(gfx, iconX, baseY, true);
                }
            }
        }

        private void drawBorder(GuiGraphics gfx, int iconX, int baseY, boolean highlighted) {
            int color = highlighted ? 0xFFFFFFFF : BORDER_COLOR;
            int left = iconX - 1;
            int top = baseY - 1;
            int right = iconX + ICON_SIZE;
            int bottom = baseY + ICON_SIZE;
            gfx.fill(left, top, right, top + 1, color);
            gfx.fill(left, bottom, right, bottom + 1, color);
            gfx.fill(left, top, left + 1, bottom + 1, color);
            gfx.fill(right, top, right + 1, bottom + 1, color);
        }

        private int getHoveredIndex(double mouseX, double mouseY, int rowLeft) {
            int rowTop = OccupationInfoList.this.getRowTop(OccupationInfoList.this.children().indexOf(this));
            int baseY = rowTop + (OccupationInfoList.this.itemHeight - ICON_SIZE) / 2;
            return getHoveredIndex(mouseX, mouseY, rowLeft, baseY);
        }

        private int getHoveredIndex(double mouseX, double mouseY, int rowLeft, int baseY) {
            if (mouseY < baseY - ICON_SIZE || mouseY >= baseY + ICON_SIZE) {
                return -1;
            }

            for (int i = 0; i < items.size(); i++) {
                int iconLeft = rowLeft + i * ICON_SPACING;
                int iconRight = iconLeft + ICON_SIZE;
                if (mouseX >= iconLeft && mouseX < iconRight) {
                    return i;
                }
            }

            return -1;
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
