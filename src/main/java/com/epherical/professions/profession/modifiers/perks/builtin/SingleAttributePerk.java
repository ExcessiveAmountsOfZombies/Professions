package com.epherical.professions.profession.modifiers.perks.builtin;

import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class SingleAttributePerk extends AbstractAttributePerk {


    public SingleAttributePerk(double increaseBy, int level, Attribute attribute) {
        super(increaseBy, level, attribute, "SingleProfessionsAttrPerk");
    }

    @Override
    public PerkType getType() {
        return Perks.SINGLE_ATTRIBUTE_PERK;
    }

    public static Builder single() {
        return new Builder();
    }

    public static class Builder extends AbstractAttributePerk.Builder<Builder> {

        @Override
        public Perk build() {
            return new SingleAttributePerk(getIncreaseBy(), getLevel(), getAttribute());
        }

        @Override
        protected Builder instance() {
            return this;
        }
    }


    public static class PerkSerializer extends AttributeSerializer<SingleAttributePerk> {

        @Override
        public SingleAttributePerk deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, double increaseBy, int level, Attribute attribute) {
            return new SingleAttributePerk(increaseBy, level, attribute);
        }
    }
}
