package com.epherical.professions.model.actions.rewards;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.bootstrap.Rewards;
import com.epherical.professions.domain.exception.ProfessionNotActiveException;
import com.epherical.professions.api.event.runtime.rewards.OccupationExperienceEvent;
import com.epherical.professions.api.event.runtime.rewards.OccupationLevelEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record OccupationExperience(double expAmount) implements Reward<OccupationExperienceEvent> {

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
    public void giveReward(OccupationExperienceEvent reward) {
        IProfessionalPlayer player = reward.getContext().getParameter(ProfessionParameter.THIS_PLAYER);
        Occupation occupation = reward.getOccupation();
        int currentLevel = occupation.getLevel();
        try {
            if (occupation.addExp(reward.getNewAmount(), player)) {
                ProfessionsCommon.INSTANCE.getEventBus().post(new OccupationLevelEvent(occupation, currentLevel, occupation.getLevel(), player));
            }
        } catch (ProfessionNotActiveException ignored) {
            ProfessionsCommon.LOG.error("Profession wasn't active yet tried to add EXP to it. {}", occupation.getProfession());
        }
    }

    @Override
    public OccupationExperienceEvent buildEvent(Occupation occupation, Action<?> action, ProfessionContext professionContext) {
        return new OccupationExperienceEvent(professionContext, action, occupation, expAmount);
    }

    public static class Builder implements Reward.Builder {
        private double exp;

        public Builder exp(double exp) {
            this.exp = exp;
            return this;
        }

        @Override
        public Reward<?> build() {
            return new OccupationExperience(exp);
        }
    }
}
