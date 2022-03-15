package com.epherical.professions.commands;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class ProfessionsCommands {

    private final ProfessionsMod mod;

    public ProfessionsCommands(ProfessionsMod mod, CommandDispatcher<CommandSourceStack> stackCommandDispatcher) {
        this.mod = mod;
        registerCommands(stackCommandDispatcher);
    }
    // Commands to add
    // help - shows commands they have access to
    // join - attempts to join an occupation
    // leave - unslots an occupation
    // leaveall - unslots all occupations
    // info - displays in chat what an occupation gives.
    // stats - display in chat current occupation stats
    // browse - displays in chat a list of professions
    // top - command to display the top leveled people on the server

    // reload - reload the CONFIG, not the datapack - admin command
    // fire - fires the player from an occupation, removing it from their saved data. - admin command
    // fireall - fires the player from all occupations, removing it from their saved data. - admin command
    // employ - forcefully adds a player to an occupation - admin command
    // setlevel - sets the level of the player in an occupation - admin command
    // givexp - adds experience to the player in an occupation - admin command
    // removexp - removes experience from the player in an occupation - admin command

    private void registerCommands(CommandDispatcher<CommandSourceStack> stack) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("professions")
                .then(Commands.literal("help")
                        .requires(commandSourceStack -> true) // TODO: luckperms
                        .executes(this::help)) // todo: this command can be improved, but work on it after.
                .then(Commands.literal("join")
                        .requires(commandSourceStack -> true) // todo: luckperms
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for (ResourceLocation professionKey : mod.getProfessionLoader().getProfessionKeys()) {
                                        builder.suggest("\"" + professionKey.toString() + "\"");
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(this::join))) // todo: finish command
                .then(Commands.literal("leave")
                        .requires(commandSourceStack -> true) // todo; luckperms
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    return builder.buildFuture(); // todo; suggestions
                                })
                                .executes(this::leave))) // todo; finish command
                .then(Commands.literal("leaveall")
                        .requires(commandSourceStack -> true) // todo; luckperms
                        .executes(this::leaveAll)) // todo; finish command
                .then(Commands.literal("info")
                        .requires(commandSourceStack -> true) // todo; luckperms
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    return builder.buildFuture(); // todo; suggestions
                                })
                                .executes(this::info))) // todo; finish command
                .then(Commands.literal("stats")
                        .requires(commandSourceStack -> true) // todo luckperms
                        .executes(this::stats) // todo; command
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::stats))) // todo: finish command
                .then(Commands.literal("browse")
                        .requires(commandSourceStack -> true) // todo luckperms
                        .executes(this::browse)) // todo; finish command
                .then(Commands.literal("top")
                        .requires(commandSourceStack -> true) // todo; luckperms
                        .executes(this::top)); // todo; finish command



        stack.register(command);
    }

    private int help(CommandContext<CommandSourceStack> stack) {
        var map = CommandUsage.getSmartUsage(stack.getNodes().get(0).getNode(), stack.getSource());
        for (Map.Entry<CommandNode<CommandSourceStack>, MutableComponent> commandNodeStringEntry : map.entrySet()) {
            stack.getSource().sendSuccess(new TextComponent("/professions ").append(commandNodeStringEntry.getValue()).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)), false);
        }
        return 1;
    }

    private int join(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));

        try {
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            PlayerManager manager = mod.getPlayerManager();
            ServerPlayer player = stack.getSource().getPlayerOrException();
            ProfessionalPlayer pPlayer = manager.getPlayer(player.getUUID());

            mod.getPlayerManager().playerJoinOccupation(pPlayer, profession, OccupationSlot.ACTIVE, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int leave(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

    private int leaveAll(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

    private int info(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

    private int stats(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

    private int browse(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

    private int top(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

}
