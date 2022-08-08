package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
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
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class DatapackEntry<T, SELF> extends AbstractWidget implements Parent, Scrollable, IdentifiableEntry {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int TEXT_COLOR = 0xFFFFFF;

    protected final Minecraft minecraft = Minecraft.getInstance();

    private final Type[] types;
    protected final TinyButton[] buttonTypes;
    protected final List<AbstractWidget> children = new ArrayList<>();

    protected final List<TinyButtonListener> listeners = new ArrayList<>();

    protected final Optional<String> serializationKey;

    protected BiConsumer<T, SELF> deserializer;

    protected int xScroll = 0;
    protected int yScroll = 0;

    public DatapackEntry(int x, int y, int width, int height, Optional<String> optionalSerializationKey, Type... types) {
        super(x, y, width, height, Component.nullToEmpty(""));
        this.types = types;
        this.serializationKey = optionalSerializationKey;
        buttonTypes = new TinyButton[types.length];
        int start = x - 10 + (types.length);

        for (int z = 0; z < this.types.length; z++) {
            int increment = 6 * z;
            int finalZ = z;
            buttonTypes[z] = new TinyButton(x + start + increment, y + 2, 7, 7, types[z], button -> {
                for (TinyButtonListener listener : listeners) {
                    listener.clicked((TinyButton) button);
                }
            }, (button, poseStack, mouseX, mouseY) -> {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.screen.renderTooltip(poseStack, types[finalZ].text, mouseX, mouseY);
            }, this);
            children.add(buttonTypes[z]);
        }
    }

    public DatapackEntry(int x, int y, int width, int height, Type... types) {
        this(x, y, width, height, Optional.empty(), types);
    }

    public DatapackEntry(int x, int y, int width, Type... types) {
        this(x, y, width, 23, types);
    }

    public DatapackEntry(int x, int y, int width, Optional<String> serializationKey, Type... types) {
        this(x, y, width, 23, serializationKey, types);
    }

    public void initPosition(int initialX, int initialY) {

    }

    public void tick(CommonDataScreen screen) {

    }

    public void rebuildTinyButtons(CommonDataScreen screen) {
        for (TinyButton buttonType : buttonTypes) {
            screen.addChild(buttonType);
        }
    }

    public abstract void onRebuild(CommonDataScreen screen);

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }


    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CommonDataScreen.WINDOW_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        if (this.width > 256) {
            // we take 16 from the beginning and 16 from the end, leaving us with 224 in the middle
            int unusedLengthPixels = 224;
            int buttonWidth = this.width - 32;

            // how many full iterations of 224 we can go
            int multiply = buttonWidth / unusedLengthPixels;
            int pixelsRemaining = buttonWidth % unusedLengthPixels;
            int startPos = 0;
            this.blit(poseStack, (this.x + getXScroll()), (this.y + getYScroll()), 0, 0, 16, this.height);
            for (int i = 0; i < multiply; i++) {
                startPos = 16 + (i * 224);
                //                x,                 y,  uOffset, vOffset, uWidth, vHeight
                this.blit(poseStack, (this.x + getXScroll()) + startPos, (this.y + getYScroll()), 16, 0, 224, this.height);
            }
            this.blit(poseStack, (this.x + getXScroll()) + (startPos += 224), (this.y + getYScroll()), 16, 0, pixelsRemaining, this.height);
            this.blit(poseStack, (this.x + getXScroll()) + (startPos + pixelsRemaining), (this.y + getYScroll()), 240, 0, 16, this.height);
        } else {
            this.blit(poseStack, this.x + getXScroll(), this.y + getYScroll(), 0, 0, this.width / 2, this.height);
            this.blit(poseStack, this.x + getXScroll() + this.width / 2, this.y + getYScroll(), 256 - this.width / 2, 0, this.width / 2, this.height);
        }

        this.renderBg(poseStack, minecraft, mouseX, mouseY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.isHovered = mouseX >= this.x + getXScroll() && mouseY >= this.y + getYScroll() && mouseX < this.x + this.width + getXScroll() && mouseY < this.y + this.height + getYScroll();
        for (TinyButton buttonType : this.buttonTypes) {
            buttonType.y = y + 2;
        }
        for (AbstractWidget child : children) {
            if (child.isHoveredOrFocused()) {
                this.isHovered = false;
            }
            child.render(poseStack, mouseX, mouseY, partialTick);
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double) (this.x + getXScroll()) && mouseY >= (double) (this.y + getYScroll()) && mouseX < (double) (this.x + this.width + getXScroll()) && mouseY < (double) (this.y + this.height + getYScroll());
    }

    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double) (this.x + getXScroll()) && mouseY >= (double) (this.y + getYScroll()) && mouseX < (double) (this.x + this.width + getXScroll()) && mouseY < (double) (this.y + this.height + getYScroll());
    }

    public void setX(int x) {
        //LOGGER.info("setting X, old {}, new {}", this.x, x);
        this.x = x;
    }

    public void setY(int y) {
        //LOGGER.info("setting Y, old {}, new {}", this.y, y);
        this.y = y;
    }

    @Override
    public void setXScroll(int x) {
        this.xScroll = x;
    }

    @Override
    public void setYScroll(int y) {
        this.yScroll = y;
    }

    @Override
    public int getXScroll() {
        return xScroll;
    }

    @Override
    public int getYScroll() {
        return yScroll;
    }

    public Optional<String> getSerializationKey() {
        return serializationKey;
    }

    @Override
    public List<? extends AbstractWidget> children() {
        return children;
    }

    public void addListener(TinyButtonListener listener) {
        listeners.add(listener);
    }

    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY, Component component) {
        super.renderToolTip(poseStack, mouseX, mouseY);
        minecraft.screen.renderTooltip(poseStack, component, mouseX, mouseY);
    }

    public List<AbstractWidget> flattenEntries(List<AbstractWidget> total, AbstractWidget current) {
        if (current instanceof Parent parent) {
            for (AbstractWidget child : parent.children()) {
                total.add(child);
                flattenEntries(total, child);
            }
        }
        return total;
    }

    public abstract JsonElement getSerializedValue();

    /**
     * DO NOT CALL {@link #deserialize(Object)} on SELF.
     * @param consumer
     * @return itself.
     */
    private SELF addDeserializer(BiConsumer<T, SELF> consumer) {
        deserializer = consumer;
        return (SELF) this;
    }

    /**
     * A way to convert an object to its DatapackEntry equivalent. Each subclass should
     * provide their own {@link Deserializer}. That deserializer should not call this method.
     * @param object The object you want to deserialize into its DatapackEntry components.
     * @throws StackOverflowError a StackOverflowError can occur if you call deserialize on the object from the {@link Deserializer}
     */
    public abstract void deserialize(T object);

    public static class TinyButton extends Button {

        private final Type type;
        private final DatapackEntry entry;

        protected boolean clicked;


        public TinyButton(int i, int j, int k, int l, Type type, Button.OnPress onPress, Button.OnTooltip onTooltip, DatapackEntry entry) {
            super(i, j, k, l, Component.nullToEmpty(""), onPress, onTooltip);
            this.type = type;
            this.entry = entry;
        }


        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            super.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            this.isHovered = mouseX >= (this.x + entry.getXScroll()) && mouseY >= (this.y + entry.getYScroll()) && mouseX < (this.x + this.width + entry.getXScroll()) && mouseY < (this.y + this.height + entry.getYScroll());
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, CommonDataScreen.WINDOW_LOCATION);
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

            int xOffset = 0;
            int yOffset = 0;
            if (entry != null) {
                xOffset = entry.getXScroll();
                yOffset = entry.getYScroll();
            }

            this.blit(poseStack, this.x + xOffset, this.y + yOffset, i * 7, 205, 7, 7);
            this.blit(poseStack, this.x + 1 + xOffset, this.y + 1 + yOffset, type.ordinal() * 5, 214, 5, 5);
            if (this.isHoveredOrFocused()) {
                this.renderToolTip(poseStack, mouseX, mouseY);
            }
        }

        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.active && this.visible && mouseX >= (double) (this.x + entry.getXScroll()) && mouseY >= (double) (this.y + entry.getYScroll()) && mouseX < (double) (this.x + this.width + entry.getXScroll()) && mouseY < (double) (this.y + this.height + entry.getYScroll());
        }

        protected boolean clicked(double mouseX, double mouseY) {
            return this.active && this.visible && mouseX >= (double) (this.x + entry.getXScroll()) && mouseY >= (double) (this.y + entry.getYScroll()) && mouseX < (double) (this.x + this.width + entry.getXScroll()) && mouseY < (double) (this.y + this.height + entry.getYScroll());
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

    public interface Deserializer<T, SELF> {
        void deserialize(T t, SELF entry);
    }
}
