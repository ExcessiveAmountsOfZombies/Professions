package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
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

import java.util.ArrayList;
import java.util.List;

public class TntDestroyAction extends BlockAbstractAction {


    protected TntDestroyAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blockList) {
        super(conditions, rewards, blockList);
    }

    @Override
    public ActionType getType() {
        return Actions.TNT_DESTROY;
    }

    @Override
    public List<Action.Singular<Block>> convertToSingle(Profession profession) {
        List<Action.Singular<Block>> list = new ArrayList<>();
        for (Block realEntity : getRealBlocks()) {
            list.add(new TntDestroyAction.Single(realEntity, profession));
        }
        return list;
    }

    public static Builder tntDestroy() {
        return new Builder();
    }

    public static class Serializer extends BlockAbstractAction.Serializer<TntDestroyAction> {

        @Override
        public TntDestroyAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new TntDestroyAction(conditions, rewards, deserializeBlocks(object));
        }
    }

    public static class Builder extends BlockAbstractAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new TntDestroyAction(this.getConditions(), this.getRewards(), blocks);
        }
    }

    public class Single extends AbstractSingle<Block> {

        public Single(Block value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return TntDestroyAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return new TranslatableComponent(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return TntDestroyAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            TntDestroyAction.this.giveRewards(context, occupation);
        }
    }
}
