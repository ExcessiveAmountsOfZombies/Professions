package com.epherical.professions.commands;

import com.epherical.professions.ProfessionMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.util.function.Predicate;

import static com.epherical.professions.util.ProfPermissions.*;

public class ProfessionForgeCommands extends ProfessionsStandardCommands {

    public ProfessionForgeCommands(ProfessionMod mod, CommandDispatcher<CommandSourceStack> stackCommandDispatcher) {
        super(mod, stackCommandDispatcher);
    }

    private Predicate<CommandSourceStack> require(PermissionNode<Boolean> node) {
        return commandSourceStack -> {
            try {
                ServerPlayer player = commandSourceStack.getPlayerOrException();
                return PermissionAPI.getPermission(player, node);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            return false;
        };
    }

    @Override
    public Predicate<CommandSourceStack> helpPredicate() {
        return require(HELP);
    }

    @Override
    public Predicate<CommandSourceStack> joinPredicate() {
        return require(JOIN);
    }

    @Override
    public Predicate<CommandSourceStack> leavePredicate() {
        return require(LEAVE);
    }

    @Override
    public Predicate<CommandSourceStack> leaveAllPredicate() {
        return require(LEAVE_ALL);
    }

    @Override
    public Predicate<CommandSourceStack> profilePredicate() {
        return require(PROFILE);
    }

    @Override
    public Predicate<CommandSourceStack> infoPredicate() {
        return require(INFO);
    }

    @Override
    public Predicate<CommandSourceStack> statsPredicate() {
        return require(STATS);
    }

    @Override
    public Predicate<CommandSourceStack> browsePredicate() {
        return require(BROWSE);
    }

    @Override
    public Predicate<CommandSourceStack> topPredicate() {
        return require(TOP);
    }

    @Override
    public Predicate<CommandSourceStack> reloadPredicate() {
        return require(RELOAD);
    }

    @Override
    public Predicate<CommandSourceStack> firePredicate() {
        return require(FIRE);
    }

    @Override
    public Predicate<CommandSourceStack> fireAllPredicate() {
        return require(FIRE_ALL);
    }

    @Override
    public Predicate<CommandSourceStack> employPredicate() {
        return require(EMPLOY);
    }

    @Override
    public Predicate<CommandSourceStack> setLevelPredicate() {
        return require(SET_LEVEL);
    }

    @Override
    public Predicate<CommandSourceStack> checkXpPredicate() {
        return stack -> false;
    }

    @Override
    public Predicate<CommandSourceStack> xpRatePredicate() {
        return stack -> false;
    }

}
