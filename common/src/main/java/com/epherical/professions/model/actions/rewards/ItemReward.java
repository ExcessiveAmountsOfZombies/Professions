package com.epherical.professions.model.actions.rewards;

import com.epherical.professions.api.actions.Reward;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.bootstrap.Rewards;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.api.event.runtime.rewards.ItemRewardEvent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public record ItemReward(ItemStack item) implements Reward<ItemRewardEvent> {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(ItemReward::item)
            ).apply(i, ItemReward::new));

    @Override
    public RewardType getType() {
        return Rewards.ITEM_REWARD;
    }

    @Override
    public ItemStack getRewardIcon() {
        return item;
    }

    @Override
    public Component getRewardName() {
        return item.getHoverName();
    }

    @Override
    public void giveReward(ItemRewardEvent reward) {
        ServerPlayer player = (ServerPlayer) reward.getContext().getParameter(ProfessionParameter.THIS_PLAYER).getPlayer();
        if (player == null) return;
        Block.popResource(player.level(), player.getOnPos().above(), reward.getNewReward());
    }

    @Override
    public ItemRewardEvent buildEvent(Occupation occupation, Action<?> action, ProfessionContext professionContext) {
        return new ItemRewardEvent(occupation, action, professionContext, item.copy());
    }

    public static class Builder implements Reward.Builder {
        private ItemStack item;

        public Builder item(ItemStack item) {
            this.item = item;
            return this;
        }

        @Override
        public Reward build() {
            return new ItemReward(item);
        }
    }
}
