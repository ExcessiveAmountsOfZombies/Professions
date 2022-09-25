package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PlaceBlockAction extends BlockAbstractAction {


    protected PlaceBlockAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blocks) {
        super(conditions, rewards, blocks);
    }

    @Override
    public ActionType getType() {
        return Actions.PLACE_BLOCK;
    }

    @Override
    public List<Action.Singular<Block>> convertToSingle(Profession profession) {
        List<Action.Singular<Block>> list = new ArrayList<>();
        for (Block realEntity : getRealBlocks()) {
            list.add(new PlaceBlockAction.Single(realEntity, profession));
        }
        return list;
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

    public static class Serializer extends BlockAbstractAction.Serializer<PlaceBlockAction> {

        @Override
        public PlaceBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new PlaceBlockAction(conditions, rewards, deserializeBlocks(object));
        }
    }

    public static class Builder extends BlockAbstractAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new PlaceBlockAction(this.getConditions(), this.getRewards(), blocks);
        }
    }

    public class Single extends AbstractSingle<Block> {

        public Single(Block value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return PlaceBlockAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return new TranslatableComponent(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return PlaceBlockAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            PlaceBlockAction.this.giveRewards(context, occupation);
        }
    }
}
