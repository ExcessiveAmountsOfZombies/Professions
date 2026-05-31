package com.epherical.professions.api.perks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface IStartupPerk extends IKeyable {

    Perk.ModificationStage getModificationStage();

    double getValue();

    ResourceLocation getGroupId();

    Perk.Applicator applicator(ServerPlayer serverPlayer);


}
