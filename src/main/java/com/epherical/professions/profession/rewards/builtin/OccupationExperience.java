package com.epherical.professions.profession.rewards.builtin;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;


public record OccupationExperience(double expAmount) implements Reward {

    @Override
    public RewardType getType() {
        return Rewards.EXPERIENCE_REWARD;
    }

    @Override
    public void giveReward(ProfessionContext context, Action action, Occupation occupation) {
        // if true, player levels up.
        ProfessionalPlayer player = context.getParameter(ProfessionParameter.THIS_PLAYER);
        if (occupation.addExp(action.modifyReward(context, this, expAmount), player)) {
            PlayerManager manager = ProfessionsMod.getInstance().getPlayerManager();
            manager.levelUp(player, occupation);
        }
    }

    @Override
    public @NotNull Component rewardChatInfo() {
        return new TextComponent(String.format("%.2fxp", expAmount)).setStyle(Style.EMPTY.withColor(ProfessionConfig.experience));
    }

    public static class RewardSerializer implements Serializer<OccupationExperience> {

        @Override
        public void serialize(JsonObject json, OccupationExperience value, @NotNull JsonSerializationContext serializationContext) {
            json.addProperty("amount", value.expAmount);
        }

        @Override
        public OccupationExperience deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext serializationContext) {
            double expAmount = GsonHelper.getAsDouble(json, "amount");
            return new OccupationExperience(expAmount);
        }
    }
}
