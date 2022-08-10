package com.epherical.professions.client.screen;

import com.epherical.professions.client.ButtonBox;
import com.epherical.professions.client.editor.EditorContainer;
import com.epherical.professions.client.editors.DatapackEditor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditOrCreateScreen extends Screen {

    private ButtonBox edit;
    private ButtonBox create;

    private final EditorContainer<?> container;

    public EditOrCreateScreen(EditorContainer<?> container) {
        super(Component.nullToEmpty("Edit & Create"));
        this.container = container;
    }

    @Override
    protected void init() {
        super.init();
        int boxWidth = this.width / 4;
        int xSpacing = 50;
        edit = new ButtonBox(xSpacing, this.height / 2 - 60, boxWidth, 60, "Edit", button -> {
            Minecraft.getInstance().setScreen(new EditChoiceScreen<>(container));
        });
        create = new ButtonBox(this.width - xSpacing - boxWidth, this.height / 2 - 60, boxWidth, 60, "Create", button -> {
            int ofx = 32;
            int width = this.width - 50;
            Minecraft.getInstance().setScreen(new DatapackScreen((DatapackEditor<?>) container.getCreator().apply(ofx, width)));
        });

        addRenderableWidget(edit);
        addRenderableWidget(create);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
