package com.epherical.professions.util;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public interface StatePropertiesPredicateHelper {

    <S extends StateHolder<?, S>> boolean anyMatches(StateDefinition<?, S>  properties, S targetProperty);
}
