package com.epherical.professions.mixin;

import com.epherical.professions.util.mixins.StatePropertiesPredicateHelper;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(StatePropertiesPredicate.class)
public class StatePropertiesPredicateMixin implements StatePropertiesPredicateHelper {


    @Shadow @Final private List<StatePropertiesPredicate.PropertyMatcher> properties;

    @Override
    public <S extends StateHolder<?, S>> boolean professions$anyMatches(StateDefinition<?, S> properties, S targetProperty) {
        for (StatePropertiesPredicate.PropertyMatcher property : this.properties) {
            if (property.match(properties, targetProperty)) {
                return true;
            }
        }
        return false;
    }
}
