package com.epherical.professions.mixin.accessor;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ItemPredicate.class)
public interface ItemPredicateAccess {

    @Accessor("tag")
    TagKey<Item> getTag();

    @Accessor("items")
    Set<Item> getItems();

    @Accessor("count")
    MinMaxBounds.Ints getCount();

    @Accessor("durability")
    MinMaxBounds.Ints getDurability();

    @Accessor("enchantments")
    EnchantmentPredicate[] getEnchantments();

    @Accessor("storedEnchantments")
    EnchantmentPredicate[] getStoredEnchantments();

    @Accessor("potion")
    Potion getPotion();

    @Accessor("nbt")
    NbtPredicate getNbtPredicate();
}
