package com.epherical.professions.core.actions.block;

import com.epherical.professions.core.actions.AbstractAction;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.register.Actions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BlockBreakAction extends AbstractAction {

    public static final MapCodec<BlockBreakAction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(BlockBreakAction::buildCommon)
                    // We can add fields with this,
                    /*BlockState.CODEC.fieldOf("target_block")
                            .forGetter(BlockBreakAction::getTargetBlock)*/
            ).apply(i, BlockBreakAction::new));


    public BlockBreakAction(Common common) {
        super(common);
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }


    @Override
    public ActionType getType() {
        return Actions.BLOCK_BREAK;
    }
}
