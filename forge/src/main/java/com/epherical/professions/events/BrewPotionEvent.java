package com.epherical.professions.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

public class BrewPotionEvent extends Event {


    private final UUID placedBy;
    private final ItemStack brewingIngredient;
    private final BrewingStandBlockEntity blockEntity;

    public BrewPotionEvent(UUID placedBy, ItemStack brewingIngredient, BrewingStandBlockEntity blockEntity) {
        this.placedBy = placedBy;
        this.brewingIngredient = brewingIngredient;
        this.blockEntity = blockEntity;
    }

    public BrewingStandBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ItemStack getBrewingIngredient() {
        return brewingIngredient;
    }

    public UUID getPlacedBy() {
        return placedBy;
    }
}
