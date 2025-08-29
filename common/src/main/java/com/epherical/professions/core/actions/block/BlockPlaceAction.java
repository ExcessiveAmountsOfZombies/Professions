package com.epherical.professions.core.actions.block;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.AbstractAction;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.core.register.Actions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

public class BlockPlaceAction extends AbstractAction {

    public static final MapCodec<BlockPlaceAction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(BlockPlaceAction::buildCommon)
                    // We can add fields with this,
                    /*BlockState.CODEC.fieldOf("target_block")
                            .forGetter(BlockBreakAction::getTargetBlock)*/
            ).apply(i, BlockPlaceAction::new));


    public BlockPlaceAction(Common common) {
        super(common);
    }

    @Override
    public void handleAction(ProfessionContext context, Occupation occupation) {}

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }


    @Override
    public ActionType getType() {
        return Actions.BLOCK_PLACE;
    }

    @Override
    public boolean test(ProfessionContext context) {
        return context.getPossibleParameter(ProfessionParameter.THIS_BLOCK) != null;
    }

    public static class Builder extends AbstractAction.Builder<Builder> {

        public Builder(Holder<Profession> profession) {
            super(profession);
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new BlockPlaceAction(new Common(getProfession(), getConditions(), getRewards()));
        }
    }
}
