package com.epherical.professions.listener;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.GateManager;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.util.GateUtil;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class FabricGateListenerServer {

    private FabricGateListenerServer() {}

    public static void register() {
        AttackBlockCallback.EVENT.register(FabricGateListenerServer::leftClickInteraction);
        PlayerBlockBreakEvents.BEFORE.register(FabricGateListenerServer::onBlockBreak);
        UseBlockCallback.EVENT.register(FabricGateListenerServer::placeBlockInteraction);
        UseItemCallback.EVENT.register(FabricGateListenerServer::placeItemInteraction);
    }

    private static InteractionResult leftClickInteraction(Player player, Level world, InteractionHand hand, BlockPos pos,
                                                          Direction direction) {
        if (world.isClientSide() || player.isSpectator()) {
            return InteractionResult.PASS;
        }

        IProfessionalPlayer professionalPlayer = getProfessionalPlayer(player);
        if (professionalPlayer == null) {
            return InteractionResult.PASS;
        }

        boolean allowed = handleBlockBreakGateManagement(world, player, player.getItemInHand(hand), pos, professionalPlayer);
        return allowed ? InteractionResult.PASS : InteractionResult.FAIL;
    }

    private static boolean onBlockBreak(Level world, Player player, BlockPos pos, BlockState state,
                                        @Nullable BlockEntity blockEntity) {
        if (player.isSpectator()) {
            return true;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return true;
        }

        IProfessionalPlayer professionalPlayer = getProfessionalPlayer(player);
        if (professionalPlayer == null) {
            return true;
        }

        GateManager gateManager = mod.getGateManager();
        ProfessionContext context = ProfessionContext.gateBuilder(world, Gates.BLOCK_BREAK, professionalPlayer)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, player.getMainHandItem())
                .addParameter(ProfessionParameter.BLOCKPOS, pos)
                .addParameter(ProfessionParameter.THIS_BLOCK_STATE, state)
                .build();

        return GateUtil.passesBlockBreakGateChecks(gateManager, professionalPlayer, context);
    }

    private static InteractionResult placeBlockInteraction(Player player, Level world, InteractionHand hand,
                                                           BlockHitResult hitResult) {
        if (world.isClientSide() || player.isSpectator()) {
            return InteractionResult.PASS;
        }

        boolean allowed = handleBlockPlacement(player, player.getItemInHand(hand));
        return allowed ? InteractionResult.PASS : InteractionResult.FAIL;
    }

    private static InteractionResult placeItemInteraction(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide() || player.isSpectator()) {
            return InteractionResult.PASS;
        }

        boolean allowed = handleBlockPlacement(player, stack);
        return allowed ? InteractionResult.PASS : InteractionResult.FAIL;
    }

    public static boolean handleBlockBreakGateManagement(Level level, Player player, ItemStack itemStack, BlockPos pos,
                                                         IProfessionalPlayer professionalPlayer) {
        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return true;
        }

        GateManager gateManager = mod.getGateManager();
        professionalPlayer.setPlayer(player);

        ProfessionContext context = ProfessionContext.gateBuilder(level, Gates.BLOCK_BREAK, professionalPlayer)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, itemStack)
                .addParameter(ProfessionParameter.BLOCKPOS, pos)
                .addParameter(ProfessionParameter.THIS_BLOCK_STATE, level.getBlockState(pos))
                .build();

        return GateUtil.passesBlockBreakGateChecks(gateManager, professionalPlayer, context);
    }

    private static boolean handleBlockPlacement(Player player, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof BlockItem)) {
            return true;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return true;
        }

        IProfessionalPlayer professionalPlayer = getProfessionalPlayer(player);
        if (professionalPlayer == null) {
            return true;
        }

        GateManager gateManager = mod.getGateManager();
        ProfessionContext context = ProfessionContext.gateBuilder(player.level(), Gates.PLACE, professionalPlayer)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, itemStack)
                .build();

        return GateUtil.checkGate(gateManager, gateManager.getGatesByType(Gates.PLACE), professionalPlayer, context);
    }

    private static @Nullable IProfessionalPlayer getProfessionalPlayer(Player player) {
        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return null;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return null;
        }
        return FabricProfessionsMod.ensureProfessionalPlayer(mod, serverPlayer);
    }
}
