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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

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
    public void handleAction(ProfessionContext context, Occupation occupation) {}

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
            return new BlockBreakAction(new Common(getProfession(), getConditions(), getRewards()));
        }
    }
}
