package com.epherical.professions.client.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SaveSideBar extends Box {

    private final Button saveButton;
    private final FileBox fileBox;

    public SaveSideBar(int x, int y, int width, int height, FileBox fileBox) {
        super(x, y, width, height);
        this.fileBox = fileBox;
        saveButton = new Button((width - 60) / 2, height - 40, 60, 20, Component.nullToEmpty("Save"), button -> {

        });
    }

    @Override
    public List<Widget> children() {
        List<Widget> widgets = new ArrayList<>(List.of(/*saveButton, */fileBox));
        widgets.addAll(fileBox.children());
        return widgets;
    }

    public FileBox getFileBox() {
        return fileBox;
    }
}
