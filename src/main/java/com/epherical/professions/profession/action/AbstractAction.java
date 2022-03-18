package com.epherical.professions.profession.action;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.ChatFormatting;
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
    public final void handleRewards(ProfessionContext context, Occupation occupation) {
        if (this.predicate.test(context) && test(context)) {
            giveRewards(context, occupation);
        }
    }

    public abstract boolean test(ProfessionContext professionContext);

    public final void giveRewards(ProfessionContext context, Occupation occupation) {
        for (Reward reward : rewards) {
            reward.giveReward(context, this, occupation);
        }
    }

    public final Map<RewardType, Component> getRewardInformation() {
        Map<RewardType, Component> map = new HashMap<>();
        for (Reward reward : rewards) {
            map.put(reward.getType(), reward.rewardChatInfo());
        }
        return map;
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
            mainComponent = new TextComponent("More");
            style = style.withBold(true).withUnderlined(true).withColor(ChatFormatting.GREEN);
            mainComponent.setStyle(style);
        } else {
            mainComponent = new TextComponent("No more").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED));
        }
        return mainComponent;
    }

    public abstract static class ActionSerializer<T extends AbstractAction> implements Serializer<T> {

        @Override
        public void serialize(@NotNull JsonObject json, T value, @NotNull JsonSerializationContext serializationContext) {
            if (!ArrayUtils.isEmpty(value.conditions)) {
                json.add("conditions", serializationContext.serialize(value.conditions));
            }
            if (!ArrayUtils.isEmpty(value.rewards)) {
                json.add("rewards", serializationContext.serialize(value.rewards));
            }
        }

        @Override
        public final T deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext serializationContext) {
            ActionCondition[] actionConditions = GsonHelper.getAsObject(json, "conditions", new ActionCondition[0], serializationContext, ActionCondition[].class);
            Reward[] rewards = GsonHelper.getAsObject(json, "rewards", new Reward[0], serializationContext, Reward[].class);
            return deserialize(json, serializationContext, actionConditions, rewards);
        }

        public abstract T deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards);


    }

    public abstract static class Builder<T extends AbstractAction.Builder<T>> implements Action.Builder {
        private final List<ActionCondition> conditions = Lists.newArrayList();
        private final List<Reward> rewards = Lists.newArrayList();

        public T when(ActionCondition.Builder conditionBuilder) {
            this.conditions.add(conditionBuilder.build());
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
