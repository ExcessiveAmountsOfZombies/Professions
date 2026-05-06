package com.epherical.professions.model.actions.entity;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.api.actions.Action;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.List;
import java.util.Optional;

public abstract class AbstractEntityAction extends Action<EntityType<?>> {

    protected AbstractEntityAction(Common common, List<Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>>> targets) {
        super(common, targets);

    }

    @Override
    public ResourceKey<? extends Registry<EntityType<?>>> getRegistryKey() {
        return Registries.ENTITY_TYPE;
    }


    @Override
    public ItemStack getIconStack(Holder<?> holder) {
        if (holder.value() instanceof EntityType<?> et) {
            Optional<Holder<Item>> spawnEggItem = SpawnEggItem.byId(et);
            return new ItemStack(spawnEggItem.map(Holder::value).orElseGet(this::getIcon), 1);
        }
        return super.getIconStack(holder);
    }

    @Override
    public boolean test(ProfessionContext context) {
        Entity entity = context.getPossibleParameter(ProfessionParameter.ENTITY);
        if (entity == null) {
            return false;
        }

        for (Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> value : getValues()) {
            if (value.left().isPresent() && entity.is(value.left().get())) {
                return true;
            }
            Optional<Holder.Reference<EntityType<?>>> entityType = BuiltInRegistries.ENTITY_TYPE.get(value.right().get());
            if (entityType.isPresent() && value.right().isPresent() && entity.is(HolderSet.direct(entityType.get()))) {
                return true;
            }
        }

        return false;
    }
}
