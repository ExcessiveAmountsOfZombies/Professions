package com.epherical.professions.model.actions.entity;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.actions.Action;
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
            SpawnEggItem spawnEggItem = SpawnEggItem.byId(et);
            return new ItemStack(spawnEggItem != null ? spawnEggItem : getIcon());
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
            if (value.left().isPresent() && entity.getType().is(value.left().get())) {
                return true;
            }
            Optional<Holder.Reference<EntityType<?>>> entityType = BuiltInRegistries.ENTITY_TYPE.getHolder(value.right().get());
            if (entityType.isPresent() && value.right().isPresent() && entity.getType().is(HolderSet.direct(entityType.get()))) {
                return true;
            }
        }

        return false;
    }
}
