package com.epherical.professions.model.actions.conditions;

import com.epherical.professions.api.actions.Condition;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.bootstrap.Conditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.util.List;
import java.util.Optional;

public record ToolMatcher(MatchTool tool) implements Condition {

    public static final MapCodec<ToolMatcher> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            MatchTool.CODEC.fieldOf("matches").forGetter(ToolMatcher::tool))
                    .apply(i, ToolMatcher::new)
    );

    @Override
    public ConditionType getType() {
        return Conditions.TOOL_MATCHES;
    }

    @Override
    public boolean test(ProfessionContext context) {
        ItemStack stack = context.getPossibleParameter(ProfessionParameter.TOOL);
        return stack != null && (this.tool.predicate().isEmpty() || this.tool.predicate().get().test(stack));
    }

    public static class Builder implements Condition.Builder {
        private final ItemPredicate.Builder itemPredicate = ItemPredicate.Builder.item();
        private final HolderLookup.Provider registries;

        public Builder(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        public Builder items(ItemLike... items) {
            this.itemPredicate.of(items);
            return this;
        }

        public Builder itemTag(TagKey<Item> itemTag) {
            this.itemPredicate.of(itemTag);
            return this;
        }

        public <T extends ItemSubPredicate> Builder withSubPredicate(ItemSubPredicate.Type<T> type, T predicate) {
            this.itemPredicate.withSubPredicate(type, predicate);
            return this;
        }

        public Builder withEnchantments(List<EnchantmentPredicate> enchantments) {
            return withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(enchantments));
        }

        public Builder withEnchantment(ResourceKey<Enchantment> enchantment, MinMaxBounds.Ints level) {
            Holder<Enchantment> holder = registries
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(enchantment);
            return withEnchantments(List.of(new EnchantmentPredicate(holder, level)));
        }

        public Builder withEnchantment(Holder<Enchantment> enchantment, MinMaxBounds.Ints level) {
            return withEnchantments(List.of(new EnchantmentPredicate(enchantment, level)));
        }

        @Override
        public Condition build() {
            return new ToolMatcher(new MatchTool(Optional.of(this.itemPredicate.build())));
        }
    }
}
