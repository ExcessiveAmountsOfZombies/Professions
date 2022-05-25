package com.epherical.professions.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

public class SmeltItemEvent extends Event {

    private final UUID owner;
    private final ItemStack smeltedItem;
    private final Recipe<?> recipe;
    private final AbstractFurnaceBlockEntity blockEntity;

    public SmeltItemEvent(UUID owner, ItemStack smeltedItem, Recipe<?> recipe, AbstractFurnaceBlockEntity blockEntity) {
        this.owner = owner;
        this.smeltedItem = smeltedItem;
        this.recipe = recipe;
        this.blockEntity = blockEntity;
    }

    public UUID getOwner() {
        return owner;
    }

    public ItemStack getSmeltedItem() {
        return smeltedItem;
    }

    public Recipe<?> getRecipe() {
        return recipe;
    }

    public AbstractFurnaceBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
