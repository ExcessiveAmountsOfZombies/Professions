package com.epherical.professions.client.entry;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Optional;

public class NumberEntry<T extends Number, OBJ> extends EditBoxEntry<OBJ, NumberEntry<T, OBJ>> {

    private final Deserializer<OBJ, NumberEntry<T, OBJ>> deserializer;

    public NumberEntry(int i, int j, int k, String desc, T defaultValue, Deserializer<OBJ, NumberEntry<T, OBJ>> deserializer) {
        super(i, j, k, desc, String.valueOf(defaultValue), Optional.of(desc));
        this.deserializer = deserializer;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        /*Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, description, x + 3, y + 8, 0xFFFFFF);
        drawCenteredString(poseStack, font, value.toString(), this.width / 2, y + 8, TEXT_COLOR);
        //font.drawShadow(poseStack, value.toString(), this.width, y + 16, TEXT_COLOR);
        if (isHoveredOrFocused()) {
            renderToolTip(poseStack, mouseX, mouseY, tooltip);
        }*/
    }

    @Override
    public void deserialize(OBJ object) {
        deserializer.deserialize(object, this);
    }

    @Override
    public String getType() {
        return "Number";
    }
}
