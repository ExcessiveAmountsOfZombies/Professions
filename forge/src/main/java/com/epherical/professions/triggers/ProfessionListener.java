package com.epherical.professions.triggers;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.capability.impl.PlayerOwnableImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
}
