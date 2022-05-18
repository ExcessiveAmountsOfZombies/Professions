package com.epherical.professions.profession.action;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractAction implements Action {
    protected final ActionCondition[] conditions;
    protected final Reward[] rewards;
    private final Predicate<ProfessionContext> predicate;

    protected AbstractAction(ActionCondition[] conditions, Reward[] rewards) {
        this.conditions = conditions;
        this.rewards = rewards;
        this.predicate = Actions.andAllConditions(conditions);
    }

    @Override
    public final boolean handleAction(ProfessionContext context, Occupation occupation) {
        return this.predicate.test(context) && test(context) && internalCondition(context);
    }

    public abstract boolean test(ProfessionContext professionContext);

    /**
     * Any type of internal condition that must be met, this is meant as a system that is not datapackable, but could have config options.
     * @param context context to be passed
     * @return true if it passes, false otherwise.
     */
    public abstract boolean internalCondition(ProfessionContext context);

    public final void giveRewards(ProfessionContext context, Occupation occupation) {
        for (Reward reward : rewards) {
            reward.giveReward(context, this, occupation);
        }
    }

    @Override
    public double modifyReward(ProfessionContext context, Reward reward, double base) {
        return base;
    }

    public final Map<RewardType, Component> getRewardInformation() {
        Map<RewardType, Component> map = new HashMap<>();
        for (Reward reward : rewards) {
            map.put(reward.getType(), reward.rewardChatInfo());
        }
        return map;
    }

    public final Component allRewardInformation() {
        MutableComponent hoverComp = new TextComponent("");
        for (Component value : getRewardInformation().values()) {
            hoverComp.append(value);
        }
        return hoverComp;
    }

    public final Component extraRewardInformation(Map<RewardType, Component> base) {
        Style style = Style.EMPTY;
        int i = 0;
        MutableComponent hoverComponent = new TextComponent("");
        for (Map.Entry<RewardType, Component> entry : base.entrySet()) {
            if (entry.getKey().equals(Rewards.PAYMENT_REWARD) || entry.getKey().equals(Rewards.EXPERIENCE_REWARD)) {
                continue;
            }
            hoverComponent.append(entry.getValue()).append("\n");
            i++;
        }
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
        style = style.withHoverEvent(hoverEvent);
        MutableComponent mainComponent;
        if (i > 0) {
            mainComponent = new TextComponent(" & More");
            style = style.withBold(true).withUnderlined(true).withColor(ProfessionConfig.moreRewards);
            mainComponent.setStyle(style);
        } else {
            mainComponent = new TextComponent("").setStyle(Style.EMPTY.withColor(ProfessionConfig.noMoreRewards));
        }
        return mainComponent;
    }

    public abstract static class ActionSerializer<T extends AbstractAction> implements Serializer<T> {

        @Override
        public void serialize(@NotNull JsonObject json, T value, @NotNull JsonSerializationContext serializationContext) {
            json.add("conditions", serializationContext.serialize(value.conditions));
            json.add("rewards", serializationContext.serialize(value.rewards));
        }

        @Override
        public final T deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext serializationContext) {
            ActionCondition[] actionConditions = GsonHelper.getAsObject(json, "conditions", new ActionCondition[0], serializationContext, ActionCondition[].class);
            Reward[] rewards = GsonHelper.getAsObject(json, "rewards", new Reward[0], serializationContext, Reward[].class);
            return deserialize(json, serializationContext, actionConditions, rewards);
        }

        public abstract T deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards);


    }

    public abstract static class Builder<T extends Builder<T>> implements Action.Builder {
        private final List<ActionCondition> conditions = new ArrayList<>();
        private final List<Reward> rewards = new ArrayList<>();

        public T condition(ActionCondition.Builder conditionBuilder) {
            this.conditions.add(conditionBuilder.build());
            return instance();
        }

        public T reward(Reward.Builder rewardBuilder) {
            this.rewards.add(rewardBuilder.build());
            return instance();
        }

        protected abstract T instance();

        public ActionCondition[] getConditions() {
            return conditions.toArray(new ActionCondition[0]);
        }

        public Reward[] getRewards() {
            return rewards.toArray(new Reward[0]);
        }
    }
}
