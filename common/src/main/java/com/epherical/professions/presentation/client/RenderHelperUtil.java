package com.epherical.professions.presentation.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;

public class RenderHelperUtil {



    public static void drawScaled(GuiGraphicsExtractor gfx, int x, int y, float scale, Runnable voidConsumer) {
        gfx.pose().pushMatrix();
        gfx.pose().translate(x, y);
        gfx.pose().scale(scale, scale);
        voidConsumer.run();
        gfx.pose().popMatrix();
    }

    public static void drawScaledString(GuiGraphicsExtractor gfx, Font font, String text, int x, int y, float scale,
                                  int color, boolean dropShadow) {
        gfx.pose().pushMatrix();
        gfx.pose().translate(x, y);
        gfx.pose().scale(scale, scale);
        gfx.text(font, text, 0, 0, ARGB.color(0xFF, color), dropShadow);
        gfx.pose().popMatrix();
    }

    public static void drawScaledString(GuiGraphics gfx, Font font, Component text, int x, int y, float scale,
                                        int color, boolean dropShadow) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawString(font, text, 0, 0, color, dropShadow);
        gfx.pose().popPose();
    }

    public static void drawWrappedScaledString(GuiGraphicsExtractor gfx, Font font, String text, int x, int y, float scale, int lineWidth, int color) {
        gfx.pose().pushMatrix();
        gfx.pose().translate(x, y);
        gfx.pose().scale(scale, scale);
        gfx.textWithWordWrap(font, FormattedText.of(text), 0, 0, lineWidth, ARGB.color(0xFF, color), false);
        gfx.pose().popMatrix();
    }

    public static void drawWrappedScaledString(GuiGraphics gfx, Font font, Component text, int x, int y, float scale, int lineWidth, int color) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawWordWrap(font, text, 0, 0, lineWidth, color);
        gfx.pose().popPose();
    }

}
