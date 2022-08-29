package com.epherical.professions.profession.modifiers;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.modifiers.milestones.Milestone;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class BasicModifiers implements Modifiers {

    private final Milestone[] milestones;
    private final Perk[] perks;

    public BasicModifiers(Milestone[] milestones, Perk[] perks) {
        this.milestones = milestones;
        this.perks = perks;
    }

    public Milestone[] getMilestones() {
        return milestones;
    }

    public Perk[] getPerks() {
        return perks;
    }

    @Override
    public void handleLevelUp(ProfessionalPlayer player, Occupation occupation) {
        int lastTimePlayerReceivedBenefits = occupation.getReceivedBenefitsUpToLevel();
        int playersCurrentLevel = occupation.getLevel();
        for (Milestone milestone : getMilestones()) {
            int receiveMilestoneAt = milestone.getLevel();
            if (receiveMilestoneAt >= lastTimePlayerReceivedBenefits && playersCurrentLevel >= receiveMilestoneAt) {
                milestone.giveMilestoneReward(player, occupation);
            }
        }
        for (Perk allPerk : occupation.getData().allPerks()) {
            allPerk.removeOldPerkData(player.getPlayer(), occupation);
            if (allPerk.canApplyPerkToPlayer("", player, occupation)) {
                if (player.getPlayer() != null) {
                    allPerk.applyPerkToPlayer(player.getPlayer(), occupation);
                }
            }
        }

        occupation.setReceivedBenefitsUpToLevel(occupation.getLevel());
    }

    public static class ModifierSerializer implements JsonSerializer<BasicModifiers>, JsonDeserializer<BasicModifiers> {

        @Override
        public BasicModifiers deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = GsonHelper.convertToJsonObject(json, "milestone object");
            Milestone[] milestones = GsonHelper.getAsObject(object, "milestones", new Milestone[0], context, Milestone[].class);
            Perk[] perks = GsonHelper.getAsObject(object, "perks", new Perk[0], context, Perk[].class);
            return new BasicModifiers(milestones, perks);
        }

        @Override
        public JsonElement serialize(BasicModifiers src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            JsonArray milestoneArray = new JsonArray();
            for (Milestone milestone : src.getMilestones()) {
                milestoneArray.add(context.serialize(milestone));
            }
            object.add("milestones", milestoneArray);
            JsonArray perkArray = new JsonArray();
            for (Perk perk : src.getPerks()) {
                perkArray.add(context.serialize(perk));
            }
            object.add("perks", perkArray);
            return object;
        }
    }
}
