package com.epherical.professions.util.mixins;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public interface StatePropertiesPredicateHelper {

    <S extends StateHolder<?, S>> boolean professions$anyMatches(StateDefinition<?, S>  properties, S targetProperty);
}
