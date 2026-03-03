package com.epherical.professions.client.gui.components;

import com.epherical.professions.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class OccupationXpBar implements Renderable {

    private static final int WIDTH = 86;
    private static final int HEIGHT = 12;
    private static final ResourceLocation EMPTY_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "occupation/occupation_xp_bar_empty");
    private static final ResourceLocation FULL_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "occupation/occupation_xp_bar_full");

    private final double percentage;

    private int x, y;


    public OccupationXpBar(double currentXp, double maxXp, int x, int y) {
        if (maxXp <= 0) {
            this.percentage = currentXp > 0 ? 1.0 : 0.0;
            return;
        }

        this.percentage = Mth.clamp(currentXp / maxXp, 0.0, 1.0);

        this.x = x;
        this.y = y;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        guiGraphics.blitSprite(EMPTY_SPRITE, x, y, WIDTH, HEIGHT);

        int filledWidth = Mth.clamp((int) Math.round(WIDTH * percentage), 0, WIDTH);
        if (filledWidth > 0) {
            guiGraphics.blitSprite(FULL_SPRITE, WIDTH, HEIGHT, 0, 0, x, y, filledWidth , HEIGHT);
        }
    }
}
