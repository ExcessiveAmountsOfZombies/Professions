package com.epherical.professions.listener.client;


import com.epherical.professions.ProfessionsCommon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static com.epherical.professions.listener.NeoforgeGateListenerServer.handleBlockBreakGateManagement;

@EventBusSubscriber(value = Dist.CLIENT, modid = ProfessionsCommon.MOD_ID)
public class NeoForgeGateListenerClient {


    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void leftClickInteraction(PlayerInteractEvent.LeftClickBlock event) {

        if (event.isCanceled()) {
            return;
        }

        if (event.getSide().isServer()) {
            return;
        }

        handleBlockBreakGateManagement(event);
    }


}
