package com.epherical.professions.client.gui.components;

import com.epherical.professions.CommonClass;
import com.epherical.professions.core.actions.Action;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OccupationInfoList extends ContainerObjectSelectionList<OccupationInfoList.Entry> {


    private final int listWidth;

    public OccupationInfoList(Minecraft mc, int width, int top, int bottom, int height) {
        super(mc, width, bottom - top, top, height);
        this.listWidth = width;


        /*CommonClass.ACTION_LOAD2.getByHolder().forEach((key, value) -> {
            addEntry(new Entry(key, value));
            addEntry(new Entry(key, value));
            addEntry(new Entry(key, value));
            addEntry(new Entry(key, value));
            addEntry(new Entry(key, value));
        });*/
    }

    @Override
    protected void renderListBackground(GuiGraphics pGuiGraphics) {
        // don't render the background.
        //super.renderListBackground(pGuiGraphics);
    }

    @Override
    protected void setRenderHeader(boolean pRenderHeader, int pHeaderHeight) {
        super.setRenderHeader(false, pHeaderHeight);
    }

    @Override
    public @Nullable Entry getHovered() {
        return super.getHovered();
    }

    @Override
    protected int getScrollbarPosition() {
        return listWidth;
    }

    @Override
    public int getRowWidth() {
        return listWidth;
    }

    @Override
    protected boolean scrollbarVisible() {
        return false;
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private static final Component COMP = Component.literal("");


        private ItemStack holder;
        private final Action action;

        private final ItemStack actionItem;

        public Entry(Holder<?> holder, Action action) {
            //this.holder = holder;
            this.action = action;

            actionItem = new ItemStack(this.action.getIcon());

            Optional<? extends ResourceKey<?>> resourceKey = holder.unwrapKey();
            if (resourceKey.isPresent() && (resourceKey.get().isFor(Registries.ITEM) || resourceKey.get().isFor(Registries.BLOCK))) {
                ItemLike like = (ItemLike) holder.value();
                this.holder = new ItemStack(like);
            }
        }

        public ItemStack getHolder() {
            return holder;
        }

        public Action getAction() {
            return action;
        }

        public ItemStack getActionItem() {
            return actionItem;
        }

        @Override
        public void render(GuiGraphics gfx,
                           int index, int y, int x, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {

            // vertical centering of a 16×16 icon in the row
            int baseY = y + (rowHeight - 16) / 2;

            final int colour = 0xFF3A291E;     // ARGB 58,41,30
            final int spacing = 18;             // 16-pixel icon + 2-pixel gap

            for (int i = 0; i < 5; i++) {
                int iconX = x + i * spacing;

                // draw the item
                gfx.renderFakeItem(holder, iconX, baseY);

                // draw 1-pixel border
                int l = iconX - 1;
                int t = baseY - 1;
                int r = iconX + 16;
                int b = baseY + 16;

                // horizontal lines
                gfx.fill(l, t, r, t, colour);     // top
                gfx.fill(l, b, r, b + 1, colour);     // bottom
                // vertical lines
                gfx.fill(l, t, l, b + 1, colour); // left
                gfx.fill(r, t, r + 1, b + 1, colour); // right
            }
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
