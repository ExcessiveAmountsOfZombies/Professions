package com.epherical.professions.model.actions.item;

import com.epherical.professions.api.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractItemAction extends Action<Item> {

    protected AbstractItemAction(Common common, List<Either<TagKey<Item>, ResourceKey<Item>>> targets) {
        super(common, targets);
    }

    @Override
    public ResourceKey<? extends Registry<Item>> getRegistryKey() {
        return Registries.ITEM;
    }

    @Override
    public boolean test(ProfessionContext context) {
        ItemStack itemStack = context.getPossibleParameter(ProfessionParameter.ITEM_INVOLVED);
        if (itemStack == null) {
            return false;
        }

        for (Either<TagKey<Item>, ResourceKey<Item>> value : getValues()) {
            if (value.left().isPresent() && itemStack.getItemHolder().is(value.left().get())) {
                return true;
            }
            if (value.right().isPresent() && itemStack.getItemHolder().is(value.right().get())) {
                return true;
            }
        }

        return false;
    }
}
