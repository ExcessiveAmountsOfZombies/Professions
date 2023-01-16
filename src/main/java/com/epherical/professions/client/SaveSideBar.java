package com.epherical.professions.client;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.client.editor.BoxSelectionWidget;
import com.epherical.professions.client.editors.DatapackEditor;
import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.datapack.ProfessionLoader;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SaveSideBar extends Box {

    private final Button saveButton;
    private final FileBox fileBox;

    private BoxSelectionWidget<SidebarDatapackEntry> widget;
    private static LinkedHashMap<String, BoxSelectionWidget.AbstractEntry<SidebarDatapackEntry>> entries = new LinkedHashMap<>();

    public SaveSideBar(int x, int y, int width, int height, FileBox fileBox) {
        super(x, y, width, height);
        this.fileBox = fileBox;
        saveButton = new Button((width - 60) / 2, height - 40, 60, 20, Component.nullToEmpty("Export"), button -> {
            for (BoxSelectionWidget.AbstractEntry<SidebarDatapackEntry> value : entries.values()) {
                SidebarDatapackEntry entry = value.get();
                String formattedSavePath = String.format(entry.getEditor().savePath(), entry.getFileName().getNamespace(), "");
                try {
                    JsonObject object = new JsonObject();
                    entry.getEditor().serialize(object);
                    Files.createDirectories(ProfessionPlatform.platform.getRootConfigPath().resolve(formattedSavePath));
                    Files.writeString(ProfessionPlatform.platform.getRootConfigPath().resolve(formattedSavePath + entry.getFileName().getPath() + ".json"),
                            ProfessionLoader.serialize(object));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        widget = new BoxSelectionWidget<>(Minecraft.getInstance(), width - 8, height, 0, height - 60, 30);
        widget.setLeftPos(0);
        for (BoxSelectionWidget.AbstractEntry<SidebarDatapackEntry> child : entries.values()) {
            child.setWidth(width - 8);
        }
        widget.addEntries(entries.values().stream().toList());
    }

    public void addEntry(ResourceLocation location, DatapackScreen screen, DatapackEditor<?> editor) {
        BoxSelectionWidget.BoxEntry<SidebarDatapackEntry> entry = new BoxSelectionWidget.BoxEntry<>(new SidebarDatapackEntry(0, 0, width - 8, 30, 1.5f, 12, location, editor, button -> {
            screen.setFileName(button.getFileName().toString());
            Minecraft.getInstance().setScreen(screen);
        }));
        if (!entries.containsKey(location + editor.datapackType())) {
            entries.put(location + editor.datapackType(), entry);
            widget.addSingleEntry(entry);
        }
    }

    public void rename(String oldName, DatapackEditor<?> editor, ResourceLocation newName) {
        String key = oldName + editor.datapackType();

        BoxSelectionWidget.AbstractEntry<SidebarDatapackEntry> entry = entries.get(key);
        if (entry != null) {
            entries.remove(key);
            entry.get().setFileName(newName);
            entries.put(newName + editor.datapackType(), entry);
        }
    }

    @Override
    public List<Widget> children() {
        List<Widget> widgets = new ArrayList<>(List.of(saveButton, fileBox));
        widgets.addAll(fileBox.children());
        return widgets;
    }

    public FileBox getFileBox() {
        return fileBox;
    }

    public BoxSelectionWidget getWidget() {
        return widget;
    }
}
