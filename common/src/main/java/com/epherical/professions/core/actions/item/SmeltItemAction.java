package com.epherical.professions.core.actions.item;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.register.Actions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

public class SmeltItemAction extends AbstractItemAction {

    public static final MapCodec<SmeltItemAction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(SmeltItemAction::buildCommon),
                    Action.tagOrElementListCodec(Registries.ITEM).fieldOf("items").forGetter(SmeltItemAction::getValues)
            ).apply(i, SmeltItemAction::new));


    public SmeltItemAction(Common common, List<Either<TagKey<Item>, ResourceKey<Item>>> targets) {
        super(common, targets);
    }


    @Override
    public Common buildCommon() {
        return Common.build(this);
    }


    @Override
    public ActionType getType() {
        return Actions.SMELT_ITEM_ACTION;
    }

    public static class Builder extends Action.Builder<Builder, Item> {

        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action<Item> build() {
            return new SmeltItemAction(new Common(getProfession(), getConditions(), getRewards()), getTargets());
        }
    }
}
