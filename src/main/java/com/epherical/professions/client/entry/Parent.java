package com.epherical.professions.client.entry;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public interface Parent {

    List<? extends AbstractWidget> children();


}
