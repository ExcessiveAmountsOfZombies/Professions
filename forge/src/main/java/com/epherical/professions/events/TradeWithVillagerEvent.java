package com.epherical.professions.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.eventbus.api.Event;

public class TradeWithVillagerEvent extends Event {


    private final ServerPlayer player;
    private final AbstractVillager villager;
    private final MerchantOffer offer;

    public TradeWithVillagerEvent(ServerPlayer player, AbstractVillager villager, MerchantOffer offer) {
        this.player = player;
        this.villager = villager;
        this.offer = offer;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public AbstractVillager getVillager() {
        return villager;
    }

    public MerchantOffer getOffer() {
        return offer;
    }
}
