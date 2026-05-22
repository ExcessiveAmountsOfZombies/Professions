package com.epherical.professions.listener;

import com.epherical.professions.GateManager;
import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.Gate;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Collection;

@EventBusSubscriber(modid = ProfessionsCommon.MOD_ID)
public class NeoforgeGateListenerServer {



    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void leftClickInteraction(PlayerInteractEvent.LeftClickBlock event) {

        if (event.isCanceled()) {
            return;
        }

        if (event.getSide().isClient()) {
            return;
        }

        handleBlockBreakGateManagement(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }

        GateManager gateManager = ProfessionsCommon.INSTANCE.getGateManager();
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer player = playerManager.getPlayer(event.getPlayer().getUUID());

        if (player == null) {
            return;
        }

        ProfessionContext context = ProfessionContext.gateBuilder(event.getPlayer().level(), Gates.BLOCK_BREAK, player)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getPlayer().getMainHandItem())
                .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                .addParameter(ProfessionParameter.THIS_BLOCK, event.getLevel().getBlockState(event.getPos()))
                .build();

        passesGateCheck(event, gateManager, player, context);

    }

    public static void passesGateCheck(ICancellableEvent event, GateManager gateManager, IProfessionalPlayer player, ProfessionContext context) {
        boolean passedGate = checkGate(gateManager.getGatesByType(Gates.BLOCK_BREAK), player, context);
        if (!passedGate) {
            event.setCanceled(true);
        } else {
            passedGate = checkGate(gateManager.getGatesByType(Gates.TOOL), player, context);
            if (!passedGate) {
                event.setCanceled(true);
            }
        }
    }

    public static boolean checkGate(Collection<Gate<?>> gates, IProfessionalPlayer player, ProfessionContext context) {
        GateManager gateManager = ProfessionsCommon.INSTANCE.getGateManager();
        if (!gateManager.areGatesEnabled(player)) {
            return true;
        }
        for (Gate<?> gate : gates) {
            Occupation occupation = player.getOccupation(gate.getProfession());
            if (occupation == null || !occupation.isActive() || !player.getCategory().hasProfession(gate.getProfession())) continue;
            if (!gate.meetsRequirements(occupation, context)) {
                return false;
            }
        }
        return true;
    }

    public static void handleBlockBreakGateManagement(PlayerInteractEvent.LeftClickBlock event) {
        GateManager gateManager = ProfessionsCommon.INSTANCE.getGateManager();
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer player = playerManager.getPlayer(event.getEntity().getUUID());

        if (player == null) {
            return;
        }

        ProfessionContext context = ProfessionContext.gateBuilder(event.getLevel(), Gates.BLOCK_BREAK, player)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getItemStack())
                .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                .addParameter(ProfessionParameter.THIS_BLOCK, event.getLevel().getBlockState(event.getPos()))
                .build();

        passesGateCheck(event, gateManager, player, context);

        if (event.isCanceled()) {
            event.getEntity().sendSystemMessage(Component.translatable("professions.gate.error.tool_level"));
        }
    }


}
