package com.epherical.professions.core.actions.item;

import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.mojang.datafixers.util.Either;
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
    public boolean test(ProfessionContext context) {
        ItemStack itemStack = context.getPossibleParameter(ProfessionParameter.ITEM_INVOLVED);
        if (itemStack == null) {
            return false;
        }

        for (Either<TagKey<Item>, ResourceKey<Item>> value : getValues()) {
            boolean result = value.mapBoth(
                    itemTagKey -> itemStack.getItemHolder().is(itemTagKey),
                    itemResourceKey -> itemStack.getItemHolder().is(itemResourceKey)
            ).orThrow();

            if (result) {
                return true;
            }
        }

        return false;
    }
}
