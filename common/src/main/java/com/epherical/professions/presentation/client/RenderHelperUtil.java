package com.epherical.professions.presentation.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;

public class RenderHelperUtil {



    public static void drawScaled(GuiGraphics gfx, int x, int y, float scale, Runnable voidConsumer) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        voidConsumer.run();
        gfx.pose().popPose();
    }

    public static void drawScaledString(GuiGraphics gfx, Font font, String text, int x, int y, float scale,
                                  int color, boolean dropShadow) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawString(font, text, 0, 0, color, dropShadow);
        gfx.pose().popPose();
    }

    public static void drawWrappedScaledString(GuiGraphics gfx, Font font, String text, int x, int y, float scale, int lineWidth, int color) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawWordWrap(font, FormattedText.of(text), 0, 0, lineWidth, color);
        gfx.pose().popPose();
    }
}
