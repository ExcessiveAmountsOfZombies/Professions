package com.epherical.professions.profession.rewards.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.NotNull;

public record ItemReward(Item item) implements Reward {
    // TODO: add count.

    @Override
    public RewardType getType() {
        return Rewards.ITEM_REWARD;
    }

    @Override
    public void giveReward(ProfessionContext context, Action action, Occupation occupation) {

    }

    @Override
    public @NotNull
    Component rewardChatInfo() {
        return new TextComponent("A ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#d14f88"))).append(item.getDescription());
    }

    public static class RewardSerializer implements Serializer<ItemReward> {

        @Override
        public void serialize(JsonObject json, ItemReward value, JsonSerializationContext serializationContext) {
            json.addProperty("item", Registry.ITEM.getKey(value.item).toString());
        }

        @Override
        public ItemReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            Item item = GsonHelper.getAsItem(json, "item");
            return new ItemReward(item);
        }
    }
}
