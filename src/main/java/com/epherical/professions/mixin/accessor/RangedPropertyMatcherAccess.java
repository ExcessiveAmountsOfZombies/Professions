package com.epherical.professions.mixin.accessor;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatePropertiesPredicate.RangedPropertyMatcher.class)
public interface RangedPropertyMatcherAccess {

    @Accessor("minValue")
    @Nullable
    String getMinValue();

    @Accessor("maxValue")
    @Nullable
    String getMaxValue();
}
