package com.epherical.professions.client.screen;

import com.epherical.professions.Constants;
import com.epherical.professions.client.EditorsBox;
import com.epherical.professions.client.editor.EditorContainer;
import com.epherical.professions.client.editor.ProfessionEditor;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class MenuScreen extends Screen {

    private static final Map<ResourceLocation, EditorContainer<?>> DATAPACK_EDITORS = Maps.newLinkedHashMap();

    private EditorsBox vanilla;
    private EditorsBox modded;

    public MenuScreen() {
        super(Component.nullToEmpty("Datapack Editor Choice Menu"));
    }

    @Override
    protected void init() {
        super.init();
        int boxWidth = this.width / 4;
        int xSpacing = 100;
        vanilla = new EditorsBox(xSpacing, 20, boxWidth, this.height - 40, "Vanilla", DATAPACK_EDITORS.values().stream().filter(EditorContainer::isNotModded));
        modded = new EditorsBox(this.width - xSpacing - boxWidth, 20, boxWidth, this.height - 40, "Modded", DATAPACK_EDITORS.values().stream().filter(EditorContainer::isModded));
        addRenderableOnly(vanilla);
        addRenderableOnly(modded);

        addWidget(vanilla.getWidget());
        addWidget(modded.getWidget());
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTick);

        vanilla.getWidget().render(stack, mouseX, mouseY, partialTick);
        modded.getWidget().render(stack, mouseX, mouseY, partialTick);

        drawScaledTextCentered(stack, font, "Datapack Editors", this.width / 2, 5, 0xFFFFFF, 2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    public static void drawScaledTextCentered(PoseStack stack, Font font, String str, int x, int y, int color, float size) {
        stack.pushPose();
        stack.scale(size, size, size);
        drawCenteredString(stack, font, str, (int) (x / size), (int) (y / size), color);
        stack.scale(1 / size, 1 / size, 1 / size);
        stack.popPose();
    }


    static {
        DATAPACK_EDITORS.put(Constants.modID("profession_editor"), new EditorContainer<>("Professions", true, ProfessionEditor::new));
       /* DATAPACK_EDITORS.put(new ResourceLocation("minecraft", "loot_table"), new EditorContainer<>("Loot Tables", false, ProfessionEditor::new));
        DATAPACK_EDITORS.put(new ResourceLocation("minecraft", "advancement"), new EditorContainer<>("Advancements", false, ProfessionEditor::new));
        DATAPACK_EDITORS.put(new ResourceLocation("minecraft", "block_tag"), new EditorContainer<>("Block Tags", false, ProfessionEditor::new));
        DATAPACK_EDITORS.put(new ResourceLocation("minecraft", "item_tag"), new EditorContainer<>("Item Tags", false, ProfessionEditor::new));
        DATAPACK_EDITORS.put(new ResourceLocation("minecraft", "entity_tags"), new EditorContainer<>("Entity Tags", false, ProfessionEditor::new));
        DATAPACK_EDITORS.put(new ResourceLocation("minecraft", "recipe"), new EditorContainer<>("Recipes", false, ProfessionEditor::new));
        DATAPACK_EDITORS.put(new ResourceLocation("minecraft", "biome"), new EditorContainer<>("Biomes", false, ProfessionEditor::new));*/
    }


}
