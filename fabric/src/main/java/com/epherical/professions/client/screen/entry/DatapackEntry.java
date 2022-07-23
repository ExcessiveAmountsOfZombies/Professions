package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class DatapackEntry extends AbstractWidget implements Parent {

    private static final Logger LOGGER = LogUtils.getLogger();

    protected final Minecraft minecraft = Minecraft.getInstance();

    private final Type[] types;
    private final TinyButton[] buttonTypes;
    protected final List<AbstractWidget> children = new ArrayList<>();


    public DatapackEntry(int x, int y, int width, int height, Type... types) {
        super(x, y, width, height, Component.nullToEmpty(""));
        this.types = types;
        buttonTypes = new TinyButton[types.length];
        int start = width - (12 * types.length);

        for (int z = 0; z < this.types.length; z++) {
            int increment = 8 * z;
            int finalZ = z;
            buttonTypes[z] = new TinyButton(x + start + increment, y + 2, 7, 7, types[z], button -> {
                System.out.println("bozo lul ");
                this.clickTinyButton((TinyButton) button);
            }, (button, poseStack, mouseX, mouseY) -> {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.screen.renderTooltip(poseStack, types[finalZ].text, mouseX, mouseY);
            });
            children.add(buttonTypes[z]);
        }
    }

    public DatapackEntry(int x, int y, int width, Type... types) {
        this(x, y, width, 23, types);
    }

    public void initPosition(int initialX, int initialY) {

    }

    public void tick(DatapackScreen screen) {

    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }


    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, DatapackScreen.WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, 0, 0, this.width / 2, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 256 - this.width / 2, 0, this.width / 2, this.height);
        //this.blit(poseStack, this.x + this.width / 2, this.y, 0, 0, 240, this.height);
        //this.blit(poseStack, this.x + this.width / 4, this.y, 3, 0, 240, this.height);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);


        //int j = this.active ? 16777215 : 10526880;
        //drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        for (TinyButton buttonType : this.buttonTypes) {
            buttonType.render(poseStack, mouseX, mouseY, partialTick);
            if (buttonType.isHoveredOrFocused()) {
                this.isHovered = false;
            }
        }
        for (AbstractWidget child : children) {
            child.render(poseStack, mouseX, mouseY, partialTick);
        }
    }

    public void setX(int x) {
        LOGGER.info("setting X, old {}, new {}", this.x, x);
        this.x = x;
    }

    public void setY(int y) {
        LOGGER.info("setting Y, old {}, new {}", this.y, y);
        this.y = y;
    }

    @Override
    public List<? extends AbstractWidget> children() {
        return children;
    }

    public void clickTinyButton(TinyButton button) {

    }

    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY, Component component) {
        super.renderToolTip(poseStack, mouseX, mouseY);
        minecraft.screen.renderTooltip(poseStack, component, mouseX, mouseY);
    }

    public List<AbstractWidget> flattenEntries(List<AbstractWidget> total, AbstractWidget current) {
        System.out.println("Flattening entry for: " + current.getClass().getName());
        if (current instanceof Parent parent) {
            for (AbstractWidget child : parent.children()) {
                total.add(child);
                flattenEntries(total, child);
            }
        }
        return total;
    }

    public static class TinyButton extends Button {

        private final Type type;

        public TinyButton(int i, int j, int k, int l, Type type, OnPress onPress) {
            this(i, j, k, l, type, onPress, NO_TOOLTIP);
        }

        public TinyButton(int i, int j, int k, int l, Type type, Button.OnPress onPress, Button.OnTooltip onTooltip) {
            super(i, j, k, l, Component.nullToEmpty(""), onPress, onTooltip);
            this.type = type;
        }


        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            super.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, DatapackScreen.WINDOW_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            int i = this.getYImage(this.isHoveredOrFocused());
            if (i == 1) {
                i = 0;
            } else {
                i = 1;
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            this.blit(poseStack, this.x, this.y, i * 7, 205, 7, 7);
            this.blit(poseStack, this.x + 1, this.y + 1, type.ordinal() * 5, 214, 5, 5);
            if (this.isHoveredOrFocused()) {
                this.renderToolTip(poseStack, mouseX, mouseY);
            }
        }

        public Type getType() {
            return type;
        }
    }


    public enum Type {
        ADD(new TextComponent("Add")),
        REMOVE(new TextComponent("Remove")),
        EDIT(new TextComponent("Edit"));

        private final Component text;

        Type(MutableComponent text) {
            this.text = text;
        }
    }
}
