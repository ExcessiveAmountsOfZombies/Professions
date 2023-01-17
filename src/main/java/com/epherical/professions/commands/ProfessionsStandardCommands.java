package com.epherical.professions.commands;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionMod;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.modifiers.perks.builtin.ScalingAttributePerk;
import com.epherical.professions.profession.modifiers.perks.builtin.SingleAttributePerk;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.util.ActionLogger;
import com.epherical.professions.util.AttributeDisplay;
import com.epherical.professions.util.XpRateTimerTask;
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
import com.mojang.logging.LogUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ProfessionsStandardCommands {

    private final Logger LOGGER = LogUtils.getLogger();

    private final ProfessionMod mod;

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS = (commandContext, suggestionsBuilder) -> {
        Collection<Advancement> collection = (commandContext.getSource()).getServer().getAdvancements().getAllAdvancements();
        return SharedSuggestionProvider.suggestResource(collection.stream().map(Advancement::getId), suggestionsBuilder);
    };


    public ProfessionsStandardCommands(ProfessionMod mod, CommandDispatcher<CommandSourceStack> stackCommandDispatcher) {
        this.mod = mod;
        this.registerCommands(stackCommandDispatcher);
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

    public abstract Predicate<CommandSourceStack> helpPredicate();

    public abstract Predicate<CommandSourceStack> joinPredicate();

    public abstract Predicate<CommandSourceStack> leavePredicate();

    public abstract Predicate<CommandSourceStack> leaveAllPredicate();

    public abstract Predicate<CommandSourceStack> profilePredicate();

    public abstract Predicate<CommandSourceStack> infoPredicate();

    public abstract Predicate<CommandSourceStack> statsPredicate();

    public abstract Predicate<CommandSourceStack> browsePredicate();

    public abstract Predicate<CommandSourceStack> topPredicate();

    public abstract Predicate<CommandSourceStack> reloadPredicate();

    public abstract Predicate<CommandSourceStack> firePredicate();

    public abstract Predicate<CommandSourceStack> fireAllPredicate();

    public abstract Predicate<CommandSourceStack> employPredicate();

    public abstract Predicate<CommandSourceStack> setLevelPredicate();

    public abstract Predicate<CommandSourceStack> checkXpPredicate();

    public abstract Predicate<CommandSourceStack> xpRatePredicate();

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

        /*if (Constants.devDebug) {
            RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> occupation1 = Commands.argument("occupation", ResourceLocationArgument.id());
            occupation1.suggests(occupationProvider);
            for (UnlockCommands value : UnlockCommands.values()) {
                occupation1.then(value.source);
            }

            LiteralArgumentBuilder<CommandSourceStack> edit = Commands.literal("prfssns")
                    .requires(Permissions.require("professions.command.admin.uedit", 4))
                    .then(Commands.literal("append").then(occupation1))
                    .then(Commands.literal("export")
                            .then(Commands.argument("suffix", StringArgumentType.string())
                                    .executes(context -> {
                                        String suffix = StringArgumentType.getString(context, "suffix");
                                        for (Map.Entry<ResourceLocation, Append.Builder> entry : UnlockCommands.builderMap.entrySet()) {

                                            JsonElement serialize = FabricProfLoader.serialize(entry.getValue().build());
                                            try {
                                                Files.writeString(CommonPlatform.platform.getRootConfigPath().resolve(entry.getKey().getPath()
                                                        + suffix + ".json"), serialize.toString());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        UnlockCommands.builderMap.clear();
                                        return 1;
                                    })));
            stack.register(edit);
        }*/

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("professions")
                .then(Commands.literal("help")
                        .requires(helpPredicate())
                        .executes(this::help))
                .then(Commands.literal("join")
                        .requires(joinPredicate())
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::join)))
                .then(Commands.literal("leave")
                        .requires(leavePredicate())
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::leave)))
                .then(Commands.literal("leaveall")
                        .requires(leaveAllPredicate())
                        .executes(this::leaveAll))
                .then(Commands.literal("profile")
                        .requires(profilePredicate())
                        .executes(this::profile)
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .executes(this::profile)))
                /*.then(Commands.literal("unlocks")
                        .requires(Permissions.require("professions.command.unlocks", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::unlocks)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(this::unlocks))))
                .then(Commands.literal("perks")
                        .requires(Permissions.require("professions.command.perks", 0))
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::perks)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(this::perks))))*/
                .then(Commands.literal("info")
                        .requires(infoPredicate())
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::info)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(this::info))))
                .then(Commands.literal("stats")
                        .requires(statsPredicate())
                        .executes(this::stats)
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .executes(this::stats)))
                .then(Commands.literal("browse")
                        .requires(browsePredicate())
                        .executes(this::browse))
                .then(Commands.literal("top")
                        .requires(topPredicate())
                        .then(Commands.argument("occupation", StringArgumentType.string())
                                .suggests(occupationProvider)
                                .executes(this::top)))
                .then(Commands.literal("reload")
                        .requires(reloadPredicate())
                        .executes(this::reload))
                .then(Commands.literal("fire")
                        .requires(firePredicate())
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .executes(this::fire))))
                .then(Commands.literal("fireall")
                        .requires(fireAllPredicate())
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .executes(this::fireAll)))
                .then(Commands.literal("employ")
                        .requires(employPredicate())
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .executes(this::employ))))
                .then(Commands.literal("setlevel")
                        .requires(setLevelPredicate())
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(playerProvider)
                                .then(Commands.argument("occupation", StringArgumentType.string())
                                        .suggests(occupationProvider)
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                .executes(this::setLevel)))))
                .then(Commands.literal("admin")
                        .then(Commands.literal("checkexp")
                                .requires(checkXpPredicate())
                                .then(Commands.argument("occupation", ResourceLocationArgument.id())
                                        .suggests(occupationProvider)
                                        .executes(this::checkExp)))
                        .then(Commands.literal("xprate")
                                .requires(xpRatePredicate())
                                .executes(this::xpRate)))
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
            stack.getSource().sendSuccess(Component.literal("/professions ").append(commandNodeStringEntry.getValue()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)), false);
        }
        return 1;
    }

    private int profile(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        String playerArg = "";
        try {
            playerArg = StringArgumentType.getString(stack, "player");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile = getGameProfile(stack, playerArg);
            if (profile == null) {
                return 0;
            }

            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer pPlayer = manager.getPlayer(commandPlayer.getUUID());
            if (pPlayer == null) {
                return 0;
            }

            Style borders = Style.EMPTY.withColor(ProfessionConfig.headerBorders);

            stack.getSource().sendSuccess(Component.literal("╔══╦════╦══╗")
                    .setStyle(borders), false);
            stack.getSource().sendSuccess(Component.translatable("╠══╩%s╿╩══╣", Component.literal("Profile").setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                    .setStyle(borders), false);
            stack.getSource().sendSuccess(Component.translatable("║ Name: %s", Component.literal(profile.getName()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(borders), false);
            stack.getSource().sendSuccess(Component.translatable("║ %s", Component.literal("/stats command")
                    .setStyle(Style.EMPTY
                            .withColor(ProfessionConfig.variables)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to run command")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions stats " + profile.getName()))))
                    .setStyle(borders), false);
            stack.getSource().sendSuccess(Component.literal("╠══════════╣")
                    .setStyle(borders), false);
            stack.getSource().sendSuccess(Component.literal("║ Perks Unlocked")
                    .setStyle(borders), false);


            AttributeDisplay display = new AttributeDisplay();
            // better method?
            for (Occupation activeOccupation : pPlayer.getActiveOccupations()) {
                for (Perk perk : activeOccupation.getData().getUnlockedPerkByType(Perks.SINGLE_ATTRIBUTE_PERK, pPlayer)) {
                    SingleAttributePerk cast = (SingleAttributePerk) perk;
                    cast.addAttributeData(activeOccupation, display);
                }
                for (Perk perk : activeOccupation.getData().getUnlockedPerkByType(Perks.SCALING_ATTRIBUTE_PERK, pPlayer)) {
                    ScalingAttributePerk cast = (ScalingAttributePerk) perk;
                    cast.addAttributeData(activeOccupation, display);
                }
            }
            Map<Attribute, MutableComponent> values = display.getValues();
            if (values.size() == 0) {
                stack.getSource().sendSuccess(Component.literal("║      No Perks")
                        .setStyle(borders), false);
                stack.getSource().sendSuccess(Component.literal("║")
                        .setStyle(borders), false);
                stack.getSource().sendSuccess(Component.literal("║      Unlocked")
                        .setStyle(borders), false);
            } else {
                int inc = 0;
                MutableComponent comp = Component.literal("║").setStyle(borders);
                for (MutableComponent value : values.values()) {
                    inc++;
                    comp.append(" ").append(value);
                    if (inc % 3 == 0) {
                        stack.getSource().sendSuccess(comp, false);
                        // todo; this is bugged
                        comp = Component.literal("║").setStyle(borders);
                    }
                }
                if (comp != null) {
                    stack.getSource().sendSuccess(comp, false);
                }
            }

            stack.getSource().sendSuccess(Component.literal("╠══╩════╩══╣").setStyle(borders), false);
        } catch (Exception e) {
            e.printStackTrace();
            // don't display errors to clients
        }
        return 0;
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

    private int unlocks(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        int page = 1;
        ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
        try {
            page = IntegerArgumentType.getInteger(stack, "page");
        } catch (IllegalArgumentException ignored) {
        }

        // no displaying errors to clients.
        try {
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            if (profession == null) {
                stack.getSource().sendFailure(Component.translatable("professions.command.error.profession_does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return 0;
    }

    private int perks(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        return 0;
    }

    private int info(CommandContext<CommandSourceStack> stack) {
        int page = 1;
        ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
        try {
            page = IntegerArgumentType.getInteger(stack, "page");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            if (profession == null) {
                stack.getSource().sendFailure(Component.translatable("professions.command.error.profession_does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }
            List<Component> components = new ArrayList<>();

            for (ActionType actionType : RegistryConstants.ACTION_TYPE) {
                Collection<Action<?>> actionsFor = profession.getActions(actionType);
                if (actionsFor != null && !actionsFor.isEmpty()) {
                    components.add(Component.translatable("=-=-=| %s |=-=-=",
                            Component.translatable(actionType.getTranslationKey()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)));
                    for (Action<?> action : actionsFor) {
                        components.addAll(action.displayInformation());
                    }
                }
            }

            int messages = components.size();
            int messagesPerPage = 12;
            int maxPage = Math.max(messages / messagesPerPage, 1);
            maxPage = (messages % messagesPerPage != 0) && messages > messagesPerPage ? maxPage + 1 : maxPage;
            // =-=-=| Break Block |=-=-=
            // =-=-=| Prev curPage/maxPage Next |=-=-=
            int begin = page == 1 ? 0 : Math.min(messages, ((page - 1) * messagesPerPage));
            int end = page == 1 ? Math.min(messages, messagesPerPage) : Math.min(messages, (page * messagesPerPage));

            if (page > maxPage) {
                stack.getSource().sendFailure(Component.translatable("professions.command.error.missing_page").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }

            for (Component component : components.subList(begin, end)) {
                stack.getSource().sendSuccess(component, false);
            }

            MutableComponent previous = Component.translatable("professions.command.prev").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (page - 1))));
            MutableComponent next = Component.translatable("professions.command.next").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (page + 1))));

            MutableComponent pageComp = Component.translatable("=-=-=| %s %s/%s %s |=-=-=", previous, page, maxPage, next).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
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
        } catch (IllegalArgumentException ignored) {
        }
        try {
            ServerPlayer commandPlayer = stack.getSource().getPlayerOrException();
            GameProfile profile = getGameProfile(stack, playerArg);
            if (profile == null) {
                return 0;
            }

            PlayerManager manager = mod.getPlayerManager();
            ProfessionalPlayer pPlayer = manager.getPlayer(profile.getId());
            MutableComponent stats = Component.translatable("professions.command.stats.header").setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
            MutableComponent headerFooter = Component.translatable("-=-=-=| %s |=-=-=-", stats).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            List<Component> components = new ArrayList<>();
            components.add(headerFooter);
            if (pPlayer != null) {
                if (pPlayer.getActiveOccupations().size() == 0 && pPlayer.getUuid().equals(commandPlayer.getUUID())) {
                    commandPlayer.sendSystemMessage(Component.translatable("professions.command.stats.error.not_in_any_professions").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                    return 1;
                }
                for (Occupation activeOccupation : pPlayer.getActiveOccupations()) {
                    double percentage = (activeOccupation.getExp() / activeOccupation.getMaxExp());
                    double bars = Math.round(percentage * 55); // how many bars should be green.
                    MutableComponent progression;
                    if (ProfessionConfig.displayXpAsPercentage) {
                        progression = Component.literal((String.format("%.1f", percentage * 100) + "%")).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
                    } else {
                        progression = Component.translatable("%s/%s exp",
                                Component.literal((String.format("%.1f", activeOccupation.getExp()))).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                                Component.literal(String.valueOf(activeOccupation.getMaxExp())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
                    }
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, progression);
                    MutableComponent mainComponent = Component.literal("").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))
                            .append(Component.literal("|".repeat((int) bars)).setStyle(Style.EMPTY.withColor(ProfessionConfig.success).withHoverEvent(event)))
                            .append(Component.literal("|".repeat((int) (55 - bars))).setStyle(Style.EMPTY.withColor(ProfessionConfig.errors).withHoverEvent(event)))
                            .append(Component.literal(" " + activeOccupation.getProfession().getDisplayName()).setStyle(Style.EMPTY.withColor(activeOccupation.getProfession().getColor()))
                                    .append(Component.translatable("professions.command.stats.level", Component.literal("" + activeOccupation.getLevel())
                                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))));
                    components.add(mainComponent);
                }

                if (components.size() > 1) {
                    for (Component component : components) {
                        commandPlayer.sendSystemMessage(component);
                    }
                } else {
                    commandPlayer.sendSystemMessage(Component.translatable("professions.command.stats.error.other_not_in_any_professions",
                                    Component.literal(profile.getName()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))).setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                }
            } else {
                commandPlayer.sendSystemMessage(Component.translatable("professions.command.error.missing_player").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
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
        MutableComponent header = Component.translatable("-=-=-=-=-=| %s |=-=-=-=-=-", Component.translatable("professions.command.browse.header")
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
        int page = 1;
        try {
            page = IntegerArgumentType.getInteger(stack, "page");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);

            if (profession == null) {
                stack.getSource().sendFailure(Component.translatable("professions.command.error.profession_does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
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
            /*int begin = page == 1 ? 0 : Math.min(messages, ((page - 1) * messagesPerPage));
            int end = page == 1 ? Math.min(messages, messagesPerPage) : Math.min(messages, (page * messagesPerPage));

            if (page > maxPage) {
                stack.getSource().sendFailure(Component.literal("That page doesn't exist!"));
                return 0;
            }*/

            if (messages == 0) {
                stack.getSource().sendFailure(Component.translatable("professions.command.error.missing_players").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return 0;
            }

            stack.getSource().sendSuccess(Component.translatable("professions.command.top.header",
                    Component.literal("" + messages).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)), profession.getDisplayComponent())
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)), false);
            int position = 1;
            for (ProfessionalPlayer player : players) {
                Component playerName;
                if (player == null) {
                    playerName = Component.translatable("professions.command.top.unknown_player");
                } else if (player.getPlayer() == null) {
                    playerName = Component.literal(player.getUuid().toString().substring(0, 12));
                } else {
                    playerName = player.getPlayer().getDisplayName();
                }
                MutableComponent msg = Component.translatable("professions.command.top.position",
                        Component.literal("" + position).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                        playerName,
                        Component.literal("" + player.getOccupation(profession).getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                        Component.literal("" + player.getOccupation(profession).getExp()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
                stack.getSource().sendSuccess(msg, false);
            }

            MutableComponent previous = Component.translatable("professions.command.prev").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions top \"" + potentialProfession + "\" " + (page - 1))));
            MutableComponent next = Component.translatable("professions.command.next").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions top \"" + potentialProfession + "\" " + (page + 1))));

            MutableComponent pageComp = Component.translatable("=-=-=| %s %s/%s %s |=-=-=", previous, page, maxPage, next).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            stack.getSource().sendSuccess(pageComp, false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return 1;
    }

    private int reload(CommandContext<CommandSourceStack> stack) {
        try {
            ProfessionConfig.reload();
            ActionLogger.reloadStyles();
            stack.getSource().sendSuccess(Component.translatable("professions.command.reload.success").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), false);
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
                commandPlayer.sendSystemMessage(Component.translatable("professions.command.fire.success").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)));
            } else {
                commandPlayer.sendSystemMessage(Component.translatable("professions.command.fire.fail").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
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

            commandPlayer.sendSystemMessage(Component.translatable("professions.command.fireall.success").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)));
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
                    commandPlayer.sendSystemMessage(Component.translatable("professions.command.setlevel.success",
                            occupation.getProfession().getDisplayComponent(),
                            Component.literal(String.valueOf(occupation.getLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                            Component.literal(profile.getName()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.success)));
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
                stack.getSource().sendFailure(Component.translatable("professions.command.error.profile_missing").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                return null;
            }
        } else {
            profile = commandPlayer.getGameProfile();
        }
        return profile;
    }

    private int checkExp(CommandContext<CommandSourceStack> stack) {
        ResourceLocation potentialProfession = ResourceLocation.tryParse(StringArgumentType.getString(stack, "occupation"));
        try {
            Profession profession = mod.getProfessionLoader().getProfession(potentialProfession);
            if (profession == null) {
                stack.getSource().sendFailure(Component.literal("Could not find that profession"));
                return 0;
            }
            int start = 1;
            int max = Math.min(profession.getMaxLevel(), 100);
            LOGGER.info("CSV experience check");
            LOGGER.info("Level,Experience");
            for (int i = start; i <= max; i++) {
                LOGGER.info("{},{}", i, (int) profession.getExperienceForLevel(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int xpRate(CommandContext<CommandSourceStack> stack) {
        try {
            ServerPlayer serverPlayer = stack.getSource().getPlayerOrException();
            UUID uuid = serverPlayer.getUUID();
            ProfessionalPlayer player = mod.getPlayerManager().getPlayer(uuid);

            if (player != null) {
                if (ActionLogger.isRunningXpCalculator(uuid)) {
                    serverPlayer.sendSystemMessage(Component.translatable("Disabled xp rate notifier"));
                    ActionLogger.removePlayerXpRateOutput(uuid);
                } else {
                    serverPlayer.sendSystemMessage(Component.translatable("enabled xp rate notifier (re-enable if you die)"));
                    ActionLogger.schedulePlayerXpRateOutput(uuid, new XpRateTimerTask(serverPlayer));
                }
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return 1;
    }

}
