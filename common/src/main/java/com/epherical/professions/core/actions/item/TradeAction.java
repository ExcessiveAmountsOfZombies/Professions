package com.epherical.professions.core.actions.item;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.AbstractAction;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.core.register.Actions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

public class TradeAction extends AbstractAction {

    public static final MapCodec<TradeAction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(TradeAction::buildCommon)
            ).apply(i, TradeAction::new));


    public TradeAction(Common common) {
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
        return Actions.TRADE_ACTION;
    }

    @Override
    public boolean test(ProfessionContext context) {
        return true;
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
            return new TradeAction(new Common(getProfession(), getConditions(), getRewards()));
        }
    }
}
