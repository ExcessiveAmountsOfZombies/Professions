package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.server.S2CExperienceGainPayload;
import com.epherical.professions.presentation.client.RenderHelperUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

/**
 * Client-side hotbar EXP notification state machine.
 * <p>
 * Receives {@link S2CExperienceGainPayload} events, accumulates them into a target EXP total,
 * animates the displayed EXP toward that target, and hides the notification once the burst is
 * idle and fully caught up.
 */
public class ExperienceNotificationHandler {

    /**
     * Time window that defines a single "gain burst".
     * Payloads that arrive after this timeout start a new notification sequence.
     */
    private static final long XP_NOTIFICATION_WINDOW_MS = 3000L;

    /**
     * Minimum speed (EXP/sec) for the smooth counter animation.
     */
    private static final double CATCH_UP_BASE_RATE = 10.0D; // exp/sec

    /**
     * Dynamic speed scaling: larger backlog -> faster catch-up.
     */
    private static final double CATCH_UP_BACKLOG_MULTIPLIER = 2.0D; // exp/sec per buffered exp

    /**
     * Comparison tolerance for floating point "close enough" checks.
     */
    private static final double EPSILON = 1D;

    /**
     * Profession label currently shown in the notification.
     */
    private static ResourceLocation activeProfession;
    private static Component activeProfessionLabel;

    /**
     * Total EXP accumulated for the current burst (what we want to reach).
     */
    private static double targetExp;

    /**
     * Current rendered EXP value (what the player sees right now).
     */
    private static double displayedExp;

    /**
     * Last time we received an EXP payload.
     */
    private static long lastExpTimestamp;

    /**
     * Ingests a new EXP gain payload.
     * <p>
     * If the payload matches the current profession and arrives within the active window,
     * it adds onto the existing burst. Otherwise, it starts a new burst.
     */
    public static void handle(S2CExperienceGainPayload payload) {

        // todo; in the future i'll turn this into something can handle multiple ways of displaying the notification
        // todo; also add a texture, so the user can decide how it'll look.


        Minecraft.getInstance().doRunTask(() -> {
            long now = System.currentTimeMillis();
            boolean sameProfession = payload.professionId().equals(activeProfession);
            boolean withinWindow = now - lastExpTimestamp <= XP_NOTIFICATION_WINDOW_MS;

            if (!sameProfession || !withinWindow) {
                activeProfession = payload.professionId();
                activeProfessionLabel = resolveProfessionLabel(payload.professionId());
                targetExp = 0.0D;
                displayedExp = 0.0D;
            }

            targetExp += payload.experienceGained();
            lastExpTimestamp = now;
        });
    }

    /**
     * Advances animation state by one render tick.
     *
     * @return {@code true} when a notification should be drawn.
     */
    private static boolean tick(DeltaTracker deltaTracker) {
        if (activeProfession == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        double deltaSeconds = Math.clamp(deltaTracker.getRealtimeDeltaTicks() / 20.0D, 0.0D, 0.2D);

        // Smoothly approach the buffered target amount. The further behind we are, the faster we animate.
        double remaining = Math.max(0.0D, targetExp - displayedExp);
        if (remaining > EPSILON && deltaSeconds > 0.0D) {
            double catchUpRate = Math.max(CATCH_UP_BASE_RATE, remaining * CATCH_UP_BACKLOG_MULTIPLIER);
            displayedExp = Math.min(targetExp, displayedExp + catchUpRate * deltaSeconds);
            if (targetExp - displayedExp < EPSILON) {
                displayedExp = targetExp;
            }
        }

        // Hide only when input has gone idle and the visual counter has fully caught up.
        boolean idleTooLong = now - lastExpTimestamp > XP_NOTIFICATION_WINDOW_MS;
        boolean caughtUp = targetExp - displayedExp < EPSILON;
        if (idleTooLong && caughtUp) {
            activeProfession = null;
            activeProfessionLabel = null;
            targetExp = 0.0D;
            displayedExp = 0.0D;
            lastExpTimestamp = 0L;
            return false;
        }

        return true;
    }

    private static String formatExp(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static Component resolveProfessionLabel(ResourceLocation professionId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return Component.literal(professionId.getPath());
        }

        IProfessionalPlayer professionalPlayer = ProfessionsCommon.INSTANCE.getPlayerManager().getPlayer(minecraft.player.getUUID());
        if (professionalPlayer == null) {
            return Component.literal(professionId.getPath());
        }

        for (Occupation occupation : professionalPlayer.getAllOccupations()) {
            if (occupation.getProfessionKey().equals(professionId)) {
                return occupation.getProfessionHolder().map(professionHolder -> {
                    Profession value = professionHolder.value();
                    return Component.literal(value.displayNameRaw()).setStyle(Style.EMPTY.withColor(value.professionColor()));
                }).orElse(Component.literal(professionId.getPath()));
            }
        }

        return Component.literal(professionId.getPath());
    }

    /**
     * Renders the current EXP notification above the hotbar.
     */
    public static void render(GuiGraphics gfx, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.options.hideGui || minecraft.player == null || !tick(deltaTracker)) {
            return;
        }

        int screenWidth = gfx.guiWidth();
        int screenHeight = gfx.guiHeight();

        Component profession = activeProfessionLabel;
        String xp = " +" + formatExp(displayedExp) + " XP";

        int professionColor = 0xFF55FF55; // ARGB green
        int xpColor = 0xFFFFD34E;         // ARGB gold

        int totalTextWidth = minecraft.font.width(profession) + minecraft.font.width(xp);
        int x = ((screenWidth - totalTextWidth) / 2) + 140;
        int y = screenHeight - 20; // above hotbar
        int professionWidth = minecraft.font.width(profession);

        RenderHelperUtil.drawScaled(gfx, x, y, 0.5f, () -> {
            gfx.fill(-2, -3, totalTextWidth + 2, 11, 0xAA000000);
            gfx.drawString(minecraft.font, profession, 0, 0, professionColor, true);
            gfx.drawString(minecraft.font, xp, professionWidth, 0, xpColor, true);
        });

    }
}
