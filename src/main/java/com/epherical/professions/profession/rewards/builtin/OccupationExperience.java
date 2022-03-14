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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;


public record OccupationExperience(double expAmount) implements Reward {

    @Override
    public RewardType getType() {
        return Rewards.EXPERIENCE_REWARD;
    }

    @Override
    public void giveReward(ProfessionContext context, Action action, Occupation occupation) {
        occupation.addExp(expAmount, context.getParameter(ProfessionParameter.THIS_PLAYER));
    }

    public static class RewardSerializer implements Serializer<OccupationExperience> {

        @Override
        public void serialize(JsonObject json, OccupationExperience value, JsonSerializationContext serializationContext) {
            json.addProperty("amount", value.expAmount);
        }

        @Override
        public OccupationExperience deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            double expAmount = GsonHelper.getAsDouble(json, "amount");
            return new OccupationExperience(expAmount);
        }
    }
}
