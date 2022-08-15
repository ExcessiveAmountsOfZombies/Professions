package com.epherical.professions.mixin.accessor;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnchantmentPredicate.class)
public interface EnchantmentPredicateAccess {

    @Accessor("enchantment")
    Enchantment getEnchantment();

    @Accessor("level")
    MinMaxBounds.Ints getLevel();
}
