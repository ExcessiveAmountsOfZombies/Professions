package com.epherical.professions.profession;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.Random;

public class ProfessionContext {

    private final Random random;
    private final Map<ProfessionParameter<?>, Object> parameters;

    ProfessionContext() {

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


    }
}
