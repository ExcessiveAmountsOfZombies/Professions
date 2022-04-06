package com.epherical.professions.trigger;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class RewardHandler {

    public static void handleReward(ProfessionContext context) {
        ProfessionalPlayer pPlayer = context.getParameter(ProfessionParameter.THIS_PLAYER);
        ServerPlayer player = pPlayer.getPlayer();
        if (player != null && player.isCreative() && (!ProfessionConfig.allowCreativeModePayments || !Permissions.check(player, "professions.reward.creative", 4))) {
            return;
        }

        pPlayer.handleAction(context);
    }
}
