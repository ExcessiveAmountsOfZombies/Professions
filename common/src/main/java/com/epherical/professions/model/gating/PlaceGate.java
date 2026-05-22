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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PlaceGate extends Gate<Block> {

    public static final MapCodec<PlaceGate> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(PlaceGate::buildCommon),
                    Gate.tagOrElementListCodec(Registries.BLOCK).fieldOf("blocks").forGetter(PlaceGate::getValues)
            ).apply(i, PlaceGate::new)
    );

    public PlaceGate(Common common, List<Either<TagKey<Block>, ResourceKey<Block>>> values) {
        super(common, values);
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public GateType getGateType() {
        return Gates.PLACE;
    }

    @Override
    public ResourceKey<? extends Registry<Block>> getRegistryKey() {
        return Registries.BLOCK;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState blockState = context.getPossibleParameter(ProfessionParameter.THIS_BLOCK);
        Block block = null;
        if (blockState == null) {
            ItemStack stack = context.getPossibleParameter(ProfessionParameter.ITEM_INVOLVED);
            if (stack != null && stack.getItem() instanceof BlockItem blockItem) {
                block = blockItem.getBlock();
            } else {
                return false;
            }
        } else {
            block = blockState.getBlock();
        }

        for (Either<TagKey<Block>, ResourceKey<Block>> value : getValues()) {
            if (value.left().isPresent() && block.builtInRegistryHolder().is(value.left().get())) {
                return true;
            }
            if (value.right().isPresent() && block.builtInRegistryHolder().is(value.right().get())) {
                return true;
            }
        }

        return false;
    }

    public static class Builder extends Gate.Builder<Builder, Block> {
        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Gate<Block> build() {
            return new PlaceGate(new Common(getProfession(), getRequirements()), getTargets());
        }
    }
}
