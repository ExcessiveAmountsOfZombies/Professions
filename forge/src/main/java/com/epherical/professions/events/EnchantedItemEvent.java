package com.epherical.professions.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class EnchantedItemEvent extends Event {

    private final ServerPlayer player;
    private final ItemStack itemStack;
    private final int levelsSpent;

    public EnchantedItemEvent(ServerPlayer player, ItemStack itemStack, int levelsSpent) {
        this.player = player;
        this.itemStack = itemStack;
        this.levelsSpent = levelsSpent;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getLevelsSpent() {
        return levelsSpent;
    }
}
