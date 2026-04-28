package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.Action;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class OccupationInfoList extends AbstractOccupationSelector<OccupationInfoList.Entry> {

    private static final int ITEMS_PER_ROW = 5;

    private final Holder<Profession> profession;

    public OccupationInfoList(Minecraft mc, int width, int top, int bottom, int height) {
        super(mc, width, bottom - top, top, height);

        this.profession = mc.getSingleplayerServer().registryAccess().lookupOrThrow(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
                .getOrThrow(ResourceKey.create(ProfessionsCommon.PROFESSION_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("professions", "mining")));



        Collection<Action<?>> actionsByProfession = ProfessionsCommon.INSTANCE.getActionLoader().getActionManager().getActionsByProfession(this.profession);
        List<EntryItem> items = new ArrayList<>();
        for (Action<?> action : actionsByProfession) {
            Collection<Holder<?>> actionsByValue = ProfessionsCommon.INSTANCE.getActionLoader().getActionManager().getValuesForAction(action);
            for (Holder<?> holder : actionsByValue) {
                EntryItem item = EntryItem.create(holder, action);
                if (item != null) {
                    items.add(item);
                }
            }
        }

        for (int i = 0; i < items.size(); i += ITEMS_PER_ROW) {
            addEntry(new Entry(items.subList(i, Math.min(i + ITEMS_PER_ROW, items.size()))));
        }
    }

    public Holder<Profession> getProfession() {
        return profession;
    }

    protected record EntryItem(ItemStack holder, Action<?> action) {

        @Nullable
        private static EntryItem create(Holder<?> holder, Action<?> action) {
            Optional<? extends ResourceKey<?>> resourceKey = holder.unwrapKey();
            if (resourceKey.isPresent() && (resourceKey.get().isFor(Registries.ITEM) || resourceKey.get().isFor(Registries.BLOCK))) {
                ItemLike like = (ItemLike) holder.value();
                return new EntryItem(new ItemStack(like), action);
            }
            return null;
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

            for (int i = 0; i < items.size(); i++) {
                int iconX = x + i * ICON_SPACING;
                gfx.renderFakeItem(items.get(i).holder(), iconX, baseY);
                drawBorder(gfx, iconX, baseY, false);
            }

            if (hovering) {
                int hoveredItem = getHoveredIndex(mouseX, mouseY, x, baseY);
                if (hoveredItem >= 0) {
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
            if (mouseY < baseY || mouseY >= baseY + ICON_SIZE) {
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
