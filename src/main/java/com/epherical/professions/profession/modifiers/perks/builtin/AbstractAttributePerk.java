package com.epherical.professions.profession.modifiers.perks.builtin;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.UUID;

public abstract class AbstractAttributePerk implements Perk {

    protected static final Logger LOGGER = LogUtils.getLogger();

    protected final UUID OUR_UUID = UUID.randomUUID();

    protected final int level;
    protected final double increaseBy;
    protected final Attribute attribute;
    protected final String name;

    public AbstractAttributePerk(double increaseBy, int level, Attribute attribute, String name) {
        this.increaseBy = increaseBy;
        this.level = level;
        this.attribute = attribute;
        this.name = name;
    }


    @Override
    public boolean canApplyPerkToPlayer(String permission, ProfessionalPlayer context, Occupation occupation) {
        return occupation.getLevel() >= level;
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
            instance.addTransientModifier(new AttributeModifier(OUR_UUID, name, increaseBy + base, AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void removeOldPerkData(ServerPlayer player, Occupation occupation) {
        AttributeInstance instance = player.getAttributes().getInstance(attribute);
        if (instance != null) {
            instance.removeModifier(OUR_UUID);
        }
    }

    @Override
    public int getLevel() {
        return level;
    }

    public abstract static class AttributeSerializer<T extends AbstractAttributePerk> implements Serializer<T> {

        @Override
        public void serialize(@NotNull JsonObject jsonObject, @NotNull T t, @NotNull JsonSerializationContext jsonSerializationContext) {
            try {
                jsonObject.addProperty("attribute", Registry.ATTRIBUTE.getKey(t.attribute).toString());
            } catch (NullPointerException e) {
                LOGGER.warn("Tried serializing an attribute that does not exist on the server.");
                e.printStackTrace();
            }

            jsonObject.addProperty("increaseBy", t.increaseBy);
            jsonObject.addProperty("level", t.level);
        }

        @Override
        @NotNull
        public T deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext) {
            double increaseBy = GsonHelper.getAsDouble(jsonObject, "increaseBy");
            int level = GsonHelper.getAsInt(jsonObject, "level");
            Attribute attribute = Registry.ATTRIBUTE.get(new ResourceLocation(GsonHelper.getAsString(jsonObject, "attribute")));
            if (attribute == null) {
                LOGGER.warn("Tried to deserialize an attribute that was null");
                throw new RuntimeException("Tried to deserialize an attribute that was null");
            }
            return deserialize(jsonObject, jsonDeserializationContext, increaseBy, level, attribute);
        }

        public abstract T deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, double increaseBy, int level, Attribute attribute);
    }

}
