package com.epherical.professions.client;

import com.epherical.professions.client.button.ButtonPress;
import com.epherical.professions.client.screen.MenuScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class ButtonBox<T extends ButtonBox<T>> extends Box implements GuiEventListener, NarratableEntry {

    private final String message;
    private final ButtonPress<T> box;

    private final float msgScale;
    private int yOffset;

    public ButtonBox(int x, int y, int width, int height, String message, ButtonPress<T> box) {
        this(x, y, width, height, 3f, 0, message, box);
    }

    public ButtonBox(int x, int y, int width, int height, float msgScale, int yOffset, String message, ButtonPress<T> box) {
        super(x, y, width, height);
        this.message = message;
        this.box = box;
        this.yOffset = yOffset;
        this.msgScale = msgScale;
    }

    @Override
    public List<Widget> children() {
        return Lists.newArrayList();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (isMouseOver(mouseX, mouseY)) {
            fill(stack, x, y, x + width + 1, y + height + 1, 0xAAFFFFFF);
        }
        super.render(stack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        MenuScreen.drawScaledTextCentered(stack, font, getMessage(), ((x + width) - width / 2), (y + height / 2 - 12) + yOffset, 0xFFFFFF, msgScale);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            boolean mouseOver = isMouseOver(mouseX, mouseY);
            if (mouseOver) {
                this.box.onPress((T) this);
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
        }
        return false;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }
}
