package com.epherical.professions.model.gating;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;

import java.util.List;

public abstract class Gate<T> {

    // todo; now that the mod is going to be used on the server and the client
    //  we can enforce unlocks on the client and do backup checks on the server


    // todo; we'll probably have a GateManager like the other Manager classes, but
    //  i think it'll be better to not map the values in those lists, w'ell just check if something is or isn't something.

    protected List<Either<TagKey<T>, ResourceKey<T>>> values;
    private final Holder<Profession> profession;


    public abstract GateType getGateType();

    public abstract ResourceKey<? extends Registry<T>> getRegistryKey();

    public abstract boolean meetsGateRequirement(Occupation occupation, ProfessionContext context);

    public List<Either<TagKey<T>, ResourceKey<T>>> getValues() {
        return values;
    }






}
