package com.epherical.professions.model.gating;

import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

public class LootDropGate extends Gate<Item> {

    // todo; evaluate this one much further, we might have 3 possible values
    // Item
    // Block
    // Entity
    // We could even deny chest loot from being generated if they aren't the proper level lol

    public static final MapCodec<LootDropGate> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(LootDropGate::buildCommon),
                    Gate.tagOrElementListCodec(Registries.ITEM).fieldOf("items").forGetter(LootDropGate::getValues)
            ).apply(i, LootDropGate::new)
    );

    public LootDropGate(Common common, List<Either<TagKey<Item>, ResourceKey<Item>>> values) {
        super(common, values);
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public GateType getGateType() {
        return Gates.LOOT_DROP;
    }

    @Override
    public ResourceKey<? extends Registry<Item>> getRegistryKey() {
        return Registries.ITEM;
    }

    @Override
    public boolean test(ProfessionContext context) {
        return false;
    }

    public static class Builder extends Gate.Builder<Builder, Item> {
        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Gate<Item> build() {
            return new LootDropGate(new Common(getProfession(), getRequirements()), getTargets());
        }
    }
}
