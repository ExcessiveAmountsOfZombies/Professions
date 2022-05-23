package com.epherical.professions.profession.rewards.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.NotNull;

public record ItemReward(Item item, int count) implements Reward {

    @Override
    public RewardType getType() {
        return Rewards.ITEM_REWARD.get();
    }

    @Override
    public void giveReward(ProfessionContext context, Action action, Occupation occupation) {
        ServerPlayer player = context.getParameter(ProfessionParameter.THIS_PLAYER).getPlayer();
        if (player == null) {
            return;
        }
        ItemStack toDrop = new ItemStack(this.item);
        toDrop.setCount(this.count);
        Block.popResource(player.getLevel(), player.getOnPos(), toDrop);
    }

    @Override
    public @NotNull Component rewardChatInfo() {
        return new TextComponent("Item ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#d14f88"))).append(item.getDescription());
    }

    public static class RewardSerializer implements Serializer<ItemReward> {

        @Override
        public void serialize(JsonObject json, ItemReward value, JsonSerializationContext serializationContext) {
            json.addProperty("item", Registry.ITEM.getKey(value.item).toString());
            json.addProperty("count", Registry.ITEM.getKey(value.item).toString());
        }

        @Override
        public ItemReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            Item item = GsonHelper.getAsItem(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            return new ItemReward(item, count);
        }
    }

    public static class Builder implements Reward.Builder {
        private Item item;
        private int count;

        public Builder item(Item item, int count) {
            this.item = item;
            this.count = count;
            return this;
        }

        @Override
        public Reward build() {
            return new ItemReward(item, count);
        }
    }
}
