package com.epherical.professions.presentation.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractOccupationSelector<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {


    private final int listWidth;

    public AbstractOccupationSelector(Minecraft mc, int width, int top, int bottom, int height) {
        super(mc, width, bottom - top, top, height);
        this.listWidth = width;
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {

    }

    @Override
    public @Nullable E getHovered() {
        return super.getHovered();
    }

    @Override
    public int getRowWidth() {
        return listWidth;
    }

    @Override
    protected int scrollBarX() {
        return listWidth;
    }

    @Override
    protected boolean scrollable() {
        return false;
    }
}
