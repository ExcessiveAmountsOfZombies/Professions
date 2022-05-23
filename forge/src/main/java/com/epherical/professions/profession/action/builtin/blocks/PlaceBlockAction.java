package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PlaceBlockAction extends BlockAbstactAction {


    protected PlaceBlockAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blocks) {
        super(conditions, rewards, blocks);
    }

    @Override
    public ActionType getType() {
        return Actions.PLACE_BLOCK.get();
    }

    public static Builder place() {
        return new Builder();
    }

    @Override
    public boolean internalCondition(ProfessionContext context) {
        if (super.internalCondition(context)) {
            cache.put(context.getParameter(ProfessionParameter.BLOCKPOS), Instant.now().plus(ProfessionConfig.paymentCoolDown, ChronoUnit.SECONDS));
            return true;
        }
        return false;
    }

    public static class Serializer extends BlockAbstactAction.Serializer<PlaceBlockAction> {

        @Override
        public PlaceBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new PlaceBlockAction(conditions, rewards, deserializeBlocks(object));
        }
    }

    public static class Builder extends BlockAbstactAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new PlaceBlockAction(this.getConditions(), this.getRewards(), blocks);
        }
    }
}
