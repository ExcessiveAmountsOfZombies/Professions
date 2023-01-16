package com.epherical.professions.commands;

import com.epherical.professions.ProfessionMod;
import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class ProfessionsFabricCommands extends ProfessionsStandardCommands {


    public ProfessionsFabricCommands(ProfessionMod mod, CommandDispatcher<CommandSourceStack> stackCommandDispatcher) {
        super(mod, stackCommandDispatcher);
    }

    @Override
    public Predicate<CommandSourceStack> helpPredicate() {
        return Permissions.require("professions.command.help", 0);
    }

    @Override
    public Predicate<CommandSourceStack> joinPredicate() {
        return Permissions.require("professions.command.join", 0);
    }

    @Override
    public Predicate<CommandSourceStack> leavePredicate() {
        return Permissions.require("professions.command.leave", 0);
    }

    @Override
    public Predicate<CommandSourceStack> leaveAllPredicate() {
        return Permissions.require("professions.command.leaveall", 0);
    }

    @Override
    public Predicate<CommandSourceStack> profilePredicate() {
        return Permissions.require("professions.command.profile", 0);
    }

    @Override
    public Predicate<CommandSourceStack> infoPredicate() {
        return Permissions.require("professions.command.info", 0);
    }

    @Override
    public Predicate<CommandSourceStack> statsPredicate() {
        return Permissions.require("professions.command.stats", 0);
    }

    @Override
    public Predicate<CommandSourceStack> browsePredicate() {
        return Permissions.require("professions.command.browse", 0);
    }

    @Override
    public Predicate<CommandSourceStack> topPredicate() {
        return Permissions.require("professions.command.top", 0);
    }

    @Override
    public Predicate<CommandSourceStack> reloadPredicate() {
        return Permissions.require("professions.command.reload", 4);
    }

    @Override
    public Predicate<CommandSourceStack> firePredicate() {
        return Permissions.require("professions.command.fire", 4);
    }

    @Override
    public Predicate<CommandSourceStack> fireAllPredicate() {
        return Permissions.require("professions.command.fireall", 4);
    }

    @Override
    public Predicate<CommandSourceStack> employPredicate() {
        return Permissions.require("professions.command.employ", 4);
    }

    @Override
    public Predicate<CommandSourceStack> setLevelPredicate() {
        return Permissions.require("professions.command.setlevel", 4);
    }

    @Override
    public Predicate<CommandSourceStack> checkXpPredicate() {
        return Permissions.require("professions.command.admin.checkexp", 4);
    }

    @Override
    public Predicate<CommandSourceStack> xpRatePredicate() {
        return Permissions.require("professions.command.admin.xprate", 4);
    }

}
