package com.epherical.professions.presentation.client.gui.widget;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class OccupationMenuButton extends Button {


    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button_disabled"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button_highlighted"));

    public static final WidgetSprites NO_HIGHLIGHT_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button_disabled"),
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_menu_button"));

    private static final ResourceLocation TOGGLE_ON_SPRITE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_toggle_on");
    private static final ResourceLocation TOGGLE_OFF_SPRITE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_toggle_off");
    private static final ResourceLocation TOGGLE_KNOB_SPRITE = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/widget/occupation_toggle_knob");

    private static final int TOGGLE_TRACK_WIDTH = 14;
    private static final int TOGGLE_TRACK_HEIGHT = 8;
    private static final int TOGGLE_KNOB_WIDTH = 6;
    private static final int TOGGLE_KNOB_HEIGHT = 6;
    private static final int TOGGLE_LEFT_PADDING = 4;
    private static final int DEFAULT_TOGGLE_TEXT_OFFSET = 24;

    private WidgetSprites buttonSprites;
    private ResourceLocation icon;
    private BooleanSupplier toggleStateSupplier;
    private ResourceLocation toggleOnSprite;
    private ResourceLocation toggleOffSprite;
    private ResourceLocation toggleKnobSprite;
    private int toggleTextOffset;


    public OccupationMenuButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, Button.OnPress pOnPress, Button.CreateNarration pCreateNarration) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
    }

    protected OccupationMenuButton(Builder builder) {
        this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
        this.setTooltip(builder.tooltip);
        this.buttonSprites = builder.sprites;
        this.icon = builder.icon;
        this.toggleStateSupplier = builder.toggleStateSupplier;
        this.toggleOnSprite = builder.toggleOnSprite;
        this.toggleOffSprite = builder.toggleOffSprite;
        this.toggleKnobSprite = builder.toggleKnobSprite;
        this.toggleTextOffset = builder.toggleTextOffset;
    }

    public static Builder omButton(Component message, Button.OnPress onPress) {
        return new Builder(message, onPress);
    }

    public static Builder omToggleButton(Component message, Button.OnPress onPress, BooleanSupplier stateSupplier) {
        return new Builder(message, onPress).toggle(stateSupplier);
    }


    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        if (buttonSprites != null) {
            pGuiGraphics.blitSprite(buttonSprites.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else {
            pGuiGraphics.blitSprite(SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        if (icon != null) {
            pGuiGraphics.blitSprite(icon, this.getX(), this.getY(), 18, 18);
        }

        if (hasToggle()) {
            renderToggle(pGuiGraphics);
        }


        int i = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }


    @Override
    public void setFocused(boolean pFocused) {
        super.setFocused(pFocused);
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int color) {
        if (!hasToggle()) {
            super.renderString(guiGraphics, font, color);
            return;
        }

        int minX = this.getX() + this.toggleTextOffset;
        int maxX = this.getX() + this.getWidth() - 2;
        if (minX >= maxX) {
            super.renderString(guiGraphics, font, color);
            return;
        }

        renderScrollingString(guiGraphics, font, this.getMessage(), minX, this.getY(), maxX, this.getY() + this.getHeight(), color);
    }

    private void renderToggle(GuiGraphics guiGraphics) {
        boolean toggledOn = this.toggleStateSupplier.getAsBoolean();

        int trackX = this.getX() + TOGGLE_LEFT_PADDING;
        int trackY = this.getY() + (this.getHeight() - TOGGLE_TRACK_HEIGHT) / 2;
        guiGraphics.blitSprite(toggledOn ? this.toggleOnSprite : this.toggleOffSprite, trackX, trackY, TOGGLE_TRACK_WIDTH, TOGGLE_TRACK_HEIGHT);

        int knobX = toggledOn ? trackX + TOGGLE_TRACK_WIDTH - TOGGLE_KNOB_WIDTH : trackX;
        int knobY = trackY + (TOGGLE_TRACK_HEIGHT - TOGGLE_KNOB_HEIGHT) / 2;
        guiGraphics.blitSprite(this.toggleKnobSprite, knobX, knobY, TOGGLE_KNOB_WIDTH, TOGGLE_KNOB_HEIGHT);
    }

    private boolean hasToggle() {
        return this.toggleStateSupplier != null
                && this.toggleOnSprite != null
                && this.toggleOffSprite != null
                && this.toggleKnobSprite != null;
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
        private BooleanSupplier toggleStateSupplier;
        private ResourceLocation toggleOnSprite;
        private ResourceLocation toggleOffSprite;
        private ResourceLocation toggleKnobSprite;
        private int toggleTextOffset = DEFAULT_TOGGLE_TEXT_OFFSET;

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

        public Builder toggle(BooleanSupplier stateSupplier) {
            this.toggleStateSupplier = stateSupplier;
            this.toggleOnSprite = TOGGLE_ON_SPRITE;
            this.toggleOffSprite = TOGGLE_OFF_SPRITE;
            this.toggleKnobSprite = TOGGLE_KNOB_SPRITE;
            return this;
        }

        public Builder toggleSprites(ResourceLocation onSprite, ResourceLocation offSprite, ResourceLocation knobSprite) {
            this.toggleOnSprite = onSprite;
            this.toggleOffSprite = offSprite;
            this.toggleKnobSprite = knobSprite;
            return this;
        }

        public Builder toggleTextOffset(int textOffset) {
            this.toggleTextOffset = textOffset;
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
