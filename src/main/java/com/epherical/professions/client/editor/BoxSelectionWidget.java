package com.epherical.professions.client.editor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoxSelectionWidget extends ContainerObjectSelectionList<BoxSelectionWidget.Entry> {


    public BoxSelectionWidget(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    public void addEntries(List<Entry> entries) {
        clearEntries();
        for (Entry entry : entries) {
            addEntry(entry);
        }
        setScrollAmount(0.0D);
    }

    @Override
    protected int getScrollbarPosition() {
        return getRowLeft() + this.width;
    }

    @Override
    public int getRowLeft() {
        return x0 + 2;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth();
    }

    @Override
    protected int getRowTop(int index) {
        return super.getRowTop(index);
    }

    @Override
    public int getRowBottom(int index) {
        return getRowTop(index);
    }

    @Nullable
    @Override
    public Entry getEntryAtPosition(double mouseX, double mouseY) {
        int i = this.getRowWidth() / 2;
        int j = this.x0 + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = Mth.floor(mouseY - (double) this.y0) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        return mouseX < (double) this.getScrollbarPosition() && mouseX >= (double) k && mouseX <= (double) l && n >= 0 && m >= 0 && n < this.getItemCount() ? this.children().get(n) : null;
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private final Button button;
        private final List<Button> buttons = new ArrayList<>();

        public Entry(Button button) {
            this.button = button;
            this.buttons.add(button);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            button.x = left;
            button.y = top;
            button.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return buttons;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.button.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.button.mouseReleased(mouseX, mouseY, button);
        }
    }
}
