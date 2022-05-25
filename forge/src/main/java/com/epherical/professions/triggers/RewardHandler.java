package com.epherical.professions.triggers;

import com.epherical.professions.ProfPermissions;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.util.ActionLogger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

public class RewardHandler {

    public static void handleReward(ProfessionContext.Builder contextBuilder) {
        contextBuilder.addParameter(ProfessionParameter.ACTION_LOGGER, new ActionLogger());
        ProfessionContext context = contextBuilder.build();
        ProfessionalPlayer pPlayer = context.getParameter(ProfessionParameter.THIS_PLAYER);
        ServerPlayer player = pPlayer.getPlayer();
        if (player != null && player.isCreative() && (!ProfessionConfig.allowCreativeModePayments || !PermissionAPI.getPermission(player, ProfPermissions.CREATIVE_PAYMENT))) {
            return;
        }

        pPlayer.handleAction(context, player);
    }
}
