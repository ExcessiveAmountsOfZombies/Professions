package com.epherical.professions.core.rewards;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.core.register.Rewards;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record OccupationExperience(double expAmount) implements Reward {

    private static final ItemStack REWARD_ICON = new ItemStack(Items.EXPERIENCE_BOTTLE);

    public static final MapCodec<OccupationExperience> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Codec.DOUBLE.fieldOf("expAmount").forGetter(OccupationExperience::expAmount)
            ).apply(i, OccupationExperience::new));

    @Override
    public RewardType getType() {
        return Rewards.OCCUPATION_EXPERIENCE;
    }

    @Override
    public ItemStack getRewardIcon() {
        return REWARD_ICON;
    }

    @Override
    public Component getRewardName() {
        return Component.literal(String.format("%.2f", expAmount) + "oxp").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
    }

    @Override
    public void giveReward(ProfessionContext context, Occupation occupation, Action actionType) {
        // if true, player levels up.
        IProfessionalPlayer player = context.getParameter(ProfessionParameter.THIS_PLAYER);
        //context.getParameter(ProfessionParameter.ACTION_LOGGER).addExpReward(rewardChatInfo(), expAmount, occupation);
        int currentLevel = occupation.getLevel();
        if (occupation.addExp(expAmount, player)) {
            System.out.println("oh yea buddy we did a thing");
           // PlayerManager manager = ProfessionPlatform.platform.getPlayerManager();
           // manager.levelUp(player, occupation, currentLevel);
        }
    }

    public static class Builder implements Reward.Builder {
        private double exp;

        public Builder exp(double exp) {
            this.exp = exp;
            return this;
        }

        @Override
        public Reward build() {
            return new OccupationExperience(exp);
        }
    }
}
