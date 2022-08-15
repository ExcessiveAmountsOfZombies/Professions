package com.epherical.professions.client.screen;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class CommonDataScreen extends Screen {

    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("professions", "textures/gui/datapack_screen.png");

    protected CommonDataScreen(Component $$0) {
        super($$0);
    }


    public abstract void addChild(AbstractWidget widget);

    public abstract void markScreenDirty();
}
