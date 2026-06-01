package com.epherical.professions.api.perks;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public interface IStartupPerk extends IKeyable {

    Perk.ModificationStage getModificationStage();

    double getValue();

    Identifier getGroupId();

    Perk.Applicator applicator(ServerPlayer serverPlayer);


}
