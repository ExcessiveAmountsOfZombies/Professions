package com.epherical.professions.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractOccupationSelector<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {


    private final int listWidth;

    public AbstractOccupationSelector(Minecraft mc, int width, int top, int bottom, int height) {
        super(mc, width, bottom - top, top, height);
        this.listWidth = width;
    }

    @Override
    protected void renderListBackground(GuiGraphics pGuiGraphics) {
    }

    @Override
    protected void setRenderHeader(boolean pRenderHeader, int pHeaderHeight) {
        super.setRenderHeader(false, pHeaderHeight);
    }

    protected int getRowTop(int pIndex) {
        return this.getY() - (int)this.getScrollAmount() + pIndex * this.itemHeight;
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.height - 8));
    }

    @Override
    public @Nullable E getHovered() {
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
}
