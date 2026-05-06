package com.epherical.professions.model.actions.item;

import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.model.actions.ActionType;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.List;

public class EnchantAction extends AbstractItemAction {

    public static final MapCodec<EnchantAction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(EnchantAction::buildCommon),
                    Action.tagOrElementListCodec(Registries.ITEM).fieldOf("items").forGetter(EnchantAction::getValues),
                    Action.tagOrElementListCodec(Registries.ENCHANTMENT).fieldOf("enchantments").forGetter(EnchantAction::getEnchantments)
            ).apply(i, EnchantAction::new));

    private final List<Either<TagKey<Enchantment>, ResourceKey<Enchantment>>> enchantments;

    public EnchantAction(Common common, List<Either<TagKey<Item>, ResourceKey<Item>>> targets,
                         List<Either<TagKey<Enchantment>, ResourceKey<Enchantment>>> enchantments) {
        super(common, targets);
        this.enchantments = enchantments;
    }


    @Override
    public boolean test(ProfessionContext context) {
        if (context.hasParameter(ProfessionParameter.ITEM_INVOLVED)) {
            // if it has an item involved, it's just item checking.
            return super.test(context);
        } else {
            EnchantmentInstance instance = context.getPossibleParameter(ProfessionParameter.ENCHANTMENT_INSTANCE);
            if (instance == null) {
                return false;
            }

            for (Either<TagKey<Enchantment>, ResourceKey<Enchantment>> value : getEnchantments()) {
                if (value.left().isPresent() && instance.enchantment().is(value.left().get())) {
                    return true;
                }
                if (value.right().isPresent() && instance.enchantment().is(value.right().get())) {
                    return true;
                }
            }
            return false;
        }
    }

    public List<Either<TagKey<Enchantment>, ResourceKey<Enchantment>>> getEnchantments() {
        return enchantments;
    }

    @Override
    public void getExtraValues(Multimap<Holder<?>, Action<?>> valueToActionsMap, Multimap<Action<?>, Holder<?>> actionToValueMap, HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Enchantment> lookup = provider.lookupOrThrow(Registries.ENCHANTMENT);
        for (Either<TagKey<Enchantment>, ResourceKey<Enchantment>> value : enchantments) {
            if (value.left().isPresent()) {
                TagKey<Enchantment> tagKey = value.left().get();
                for (Holder<Enchantment> holder : lookup.getOrThrow(tagKey)) {
                    valueToActionsMap.put(holder, this);
                    actionToValueMap.put(this, holder);
                }
            } else {
                ResourceKey<Enchantment> resourceKey = value.right().orElseThrow();
                Holder.Reference<Enchantment> holder = lookup.getOrThrow(resourceKey);
                valueToActionsMap.put(holder, this);
                actionToValueMap.put(this, holder);
            }
        }
    }

    private void indexHolder(Holder<?> holder, Multimap<Holder<?>, Action<?>> valueToActionsMap, Multimap<Action<?>, Holder<?>> actionToValueMap) {
        valueToActionsMap.put(holder, this);
        actionToValueMap.put(this, holder);
    }


    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public Item getIcon() {
        return Items.ENCHANTED_BOOK;
    }

    @Override
    public ItemStack getIconStack(Holder<?> holder) {
        if (holder.value() instanceof Enchantment) {
            ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
            MutableComponent comp = (MutableComponent) Enchantment.getFullname((Holder<Enchantment>) holder, 10);
            comp.setStyle(Style.EMPTY);
            stack.set(DataComponents.CUSTOM_NAME, comp);
            return stack;
        } else {
            return super.getIconStack(holder);
        }

    }

    @Override
    public ActionType getType() {
        return Actions.ENCHANT_ACTION;
    }

    public static class Builder extends Action.Builder<Builder, Item> {
        private final List<Either<TagKey<Enchantment>, ResourceKey<Enchantment>>> enchantments = new ArrayList<>();

        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        public Builder enchantment(Either<TagKey<Enchantment>, ResourceKey<Enchantment>> enchantment) {
            this.enchantments.add(enchantment);
            return this;
        }

        public Builder enchantment(TagKey<Enchantment> enchantment) {
            this.enchantments.add(Either.left(enchantment));
            return this;
        }

        public Builder enchantment(ResourceKey<Enchantment> enchantment) {
            this.enchantments.add(Either.right(enchantment));
            return this;
        }

        public List<Either<TagKey<Enchantment>, ResourceKey<Enchantment>>> getEnchantments() {
            return enchantments;
        }

        @Override
        public Action<Item> build() {
            return new EnchantAction(new Common(getProfession(), getConditions(), getRewards()), getTargets(), getEnchantments());
        }
    }
}
