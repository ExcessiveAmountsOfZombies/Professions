package com.epherical.professions.model.actions.entity;

import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.ActionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.List;

public class BreedAction extends AbstractEntityAction {

    public static final MapCodec<BreedAction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(BreedAction::buildCommon),
                    Action.tagOrElementListCodec(Registries.ENTITY_TYPE).fieldOf("entities").forGetter(BreedAction::getValues)
            ).apply(i, BreedAction::new));

    protected BreedAction(Common common, List<Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>>> targets) {
        super(common, targets);

    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public ActionType getType() {
        return Actions.BREED_ACTION;
    }

    @Override
    public Item getIcon() {
        return super.getIcon();
    }

    public static class Builder extends Action.Builder<Builder, EntityType<?>> {

        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action<EntityType<?>> build() {
            return new BreedAction(new Common(getProfession(), getConditions(), getRewards()), getTargets());
        }
    }
}
