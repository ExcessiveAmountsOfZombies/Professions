package com.epherical.professions.profession.modifiers.milestones.builtin;

import com.epherical.professions.profession.modifiers.milestones.Milestone;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;

public abstract class AbstractLevelMilestone implements Milestone {

    protected final int level;

    public AbstractLevelMilestone(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }


    public static abstract class AbstractLevelMilestoneSerializer<T extends AbstractLevelMilestone> implements Serializer<T> {


        @Override
        public void serialize(JsonObject jsonObject, T t, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("level", t.level);
        }

        @Override
        public T deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int level = GsonHelper.getAsInt(jsonObject, "level");
            return deserialize(jsonObject, jsonDeserializationContext, level);
        }

        public abstract T deserialize(JsonObject object, JsonDeserializationContext context, int level);
    }
}
