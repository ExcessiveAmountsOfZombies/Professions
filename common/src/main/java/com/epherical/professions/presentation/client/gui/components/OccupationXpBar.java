package com.epherical.professions.presentation.client.gui.components;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class OccupationXpBar implements Renderable {

    private static final int WIDTH = 86;
    private static final int HEIGHT = 12;
    private static final Identifier EMPTY_SPRITE = Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_xp_bar_empty");
    private static final Identifier FULL_SPRITE = Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation/occupation_xp_bar_full");

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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED,EMPTY_SPRITE, x, y, WIDTH, HEIGHT);

        int filledWidth = Mth.clamp((int) Math.round(WIDTH * percentage), 0, WIDTH);
        if (filledWidth > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FULL_SPRITE, WIDTH, HEIGHT, 0, 0, x, y, filledWidth , HEIGHT);
        }
    }
}
