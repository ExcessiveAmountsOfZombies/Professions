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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BlockBreakGate extends Gate<Block> {

    public static final MapCodec<BlockBreakGate> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(BlockBreakGate::buildCommon),
                    Gate.tagOrElementListCodec(Registries.BLOCK).fieldOf("blocks").forGetter(BlockBreakGate::getValues)
            ).apply(i, BlockBreakGate::new)
    );

    public BlockBreakGate(Common common, List<Either<TagKey<Block>, ResourceKey<Block>>> values) {
        super(common, values);
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public GateType getGateType() {
        return Gates.BLOCK_BREAK;
    }

    @Override
    public ResourceKey<? extends Registry<Block>> getRegistryKey() {
        return Registries.BLOCK;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState blockState = context.getPossibleParameter(ProfessionParameter.THIS_BLOCK);
        if (blockState == null) {
            return false;
        }

        for (Either<TagKey<Block>, ResourceKey<Block>> value : getValues()) {
            if (value.left().isPresent() && blockState.getBlockHolder().is(value.left().get())) {
                return true;
            }
            if (value.right().isPresent() && blockState.getBlockHolder().is(value.right().get())) {
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
            return new BlockBreakGate(new Common(getProfession(), getRequirements()), getTargets());
        }
    }
}
