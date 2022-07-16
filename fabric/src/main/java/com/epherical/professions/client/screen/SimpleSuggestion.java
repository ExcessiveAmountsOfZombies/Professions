package com.epherical.professions.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public class SimpleSuggestion extends CommandSuggestions {

    public SimpleSuggestion(Minecraft minecraft, Screen screen, EditBox editBox, Font font, boolean bl2, int i, int j, boolean bl3, int k) {
        super(minecraft, screen, editBox, font, false, bl2, i, j, bl3, k);
    }

    @Override
    public void updateCommandInfo() {
        //String string = this.input.getValue();
    }
}
