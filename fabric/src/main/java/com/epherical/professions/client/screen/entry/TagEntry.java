package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TagEntry<T> extends DatapackEntry {


    //private CommandSuggestions suggestions;
    private EditBox box;
    private final Registry<T> registry;
    private T value;

    public TagEntry(int x, int y, int width, Registry<T> registry, T defaultValue) {
        super(x, y, width);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        this.box = new EditBox(font, this.x + getXScroll() + this.width / 2 - 50, y + 8 + getYScroll(), 250, 22, Component.nullToEmpty(registry.getKey(defaultValue).toString()));
        this.box.setVisible(true);
        this.box.setMaxLength(100);
        this.box.setBordered(false);
        this.box.setValue(registry.getKey(defaultValue).toString());
        this.box.setTextColor(0x0095ba);
        this.registry = registry;
        this.value = defaultValue;
        children.add(box);

    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        if (isHoveredOrFocused()) {
            renderToolTip(poseStack, mouseX, mouseY, this.box.getMessage());
        }
        //suggestions.render(poseStack, mouseX, mouseY);

        this.box.y = y + 8 + getYScroll();
    }

    @Override
    public void tick(DatapackScreen screen) {
        super.tick(screen);
        /*if (this.suggestions == null) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            this.suggestions = new CommandSuggestions(this.minecraft, screen, box, font, true, false, 1, 10, true, -805306368);
            this.suggestions.updateCommandInfo();
        }*/
        box.tick();
        box.setMessage(Component.nullToEmpty(box.getValue()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        /*if (suggestions.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }*/
        return box.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        /*this.suggestions.pendingSuggestions = getSuggestions();
        suggestions.showSuggestions(true);
        if (suggestions.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }*/
            return box.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        /*double newDelta = Mth.clamp(delta, -1.0, 1.0);
        if (suggestions.mouseScrolled(newDelta)) {
            return true;
        }*/
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // todo; this isn't a very good way of doing this.
        //  the child is ahead of the button, so the button gets called first, ignoring that we really clicked on the box first.
        return box.charTyped(codePoint, modifiers);
    }

    @Override
    public String getType() {
        return "String";
    }

    /*CompletableFuture<Suggestions> getSuggestions() {
        SuggestionsBuilder builder = new SuggestionsBuilder(box.getValue(), box.getValue().toLowerCase(Locale.ROOT), 0);
        Minecraft.getInstance().player.connection.getSuggestionsProvider()
                .suggestRegistryElements(registry, SharedSuggestionProvider.ElementSuggestionType.ALL, builder);
        return builder.buildFuture();
    }*/
}
