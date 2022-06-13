package com.epherical.professions.trigger;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.util.ActionLogger;
import com.mojang.logging.LogUtils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.NoSuchElementException;

public class RewardHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleReward(ProfessionContext.Builder contextBuilder) {
        contextBuilder.addParameter(ProfessionParameter.ACTION_LOGGER, new ActionLogger());
        ProfessionContext context = contextBuilder.build();
        try {
            ProfessionalPlayer pPlayer = context.getParameter(ProfessionParameter.THIS_PLAYER);
            ServerPlayer player = pPlayer.getPlayer();
            if (player != null && player.isCreative() && (!ProfessionConfig.allowCreativeModePayments || !Permissions.check(player, "professions.reward.creative", 4))) {
                return;
            }
            pPlayer.handleAction(context, player);
        } catch (NoSuchElementException e) {
            LOGGER.warn("ProfessionalPlayer did not exist, dumping parameters.");
            LOGGER.warn(context.toString());
            e.printStackTrace();
        }
    }
}
