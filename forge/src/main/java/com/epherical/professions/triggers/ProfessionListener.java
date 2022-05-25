package com.epherical.professions.triggers;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ProfessionListener {

    @SubscribeEvent()
    public void onBlockEntity(BlockEvent.EntityPlaceEvent event) {
        event.getWorld().getBlockEntity(event.getPos());
    }
}
