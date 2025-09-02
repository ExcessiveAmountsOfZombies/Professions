package com.epherical.professions.core.rewards;

import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.core.register.Rewards;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ItemReward(ItemStack item) implements Reward {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(ItemReward::item)
            ).apply(i, ItemReward::new));

    @Override
    public RewardType getType() {
        return Rewards.ITEM_REWARD;
    }

    @Override
    public void giveReward(ProfessionContext context, Occupation occupation, Action actionType) {
        ServerPlayer player = context.getParameter(ProfessionParameter.THIS_PLAYER).getPlayer();
        if (player == null) {
            return;
        }
        //ItemStack toDrop = new ItemStack(this.item);
        //toDrop.setCount(this.count);
        Block.popResource(player.level(), player.getOnPos().above(), item.copy());
    }

    /*@Override
    public @NotNull Component rewardChatInfo() {
        return Component.literal("Item ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#d14f88"))).append(item.getDescription());
    }*/

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
