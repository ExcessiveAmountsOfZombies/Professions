package com.epherical.professions.core.actions.block;

import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class AbstractBlockAction extends Action<Block> {

    protected AbstractBlockAction(Common common, List<Either<TagKey<Block>, ResourceKey<Block>>> targets) {
        super(common, targets);
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState blockState = context.getPossibleParameter(ProfessionParameter.THIS_BLOCK);
        if (blockState == null) {
            return false;
        }

        for (Either<TagKey<Block>, ResourceKey<Block>> value : getValues()) {
            boolean result = value.mapBoth(
                    blockTagKey -> blockState.getBlockHolder().is(blockTagKey),
                    blockResourceKey -> blockState.getBlockHolder().is(blockResourceKey)
            ).orThrow();

            if (result) {
                return true;
            }
        }

        return false;
    }
}
