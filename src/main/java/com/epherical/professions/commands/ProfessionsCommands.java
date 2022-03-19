package com.epherical.professions.commands;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.util.GameProfileHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        SuggestionProvider<CommandSourceStack> provider = (context, builder) -> {
            for (ResourceLocation professionKey : mod.getProfessionLoader().getProfessionKeys()) {
                builder.suggest("\"" + professionKey.toString() + "\"");
            }
            return builder.buildFuture();
        };

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("professions")
                .then(Commands.literal("help")
                        .requires(Permissions.require("professions.command.help", 0))
                        .executes(this::help)) // todo: this command can be improved, but work on it after.
                .then(Commands.literal("join")
                        .requires(Permissions.require("professions.command.join", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(provider)
                                .executes(this::join)))
                .then(Commands.literal("leave")
                        .requires(Permissions.require("professions.command.leave", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(provider)
                                .executes(this::leave)))
                .then(Commands.literal("leaveall")
                        .requires(Permissions.require("professions.command.leaveall", 0))
                        .executes(this::leaveAll))
                .then(Commands.literal("info")
                        .requires(Permissions.require("professions.command.info", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(provider)
                                .executes(this::info)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(this::info))))
                .then(Commands.literal("stats")
                        .requires(commandSourceStack -> true) // todo luckperms
                        .executes(this::stats) // todo; command
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                                        builder.suggest(player.getGameProfile().getName());
                                    }
                                    return builder.buildFuture();
                                })
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

            mod.getPlayerManager().joinOccupation(pPlayer, profession, OccupationSlot.ACTIVE, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int leave(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));

        try {
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            ServerPlayer player = stack.getSource().getPlayerOrException();
            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer pPlayer = manager.getPlayer(player.getUUID());
            manager.leaveOccupation(pPlayer, profession, player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int leaveAll(CommandContext<CommandSourceStack> stack) {

        try {
            Collection<Profession> collection = mod.getProfessionLoader().getProfessions();
            ServerPlayer player = stack.getSource().getPlayerOrException();
            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer pPlayer = manager.getPlayer(player.getUUID());
            for (Profession profession : collection) {
                manager.leaveOccupation(pPlayer, profession, player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int info(CommandContext<CommandSourceStack> stack) {
        int page = 1;
        ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
        try {
            page = IntegerArgumentType.getInteger(stack, "page");
        } catch (IllegalArgumentException ignored) {}

        try {
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            List<Component> components = new ArrayList<>();

            for (ActionType actionType : ProfessionConstants.ACTION_TYPE) {
                // todo: null checking.
                Collection<Action> actionsFor = profession.getActions(actionType);
                if (actionsFor != null && !actionsFor.isEmpty()) {
                    // todo: configurable color
                    components.add(new TranslatableComponent("=-=-=| %s |=-=-=",
                                    new TextComponent(actionType.getDisplayName()).setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)))
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                    for (Action action : actionsFor) {
                        components.addAll(action.displayInformation());
                    }
                }
            }

            int messages = components.size();
            int messagesPerPage = 12;
            int maxPage = Math.max(messages / messagesPerPage, 1);
            maxPage = messages % messagesPerPage != 0 ? maxPage + 1 : maxPage;
            // =-=-=| Break Block |=-=-=
            // =-=-=| Prev curPage/maxPage Next |=-=-=
            int begin = page == 1 ? 0 : Math.min(messages, ((page -1) * messagesPerPage));
            int end = page == 1 ? Math.min(messages, messagesPerPage) : Math.min(messages, (page * messagesPerPage));

            if (page > maxPage) {
                stack.getSource().sendFailure(new TextComponent("That page doesn't exist!"));
                return 0;
            }

            for (Component component : components.subList(begin, end)) {
                stack.getSource().sendSuccess(component, false);
            }

            MutableComponent previous = new TextComponent("Prev").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (page-1))));
            MutableComponent next = new TextComponent("Next").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (page+1))));

            MutableComponent pageComp = new TranslatableComponent("=-=-=| %s %s/%s %s |=-=-=", previous, page, maxPage, next).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            stack.getSource().sendSuccess(pageComp, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    /**
     * Check the players or another players stats. This checks online players and then {@link net.minecraft.server.players.GameProfileCache GameProfileCache}
     * to see if an offline player that has previously played on the server has any stats.
     */
    private int stats(CommandContext<CommandSourceStack> stack) {
        String playerArg = "";
        try {
            playerArg = StringArgumentType.getString(stack, "player");
        } catch (IllegalArgumentException ignored) {}
        try {
            MinecraftServer server = stack.getSource().getServer();
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile;
            if (playerArg.length() > 0) {
                ServerPlayer otherPlayer = server.getPlayerList().getPlayerByName(playerArg);
                if (otherPlayer == null) {
                    profile = ((GameProfileHelper) server.getProfileCache()).getProfileNoLookup(playerArg);
                } else {
                    profile = otherPlayer.getGameProfile();
                }
                if (profile == null) {
                    stack.getSource().sendFailure(new TextComponent("Could not find that player! Perhaps they haven't played in a long time?"));
                    return 0;
                }
            } else {
                profile = commandPlayer.getGameProfile();
            }

            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer pPlayer = manager.getPlayer(profile.getId());
            MutableComponent stats = new TextComponent("Stats").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
            MutableComponent headerFooter = new TranslatableComponent("-=-=-=| %s |=-=-=-", stats).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            List<Component> components = new ArrayList<>();
            components.add(headerFooter);
            if (pPlayer != null) {
                if (pPlayer.getActiveOccupations().size() == 0) {
                    // TODO: translations and better colors for all messages ehre
                    commandPlayer.sendMessage(new TextComponent("Join a profession before you can check your stats!"), Util.NIL_UUID);
                    return 1;
                }
                for (Occupation activeOccupation : pPlayer.getActiveOccupations()) {
                    double percentage = (activeOccupation.getExp() / activeOccupation.getMaxExp());
                    double bars = Math.round(percentage * 50); // how many bars should be green.
                    MutableComponent hoverPercentage = new TextComponent((String.format("%.2f", percentage * 100))).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("%s Percent", hoverPercentage));
                    MutableComponent mainComponent = new TextComponent("").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
                            .append(new TextComponent("|".repeat((int) bars)).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withHoverEvent(event)))
                            .append(new TextComponent("|".repeat((int) (50 - bars))).setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withHoverEvent(event)))
                            .append(new TextComponent(" " + activeOccupation.getProfession().getDisplayName()).setStyle(Style.EMPTY.withColor(activeOccupation.getProfession().getColor()))
                                    .append(new TextComponent(" Level: " + activeOccupation.getLevel()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))));
                    components.add(mainComponent);
                }

                for (Component component : components) {
                    commandPlayer.sendMessage(component, Util.NIL_UUID);
                }

            } else {
                commandPlayer.sendMessage(new TextComponent("That player does not exist"), Util.NIL_UUID);
            }

            // -=-=-=| Stats |=-=-=-
            // hover shows xp percentage
            // |||||||||||||||||||| <DisplayName> <Level>
            // config option can show to show number value
            // <curXp>/<MaxXP> <DisplayName> Level: <Level>
            // repeat x amount for each profession
        } catch (Exception e) {
            // everything goes inside a big catch so code errors aren't displayed to the client.
            e.printStackTrace();
        }


        return 1;
    }

    private int browse(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

    private int top(CommandContext<CommandSourceStack> stack) {
        return 1;
    }

}
