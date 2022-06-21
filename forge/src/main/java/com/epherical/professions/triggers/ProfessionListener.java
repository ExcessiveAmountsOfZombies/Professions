package com.epherical.professions.triggers;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.capability.impl.PlayerOwnableImpl;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ProfessionUtil;
import com.epherical.professions.util.UnlockErrorHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ProfessionListener {

    @SubscribeEvent()
    public void onBlockEntity(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        BlockEntity entity = event.getWorld().getBlockEntity(event.getPos());
        if (entity != null) {
            entity.getCapability(PlayerOwnableImpl.OWNING_CAPABILITY).ifPresent(ownable -> {
                ownable.setPlacedBy((ServerPlayer) event.getEntity());
            });
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().getLevel().isClientSide) {
            ProfessionsForge.getInstance().getPlayerManager().playerJoined((ServerPlayer) event.getPlayer());
        }

    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().getLevel().isClientSide) {
            ProfessionsForge.getInstance().getPlayerManager().playerQuit((ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = false)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || event.getWorld().isClientSide()) {
            return;
        }

        ProfessionalPlayer player = CommonPlatform.platform.getPlayerManager().getPlayer(event.getPlayer().getUUID());
        if (player == null) {
            return;
        }

        // want the opposite, if it returns true, we want to set it to false, so it doesn't actually cancel the event.
        event.setCanceled(!ProfessionUtil.canBreak(player, event.getPlayer(), event.getState().getBlock()));
    }
}
