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
import com.epherical.professions.model.gating.GateReport;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled() || event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        GateManager gateManager = ProfessionsCommon.INSTANCE.getGateManager();
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer player = playerManager.getPlayer(serverPlayer.getUUID());
        if (player == null) {
            return;
        }

        ProfessionContext context = ProfessionContext.gateBuilder(serverPlayer.serverLevel(), Gates.PLACE, player)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, serverPlayer.getMainHandItem())
                .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                .addParameter(ProfessionParameter.THIS_BLOCK, event.getState())
                .build();

        if (!checkGate(gateManager.getGatesByType(Gates.PLACE), player, context)) {
            event.setCanceled(true);
        }
    }

    /**
     * Event to handle block placement, it's not specific to the client so we can just do one method.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void placeBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
        handleBlockPlacement(event.getItemStack(), event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void placeBlockInteraction(PlayerInteractEvent.RightClickItem event) {
        handleBlockPlacement(event.getItemStack(), event, event.getEntity());
    }

    private static void handleBlockPlacement(ItemStack itemStack, ICancellableEvent event, Player mcPlayer) {
        if (event.isCanceled()) {
            return;
        }

        GateManager gateManager = ProfessionsCommon.INSTANCE.getGateManager();
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer player = playerManager.getPlayer(mcPlayer.getUUID());
        if (player == null || !(itemStack.getItem() instanceof BlockItem)) {
            return;
        }

        ProfessionContext context = ProfessionContext.gateBuilder(mcPlayer.level(), Gates.PLACE, player)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, itemStack)
                .build();

        event.setCanceled(!checkGate(gateManager.getGatesByType(Gates.PLACE), player, context));
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
        if (!gateManager.areGatesEnabled(player) || player.getCategory() == null) {
            return true; // good
        }
        GateReport gateReport = new GateReport();
        for (Gate<?> gate : gates) {
            Occupation occupation = player.getOccupation(gate.getProfession());
            if (occupation == null || !occupation.isActive() || !player.getCategory().hasProfession(gate.getProfession())) continue;
            gate.meetsRequirements(occupation, context, gateReport);
        }

        if (!gateReport.isAllowed()) {
            gateReport.sendFailureMessage(player.getPlayer());
        }

        return gateReport.isAllowed(); // good
    }

    public static void handleBlockBreakGateManagement(PlayerInteractEvent.LeftClickBlock event) {
        GateManager gateManager = ProfessionsCommon.INSTANCE.getGateManager();
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer player = playerManager.getPlayer(event.getEntity().getUUID());

        if (player == null) {
            return;
        }

        // todo; maybe we want the ability to add multiple gate checks...
        ProfessionContext context = ProfessionContext.gateBuilder(event.getLevel(), Gates.BLOCK_BREAK, player)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getItemStack())
                .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                .addParameter(ProfessionParameter.THIS_BLOCK, event.getLevel().getBlockState(event.getPos()))
                .build();

        passesGateCheck(event, gateManager, player, context);

        /*if (event.isCanceled()) {
            event.getEntity().sendSystemMessage(Component.translatable("professions.gate.error.tool_level"));
        }*/
    }


}
