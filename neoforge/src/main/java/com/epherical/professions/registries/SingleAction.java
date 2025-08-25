package com.epherical.professions.registries;


import com.epherical.professions.core.actions.Action;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.List;

public record SingleAction<T>(
        Either<TagKey<T>, ResourceKey<T>> entry,
        List<Action> actions) {
}
