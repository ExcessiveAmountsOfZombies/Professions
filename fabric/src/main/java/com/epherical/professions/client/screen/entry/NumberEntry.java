package com.epherical.professions.client.screen.entry;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class NumberEntry<T extends Number> extends StringEntry {

    private final String description;
    private T value;
    private Component tooltip;

    public NumberEntry(int i, int j, int k, String desc, T defaultValue) {
        super(i, j, k, desc, String.valueOf(defaultValue));
        this.description = desc;
        this.value = defaultValue;
        this.tooltip = new TextComponent(value.toString());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        /*Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, description, x + 3, y + 8, 0xFFFFFF);
        drawCenteredString(poseStack, font, value.toString(), this.width / 2, y + 8, 0x0095ba);
        //font.drawShadow(poseStack, value.toString(), this.width, y + 16, 0x0095ba);
        if (isHoveredOrFocused()) {
            renderToolTip(poseStack, mouseX, mouseY, tooltip);
        }*/
    }

    @Override
    public String getType() {
        return "Number";
    }

    @Override
    public String toString() {
        return "NumberEntry{" +
                "description='" + description + '\'' +
                ", value=" + value +
                "} " + super.toString();
    }
}
