package com.epherical.professions.core.actions.block;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BlockBreakAction extends AbstractBlockAction {

    public static final MapCodec<BlockBreakAction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(BlockBreakAction::buildCommon),
                    Action.tagOrElementListCodec(Registries.BLOCK).fieldOf("blocks").forGetter(BlockBreakAction::getValues)
            ).apply(i, BlockBreakAction::new));


    public BlockBreakAction(Common common, List<Either<TagKey<Block>, ResourceKey<Block>>> targets) {
        super(common, targets);
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public ActionType getType() {
        return Actions.BLOCK_BREAK;
    }

    @Override
    public Item getIcon() {
        return Items.DIAMOND_PICKAXE;
    }

    public static class Builder extends Action.Builder<Builder, Block> {

        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action<Block> build() {
            return new BlockBreakAction(new Common(getProfession(), getConditions(), getRewards()), getTargets());
        }
    }
}
