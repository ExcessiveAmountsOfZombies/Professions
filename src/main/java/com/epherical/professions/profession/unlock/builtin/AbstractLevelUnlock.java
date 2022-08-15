package com.epherical.professions.profession.unlock.builtin;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.Tristate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLevelUnlock<T> implements Unlock<T> {

    protected final int level;

    public AbstractLevelUnlock(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public abstract static class JsonUnlockSerializer<V extends AbstractLevelUnlock<?>> implements Serializer<V> {

        @Override
        public void serialize(JsonObject jsonObject, V v, @NotNull JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("level", v.level);
        }

        @Override
        public @NotNull V deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext jsonDeserializationContext) {
            int level = GsonHelper.getAsInt(json, "level");
            return deserialize(json, jsonDeserializationContext, level);
        }

        public abstract V deserialize(JsonObject json, JsonDeserializationContext context, int level);
    }

    public abstract static class AbstractSingle<T> implements Singular<T> {
        protected final T value;
        protected final int level;
        protected final Profession profession;

        public AbstractSingle(T value, int level, Profession profession) {
            this.value = value;
            this.level = level;
            this.profession = profession;
        }

        public Tristate isLocked(T object, int level) {
            return (level >= this.level && value.equals(object)) ? Tristate.TRUE : Tristate.FALSE;
        }

        @Override
        public boolean canUse(ProfessionalPlayer player) {
            Occupation occupation = player.getOccupation(getProfession());
            return this.isLocked(getObject(), occupation.getLevel()).valid();
        }

        @Override
        public T getObject() {
            return value;
        }

        @Override
        public Profession getProfession() {
            return profession;
        }

        @Override
        public Component getProfessionDisplay() {
            return profession.getDisplayComponent();
        }
    }
}
