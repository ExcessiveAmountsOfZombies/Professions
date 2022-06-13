package com.epherical.professions.profession;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

public record ProfessionContext(ServerLevel level, Random random, Map<ProfessionParameter<?>, Object> parameters) {

    public boolean hasParameter(ProfessionParameter<?> parameter) {
        return this.parameters.containsKey(parameter);
    }

    public <T> T getParameter(ProfessionParameter<T> parameter) {
        T value = (T) this.parameters.get(parameter);
        if (value == null) {
            throw new NoSuchElementException(parameter.name().toString());
        } else {
            return value;
        }
    }

    @Nullable
    public <T> T getPossibleParameter(ProfessionParameter<T> parameter) {
        return (T) this.parameters.get(parameter);
    }

    public Random random() {
        return random;
    }

    public ServerLevel level() {
        return level;
    }

    @Override
    public String toString() {
        return "ProfessionContext{" +
                "level=" + level +
                ", random=" + random +
                ", parameters=" + parameters +
                '}';
    }

    public static class Builder {
        private final ServerLevel level;
        private final Map<ProfessionParameter<?>, Object> parameters = Maps.newIdentityHashMap();
        private Random random;

        public Builder(ServerLevel level) {
            this.level = level;
        }

        public Builder addRandom(Random random) {
            this.random = random;
            return this;
        }

        public <T> Builder addParameter(ProfessionParameter<T> parameter, T value) {
            this.parameters.put(parameter, value);
            return this;
        }

        public ServerLevel getLevel() {
            return level;
        }

        public ProfessionContext build() {
            return new ProfessionContext(level, random, parameters);
        }


    }
}
