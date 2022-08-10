package com.epherical.professions.mixin.accessor;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = StatePropertiesPredicate.class)
public interface StatePropertiesPredicateAccess {

    @Accessor("properties")
    List<StatePropertiesPredicate.PropertyMatcher> getProperties();

}
