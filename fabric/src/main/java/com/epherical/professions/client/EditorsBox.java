package com.epherical.professions.client;

import com.epherical.professions.client.editor.BoxSelectionWidget;
import com.epherical.professions.client.editor.EditorContainer;
import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.client.screen.MenuScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EditorsBox extends Box {

    private final String header;
    private final BoxSelectionWidget widget;

    public EditorsBox(int x, int y, int width, int height, String header, Stream<EditorContainer<?>> containerStream) {
        super(x, y, width, height);
        this.header = header;
        widget = new BoxSelectionWidget(Minecraft.getInstance(), width, height - 20, 42, height + 4, 20);
        widget.setLeftPos(x);

        List<BoxSelectionWidget.Entry> entries = new ArrayList<>();

        int yOffset = 46;
        for (EditorContainer<?> o : containerStream.toList()) {
            entries.add(new BoxSelectionWidget.Entry(new Button(x + 40, yOffset, width - 1, 20, Component.nullToEmpty(o.getDisplayName()), button -> {
                Minecraft.getInstance().setScreen(new DatapackScreen(o.getCreator()));
            })));
            yOffset += 20;
        }
        widget.addEntries(entries);
    }

    @Override
    public List<Widget> children() {
        return List.of();
    }

    public BoxSelectionWidget getWidget() {
        return widget;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        hLine(stack, x, x + width, 45, 0xFFFFFFFF);
        super.render(stack, mouseX, mouseY, partialTick);


        Minecraft minecraft = Minecraft.getInstance();

        Font font = minecraft.font;
        MenuScreen.drawScaledTextCentered(stack, font, header, ((x + width) - width / 2), (y + 8), 0xFFFFFF, 2f);
    }
}
