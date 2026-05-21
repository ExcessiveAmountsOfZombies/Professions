package com.epherical.professions.core.context;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.gating.GateType;
import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public record ProfessionContext(Level level, RandomSource randomSource,
                                Map<ProfessionParameter<?>, Object> parameters) {

    public boolean hasParameter(ProfessionParameter<?> parameter) {
        return parameters.containsKey(parameter);
    }

    public <T> T getParameter(ProfessionParameter<T> parameter) throws NoSuchElementException {
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

    public boolean isClientSide() {
        return level().isClientSide();
    }


    @Override
    @NotNull
    public String toString() {
        return "ProfessionContext{" +
                "level=" + level +
                ", randomSource=" + randomSource +
                ", parameters=" + parameters +
                '}';
    }

    public static Builder builder(Level level, ActionType actionType, IProfessionalPlayer player) {
        return new Builder(level)
                .addRandom(level.getRandom())
                .addParameter(ProfessionParameter.ACTION_TYPE, actionType)
                .addParameter(ProfessionParameter.THIS_PLAYER, player);
    }

    public static Builder gateBuilder(Level level, GateType gateType, IProfessionalPlayer player) {
        return new Builder(level)
                .addRandom(level.getRandom())
                .addParameter(ProfessionParameter.GATE_TYPE, gateType)
                .addParameter(ProfessionParameter.THIS_PLAYER, player);
    }

    public static class Builder {
        private final Level level;
        private final Map<ProfessionParameter<?>, Object> parameters = Maps.newIdentityHashMap();
        private RandomSource random;

        public Builder(Level level) {
            this.level = level;
            this.random = level.getRandom();
        }

        public Builder addRandom(RandomSource random) {
            this.random = random;
            return this;
        }

        public <T> Builder addParameter(ProfessionParameter<T> parameter, T value) {
            this.parameters.put(parameter, value);
            return this;
        }

        public Level getLevel() {
            return level;
        }

        public ProfessionContext build() {
            return new ProfessionContext(level, random, parameters);
        }


    }
}
