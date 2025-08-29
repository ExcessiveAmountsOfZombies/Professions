package com.epherical.professions.registries;

import com.epherical.professions.core.actions.Action;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;


@FunctionalInterface
interface Resolver<T> {
    void resolve(RegistryAccess access, Multimap<Holder<?>, Action> sink);
}
