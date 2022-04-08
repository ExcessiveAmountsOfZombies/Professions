package com.epherical.professions.commands;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.util.ActionLogger;
import com.epherical.professions.util.mixins.GameProfileHelper;
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
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        SuggestionProvider<CommandSourceStack> occupationProvider = (context, builder) -> {
            for (ResourceLocation professionKey : mod.getProfessionLoader().getProfessionKeys()) {
                builder.suggest("\"" + professionKey.toString() + "\"");
            }
            return builder.buildFuture();
        };
        SuggestionProvider<CommandSourceStack> playerProvider = (context, builder) -> {
            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                builder.suggest(player.getGameProfile().getName());
            }
            return builder.buildFuture();
        };

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("professions")
                .then(Commands.literal("help")
                        .requires(Permissions.require("professions.command.help", 0))
                        .executes(this::help))
                .then(Commands.literal("join")
                        .requires(Permissions.require("professions.command.join", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::join)))
                .then(Commands.literal("leave")
                        .requires(Permissions.require("professions.command.leave", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::leave)))
                .then(Commands.literal("leaveall")
                        .requires(Permissions.require("professions.command.leaveall", 0))
                        .executes(this::leaveAll))
                .then(Commands.literal("info")
                        .requires(Permissions.require("professions.command.info", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::info)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(this::info))))
                .then(Commands.literal("stats")
                        .requires(Permissions.require("professions.command.stats", 0))
                        .executes(this::stats)
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .executes(this::stats)))
                .then(Commands.literal("browse")
                        .requires(Permissions.require("professions.command.browse", 0))
                        .executes(this::browse))
                .then(Commands.literal("top")
                        .requires(Permissions.require("professions.command.top", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::top)))
                .then(Commands.literal("reload")
                        .requires(Permissions.require("professions.command.reload", 4))
                        .executes(this::reload))
                .then(Commands.literal("fire")
                        .requires(Permissions.require("professions.command.fire", 4))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .executes(this::fire))))
                .then(Commands.literal("fireall")
                        .requires(Permissions.require("professions.command.fireall", 4))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .executes(this::fireAll)))
                .then(Commands.literal("employ")
                        .requires(Permissions.require("professions.command.employ", 4))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .executes(this::employ))))
                .then(Commands.literal("setlevel")
                        .requires(Permissions.require("professions.command.setlevel", 4))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                .executes(this::setLevel)))))
                /*.then(Commands.literal("givexp")
                        .requires(Permissions.require("professions.command.givexp", 4))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .then(Commands.argument("xp", IntegerArgumentType.integer(1))
                                                .executes(this::givexp)))))
                .then(Commands.literal("removexp")
                        .requires(Permissions.require("professions.command.removexp", 4))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .then(Commands.argument("xp", IntegerArgumentType.integer(1))
                                                .executes(this::takexp)))))*/;
        stack.register(command);
    }

    private int help(CommandContext<CommandSourceStack> stack) {
        var map = CommandUsage.getSmartUsage(stack.getNodes().get(0).getNode(), stack.getSource());
        for (Map.Entry<CommandNode<CommandSourceStack>, MutableComponent> commandNodeStringEntry : map.entrySet()) {
            stack.getSource().sendSuccess(new TextComponent("/professions ").append(commandNodeStringEntry.getValue()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)), false);
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
            if (profession == null) {
                stack.getSource().sendFailure(new TranslatableComponent("professions.command.error.profession_does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }
            List<Component> components = new ArrayList<>();

            for (ActionType actionType : ProfessionConstants.ACTION_TYPE) {
                Collection<Action> actionsFor = profession.getActions(actionType);
                if (actionsFor != null && !actionsFor.isEmpty()) {
                    components.add(new TranslatableComponent("=-=-=| %s |=-=-=",
                                    new TranslatableComponent(actionType.getTranslationKey()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)));
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
                stack.getSource().sendFailure(new TranslatableComponent("professions.command.error.missing_page").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }

            for (Component component : components.subList(begin, end)) {
                stack.getSource().sendSuccess(component, false);
            }

            MutableComponent previous = new TranslatableComponent("professions.command.prev").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (page-1))));
            MutableComponent next = new TranslatableComponent("professions.command.next").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (page+1))));

            MutableComponent pageComp = new TranslatableComponent("=-=-=| %s %s/%s %s |=-=-=", previous, page, maxPage, next).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
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
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile = getGameProfile(stack, playerArg);
            if (profile == null) {
                return 0;
            }

            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer pPlayer = manager.getPlayer(profile.getId());
            MutableComponent stats = new TranslatableComponent("professions.command.stats.header").setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
            MutableComponent headerFooter = new TranslatableComponent("-=-=-=| %s |=-=-=-", stats).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            List<Component> components = new ArrayList<>();
            components.add(headerFooter);
            if (pPlayer != null) {
                if (pPlayer.getActiveOccupations().size() == 0 && pPlayer.getUuid().equals(commandPlayer.getUUID())) {
                    commandPlayer.sendMessage(new TranslatableComponent("professions.command.stats.error.not_in_any_professions").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
                    return 1;
                }
                for (Occupation activeOccupation : pPlayer.getActiveOccupations()) {
                    double percentage = (activeOccupation.getExp() / activeOccupation.getMaxExp());
                    double bars = Math.round(percentage * 55); // how many bars should be green.
                    MutableComponent progression;
                    if (ProfessionConfig.displayXpAsPercentage) {
                        progression = new TextComponent((String.format("%.1f", percentage * 100) + "%")).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
                    } else {
                        progression = new TranslatableComponent("%s/%s exp",
                                new TextComponent((String.format("%.1f", activeOccupation.getExp()))).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                                new TextComponent(String.valueOf(activeOccupation.getMaxExp())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
                    }
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, progression);
                    MutableComponent mainComponent = new TextComponent("").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))
                            .append(new TextComponent("|".repeat((int) bars)).setStyle(Style.EMPTY.withColor(ProfessionConfig.success).withHoverEvent(event)))
                            .append(new TextComponent("|".repeat((int) (55 - bars))).setStyle(Style.EMPTY.withColor(ProfessionConfig.errors).withHoverEvent(event)))
                            .append(new TextComponent(" " + activeOccupation.getProfession().getDisplayName()).setStyle(Style.EMPTY.withColor(activeOccupation.getProfession().getColor()))
                                    .append(new TranslatableComponent("professions.command.stats.level", new TextComponent("" + activeOccupation.getLevel())
                                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))));
                    components.add(mainComponent);
                }

                if (components.size() > 1) {
                    for (Component component : components) {
                        commandPlayer.sendMessage(component, Util.NIL_UUID);
                    }
                } else {
                    commandPlayer.sendMessage(new TranslatableComponent("professions.command.stats.error.other_not_in_any_professions", new TextComponent(profile.getName()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))).setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
                }
            } else {
                commandPlayer.sendMessage(new TranslatableComponent("professions.command.error.missing_player").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
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
        // -=-=-=-=-=| Browse Professions |=-=-=-=-=-
        // Miner Max Level: 100 <-- hover component the description across the entire thing
        MutableComponent header = new TranslatableComponent("-=-=-=-=-=| %s |=-=-=-=-=-", new TranslatableComponent("professions.command.browse.header")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
        List<Component> components = new ArrayList<>();
        components.add(header);
        for (Profession profession : mod.getProfessionLoader().getProfessions()) {
            components.add(profession.createBrowseMessage());
        }
        for (Component component : components) {
            stack.getSource().sendSuccess(component, false);
        }

        return 1;
    }

    private int top(CommandContext<CommandSourceStack> stack) {
        try {
            ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);

            if (profession == null) {
                stack.getSource().sendFailure(new TranslatableComponent("professions.command.error.profession_does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }

            // if it's not a database, im not going to open all the files to see whose on top, so it will only show
            // the top players who are online.
            int messagesPerPage = 12;
            Collection<ProfessionalPlayer> players = Collections.emptyList();
            if (!mod.getDataStorage().isDatabase()) {
                players = mod.getPlayerManager().getPlayers();
                players = players.stream()
                        .filter(player -> player.isOccupationActive(profession))
                        .sorted(Comparator.comparing(player -> player.getOccupation(profession).getLevel()))
                        .limit(messagesPerPage)
                        .collect(Collectors.toList());
            }
            int messages = players.size();

            int maxPage = Math.max(messages / messagesPerPage, 1);
            maxPage = messages % messagesPerPage != 0 ? maxPage + 1 : maxPage;
            // todo: add multiple pages, but for now just display top 12
            /*int begin = page == 1 ? 0 : Math.min(messages, ((page -1) * messagesPerPage));
            int end = page == 1 ? Math.min(messages, messagesPerPage) : Math.min(messages, (page * messagesPerPage));

            if (page > maxPage) {
                stack.getSource().sendFailure(new TextComponent("That page doesn't exist!"));
                return 0;
            }*/

            if (messages == 0) {
                stack.getSource().sendFailure(new TranslatableComponent("professions.command.error.missing_players").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }

            stack.getSource().sendSuccess(new TranslatableComponent("professions.command.top.header",
                    new TextComponent("" + messages).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)), profession.getDisplayComponent())
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)), false);
            int position = 1;
            for (ProfessionalPlayer player : players) {
                Component playerName;
                if (player == null) {
                    playerName = new TranslatableComponent("professions.command.top.unknown_player");
                } else {
                    playerName = player.getPlayer().getDisplayName();
                }
                MutableComponent msg = new TranslatableComponent("professions.command.top.position",
                        new TextComponent("" +  position).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                        playerName,
                        new TextComponent("" + player.getOccupation(profession).getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                        new TextComponent("" + player.getOccupation(profession).getExp()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
                stack.getSource().sendSuccess(msg, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return 1;
    }

    private int reload(CommandContext<CommandSourceStack> stack) {
        try {
            ProfessionConfig.reload();
            ActionLogger.reloadStyles();
            stack.getSource().sendSuccess(new TranslatableComponent("professions.command.reload.success").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int fire(CommandContext<CommandSourceStack> stack) {
        try {
            String playerArg = StringArgumentType.getString(stack, "player");
            ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile = getGameProfile(stack, playerArg);
            if (profile == null) {
                return 0;
            }
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            PlayerManager manager = mod.getPlayerManager();

            ProfessionalPlayer player = manager.getPlayer(profile.getId());
            if (manager.fireFromOccupation(player, profession, commandPlayer)) {
                commandPlayer.sendMessage(new TranslatableComponent("professions.command.fire.success").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), Util.NIL_UUID);
            } else {
                commandPlayer.sendMessage(new TranslatableComponent("professions.command.fire.fail").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int fireAll(CommandContext<CommandSourceStack> stack) {
        try {
            String playerArg = StringArgumentType.getString(stack, "player");
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile = getGameProfile(stack, playerArg);
            if (profile == null) {
                return 0;
            }
            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer player = manager.getPlayer(profile.getId());
            for (Profession profession : mod.getProfessionLoader().getProfessions()) {
                manager.fireFromOccupation(player, profession, commandPlayer);
            }

            commandPlayer.sendMessage(new TranslatableComponent("professions.command.fireall.success").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), Util.NIL_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int employ(CommandContext<CommandSourceStack> stack) {
        try {
            String playerArg = StringArgumentType.getString(stack, "player");
            ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile = getGameProfile(stack, playerArg);
            if (profile == null) {
                return 0;
            }
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer player = manager.getPlayer(profile.getId());
            manager.joinOccupation(player, profession, OccupationSlot.ACTIVE, commandPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int setLevel(CommandContext<CommandSourceStack> stack) {
        try {
            String playerArg = StringArgumentType.getString(stack, "player");
            ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
            int level = IntegerArgumentType.getInteger(stack, "level");
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile = getGameProfile(stack, playerArg);
            if (profile == null) {
                return 0;
            }

            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer player = manager.getPlayer(profile.getId());

            if (player != null) {
                Occupation occupation = player.getOccupation(profession);
                if (occupation != null) {
                    occupation.setLevel(level, player);
                    commandPlayer.sendMessage(new TranslatableComponent("professions.command.setlevel.success",
                            occupation.getProfession().getDisplayComponent(),
                            new TextComponent(String.valueOf(occupation.getLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                            new TextComponent(profile.getName()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), Util.NIL_UUID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }



    private GameProfile getGameProfile(CommandContext<CommandSourceStack> stack, String playerArg) throws CommandSyntaxException {
        MinecraftServer server = stack.getSource().getServer();
        ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
        GameProfile profile;
        if (playerArg.length() > 0) {
            ServerPlayer otherPlayer = server.getPlayerList().getPlayerByName(playerArg);
            if (otherPlayer == null) {
                profile = ((GameProfileHelper) server.getProfileCache()).professions$getProfileNoLookup(playerArg);
            } else {
                profile = otherPlayer.getGameProfile();
            }
            if (profile == null) {
                stack.getSource().sendFailure(new TranslatableComponent("professions.command.error.profile_missing").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return null;
            }
        } else {
            profile = commandPlayer.getGameProfile();
        }
        return profile;
    }

}
