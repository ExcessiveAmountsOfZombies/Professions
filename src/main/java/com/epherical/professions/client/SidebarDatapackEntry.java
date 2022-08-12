package com.epherical.professions.client;

import com.epherical.professions.client.button.ButtonPress;
import com.epherical.professions.client.editors.DatapackEditor;
import com.epherical.professions.client.screen.MenuScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class SidebarDatapackEntry extends ButtonBox<SidebarDatapackEntry> {

    private ResourceLocation fileName;
    private final DatapackEditor<?> editor;

    public SidebarDatapackEntry(int x, int y, int width, int height, float msgScale, int yOffset, ResourceLocation location, DatapackEditor<?> editor, ButtonPress<SidebarDatapackEntry> box) {
        super(x, y, width, height, msgScale, yOffset, location.toString(), box);
        this.editor = editor;
        this.fileName = location;
    }


    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        super.render(stack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        MenuScreen.drawScaledTextCentered(stack, font, editor.datapackType(), (int) (x + 5 + font.width(editor.datapackType()) / (2 / 0.85)), y + 2, 0xFFFFFF, 0.85f);
    }

    @Override
    public String getMessage() {
        return fileName.toString();
    }

    public DatapackEditor<?> getEditor() {
        return editor;
    }

    public ResourceLocation getFileName() {
        return fileName;
    }

    public void setFileName(ResourceLocation fileName) {
        this.fileName = fileName;
    }
}
