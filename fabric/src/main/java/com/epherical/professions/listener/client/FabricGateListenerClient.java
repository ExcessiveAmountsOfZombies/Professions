package com.epherical.professions.listener.client;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.listener.FabricGateListenerServer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class FabricGateListenerClient {

    private FabricGateListenerClient() {}

    public static void register() {
        AttackBlockCallback.EVENT.register(FabricGateListenerClient::leftClickInteraction);
    }

    private static InteractionResult leftClickInteraction(Player player, Level world, InteractionHand hand, BlockPos pos,
                                                          Direction direction) {
        if (!world.isClientSide() || player.isSpectator()) {
            return InteractionResult.PASS;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return InteractionResult.PASS;
        }

        IProfessionalPlayer professionalPlayer = mod.getPlayerManager().getPlayer(player.getUUID());
        if (professionalPlayer == null) {
            return InteractionResult.PASS;
        }

        boolean allowed = FabricGateListenerServer.handleBlockBreakGateManagement(world, player, player.getItemInHand(hand),
                pos, professionalPlayer);
        return allowed ? InteractionResult.PASS : InteractionResult.FAIL;
    }
}
