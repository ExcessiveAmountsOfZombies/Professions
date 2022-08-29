package com.epherical.professions.profession.modifiers.perks.builtin;

import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ScalingAttributePerk extends AbstractAttributePerk {


    public ScalingAttributePerk(double increaseBy, int level, Attribute attribute) {
        super(increaseBy, level, attribute, "ScalingProfessionsAttrPerk");
    }

    @Override
    public PerkType getType() {
        return Perks.SCALING_ATTRIBUTE_PERK;
    }

    @Override
    public void applyPerkToPlayer(ServerPlayer player, Occupation occupation) {
        AttributeInstance instance = player.getAttributes().getInstance(attribute);
        if (instance != null) {
            // it doesn't seem like this bit of code ever gets used, it seems to always be null.
            AttributeModifier modifier = instance.getModifier(OUR_UUID);
            double base = 0;
            if (modifier != null) {
                base = modifier.getAmount();
            }
            double amount = (increaseBy * occupation.getLevel() - level) + base;
            if (amount != 0) {
                // scales bad
                instance.addTransientModifier(new AttributeModifier(OUR_UUID, name, amount, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    public static class PerkSerializer extends AttributeSerializer<ScalingAttributePerk> {

        @Override
        public ScalingAttributePerk deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, double increaseBy, int level, Attribute attribute) {
            return new ScalingAttributePerk(increaseBy, level, attribute);
        }
    }
}
