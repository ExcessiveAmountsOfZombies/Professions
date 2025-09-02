package com.epherical.professions.commands;

import com.epherical.professions.CommonClass;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.Action;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ProfessionsStandardCommands {

    private final Logger LOGGER = LogUtils.getLogger();

    private final CommonClass mod;

    public ProfessionsStandardCommands(CommonClass mod, CommandDispatcher<CommandSourceStack> stackCommandDispatcher, CommandBuildContext commandBuildContext) {
        this.mod = mod;
        this.registerCommands(stackCommandDispatcher, commandBuildContext);
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


    private void registerCommands(CommandDispatcher<CommandSourceStack> stack, CommandBuildContext buildContext) {
        SuggestionProvider<CommandSourceStack> playerProvider = (context, builder) -> {
            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                builder.suggest(player.getGameProfile().getName());
            }
            return builder.buildFuture();
        };

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("professions")
                .then(Commands.literal("info")
                        .then(Commands.argument("occupation", ResourceOrTagArgument.resourceOrTag(buildContext, CommonClass.PROFESSION_REGISTRY_KEY))
                                .executes(this::info)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(this::info))));
        stack.register(command);

    }


    private int info(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        int oldPage = 1;
        ResourceOrTagArgument.Result<Profession> potentialProfession = ResourceOrTagArgument.getResourceOrTag(stack, "occupation", CommonClass.PROFESSION_REGISTRY_KEY);
        try {
            oldPage = IntegerArgumentType.getInteger(stack, "page");
        } catch (IllegalArgumentException ignored) {
        }

        Optional<Holder.Reference<Profession>> left = potentialProfession.unwrap().left();
        final int finalOldPage = oldPage;
        left.ifPresent(p -> {
            try {
                Profession profession = p.value();


                Collection<Holder<?>> holdersForProfession = mod.getActionLoader().getHoldersForProfession(p);


                List<Component> components = new ArrayList<>();

                for (Holder<?> holder : holdersForProfession) {
                    Collection<Action> actionsByHolder = mod.getActionLoader().getActionsByHolder(holder);
                    for (Action action : actionsByHolder) {
                        if (p.equals(action.getProfession())) {
                            components.add(Component.literal(holder.getRegisteredName()));
                        }
                    }
                }

                // todo; probably move this
                if (profession == null) {
                    stack.getSource().sendFailure(Component.translatable("professions.command.error.profession_does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                    return;
                }


               /* for (ActionType actionType : RegistryConstants.ACTION_TYPE) {
                    Collection<Action<?>> actionsFor = profession.getActions(actionType);
                    if (actionsFor != null && !actionsFor.isEmpty()) {
                        components.add(Component.translatable("=-=-=| %s |=-=-=",
                                        Component.translatable(actionType.getTranslationKey()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)));
                        for (Action<?> action : actionsFor) {
                            components.addAll(action.displayInformation());
                        }
                    }
                }*/

                int messages = components.size();
                int messagesPerPage = 12;
                int maxPage = Math.max(messages / messagesPerPage, 1);
                maxPage = (messages % messagesPerPage != 0) && messages > messagesPerPage ? maxPage + 1 : maxPage;
                // =-=-=| Break Block |=-=-=
                // =-=-=| Prev curPage/maxPage Next |=-=-=
                int begin = finalOldPage == 1 ? 0 : Math.min(messages, ((finalOldPage - 1) * messagesPerPage));
                int end = finalOldPage == 1 ? Math.min(messages, messagesPerPage) : Math.min(messages, (finalOldPage * messagesPerPage));

                if (finalOldPage > maxPage) {
                    stack.getSource().sendFailure(Component.translatable("professions.command.error.missing_page").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
                    //return 0;
                }

                for (Component component : components.subList(begin, end)) {
                    stack.getSource().sendSuccess(() -> component, false);
                }

                MutableComponent previous = Component.translatable("professions.command.prev").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (finalOldPage - 1))));
                MutableComponent next = Component.translatable("professions.command.next").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/professions info \"" + potentialProfession + "\" " + (finalOldPage + 1))));

                MutableComponent pageComp = Component.translatable("=-=-=| %s %s/%s %s |=-=-=", previous, finalOldPage, maxPage, next).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
                stack.getSource().sendSuccess(() -> pageComp, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        return 1;
    }
}
