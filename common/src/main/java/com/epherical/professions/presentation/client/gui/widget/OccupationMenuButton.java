package com.epherical.professions.presentation.client.gui.widget;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class OccupationMenuButton extends Button {


    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button_disabled"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button_highlighted"));

    private WidgetSprites buttonSprites;
    private ResourceLocation icon;


    public OccupationMenuButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, Button.OnPress pOnPress, Button.CreateNarration pCreateNarration) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
    }

    protected OccupationMenuButton(Builder builder) {
        this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
        this.setTooltip(builder.tooltip);
        this.buttonSprites = builder.sprites;
        this.icon = builder.icon;
    }

    public static Builder omButton(Component message, Button.OnPress onPress) {
        return new Builder(message, onPress);
    }


    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        if (buttonSprites != null) {
            pGuiGraphics.blitSprite(buttonSprites.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else {
            pGuiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        if (icon != null) {
            pGuiGraphics.blitSprite(icon, this.getX(), this.getY(), 18, 18);
        }


        int i = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public static class Builder {
        private final Component message;
        private final Button.OnPress onPress;
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = Button.DEFAULT_WIDTH;
        private int height = Button.DEFAULT_HEIGHT;
        private WidgetSprites sprites;
        private ResourceLocation icon;
        private Button.CreateNarration createNarration = DEFAULT_NARRATION;

        public Builder(Component message, Button.OnPress onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder background(WidgetSprites pBackground) {
            this.sprites = pBackground;
            return this;
        }

        public Builder icon(ResourceLocation pIcon) {
            this.icon = pIcon;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder createNarration(Button.CreateNarration createNarration) {
            this.createNarration = createNarration;
            return this;
        }

        public OccupationMenuButton build() {
            return new OccupationMenuButton(this);
        }

        public OccupationMenuButton build(Function<Builder, OccupationMenuButton> builder) {
            return builder.apply(this);
        }
    }
}
