package com.epherical.professions.client.screen;

import com.epherical.professions.client.EditorsBox;
import com.epherical.professions.client.editor.BoxSelectionWidget;
import com.epherical.professions.client.editor.EditorContainer;
import com.epherical.professions.client.editors.DatapackEditor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class EditChoiceScreen<T> extends Screen {

    private final EditorContainer<?> container;

    private EditorsBox entries;

    public EditChoiceScreen(EditorContainer<?> container) {
        super(Component.nullToEmpty("Edit"));
        this.container = container;
    }

    @Override
    protected void init() {
        super.init();
        int ofx = 32;
        int width = this.width - 50;

        int boxWidth = this.width / 3;

        DatapackEditor<T> apply = (DatapackEditor<T>) container.getCreator().apply(ofx, width);
        List<BoxSelectionWidget.Entry> boxEntries = new ArrayList<>();
        for (Button deserializableObjectButton : apply.deserializableObjectButtons(apply)) {
            deserializableObjectButton.setWidth(boxWidth - 2);
            boxEntries.add(new BoxSelectionWidget.Entry(deserializableObjectButton));
        }

        entries = new EditorsBox((this.width / 2) - boxWidth / 2, 20, boxWidth, this.height - 40, "Choices", boxEntries);

        addRenderableOnly(entries);
        addWidget(entries.getWidget());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        entries.getWidget().render(poseStack, mouseX, mouseY, partialTick);
    }

    /*@Override
    public boolean isPauseScreen() {
        return false;
    }*/
}
