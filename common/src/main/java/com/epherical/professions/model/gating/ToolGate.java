package com.epherical.professions.model.gating;

import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ToolGate extends Gate<Item> {

    public static final MapCodec<ToolGate> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(ToolGate::buildCommon),
                    Gate.tagOrElementListCodec(Registries.ITEM).fieldOf("items").forGetter(ToolGate::getValues)
            ).apply(i, ToolGate::new)
    );

    public ToolGate(Common common, List<Either<TagKey<Item>, ResourceKey<Item>>> values) {
        super(common, values);
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public GateType getGateType() {
        return Gates.TOOL;
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
            return new ToolGate(new Common(getProfession(), getRequirements()), getTargets());
        }
    }
}
