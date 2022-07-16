package com.epherical.professions.client.screen.entry;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;

public interface Parent {

    List<? extends AbstractWidget> children();


}
