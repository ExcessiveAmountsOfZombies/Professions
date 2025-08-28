package com.epherical.professions.registries;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Map;

public record ActionOf<T>(
        boolean replace,
        Map<Either<TagKey<T>, ResourceKey<T>>, SingleAction<T>> values) {
}
