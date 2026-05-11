package com.epherical.professions.presentation.commands;

import com.epherical.professions.ActionManager;
import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.epherical.professions.domain.exception.ProfessionNotActiveException;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.rewards.Reward;
import com.epherical.professions.data.config.ProfessionConfig;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ProfessionsStandardCommands {

    private static final int MESSAGES_PER_PAGE = 12;

    private final ActionManager actionManager;
    private final PlayerManager playerManager;

    public ProfessionsStandardCommands(ProfessionsCommon mod, CommandDispatcher<CommandSourceStack> stackCommandDispatcher,
                                       CommandBuildContext commandBuildContext, ActionManager actionManager) {
        this.actionManager = actionManager;
        this.playerManager = mod.getPlayerManager();
        this.registerCommands(stackCommandDispatcher, commandBuildContext);
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> stack, CommandBuildContext buildContext) {

        //todo; we should write a command that can remove the perkIDs from the player.
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("professions")
                .then(Commands.literal("info")
                        .then(Commands.argument("occupation", ResourceOrTagArgument.resourceOrTag(buildContext, ProfessionsCommon.PROFESSION_REGISTRY_KEY))
                                .executes(this::info)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(this::info))))
                .then(Commands.literal("setlevel")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("occupation", ResourceOrTagArgument.resourceOrTag(buildContext, ProfessionsCommon.PROFESSION_REGISTRY_KEY))
                                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                        .executes(this::setLevel))))
                .then(Commands.literal("unclaimedperks")
                        .executes(this::unclaimedPerks))
                .then(Commands.literal("claimperk")
                        .then(Commands.argument("perk_id", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    ServerPlayer sourcePlayer;
                                    try {
                                        sourcePlayer = context.getSource().getPlayerOrException();
                                    } catch (CommandSyntaxException exception) {
                                        return builder.buildFuture();
                                    }

                                    IProfessionalPlayer player = playerManager.getPlayer(sourcePlayer.getUUID());
                                    if (player == null) {
                                        return builder.buildFuture();
                                    }

                                    Set<ResourceLocation> perkIds = playerManager.getUnlockedUnclaimedPerkIds(player);
                                    for (ResourceLocation perkId : perkIds) {
                                        builder.suggest(perkId.toString());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(this::claimPerk)));
        stack.register(command);
    }

    private int setLevel(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ServerPlayer sourcePlayer = stack.getSource().getPlayerOrException();
        IProfessionalPlayer player = playerManager.getPlayer(sourcePlayer.getUUID());
        if (player == null) {
            stack.getSource().sendFailure(Component.translatable("professions.command.error.missing_player")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        ResourceOrTagArgument.Result<Profession> potentialProfession = ResourceOrTagArgument.getResourceOrTag(stack, "occupation", ProfessionsCommon.PROFESSION_REGISTRY_KEY);
        Optional<Holder.Reference<Profession>> professionResult = potentialProfession.unwrap().left();
        if (professionResult.isEmpty()) {
            stack.getSource().sendFailure(Component.translatable("professions.command.error.profession_does_not_exist")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        Holder.Reference<Profession> professionHolder = professionResult.get();
        Occupation occupation = player.getOccupation(professionHolder);
        if (occupation == null) {
            stack.getSource().sendFailure(Component.literal("Profession is not active: " + professionHolder.key().location())
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        int level = IntegerArgumentType.getInteger(stack, "level");
        try {
            occupation.setLevel(level, player);
        } catch (ProfessionNotActiveException exception) {
            stack.getSource().sendFailure(Component.literal("Could not set level for profession: " + professionHolder.key().location())
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        stack.getSource().sendSuccess(() -> Component.literal("Set ")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.success))
                .append(professionHolder.value().displayName().copy()
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                .append(Component.literal(" level to ")
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.success)))
                .append(Component.literal(String.valueOf(level))
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))), false);
        return 1;
    }

    private int unclaimedPerks(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ServerPlayer sourcePlayer = stack.getSource().getPlayerOrException();
        IProfessionalPlayer player = playerManager.getPlayer(sourcePlayer.getUUID());
        if (player == null) {
            stack.getSource().sendFailure(Component.translatable("professions.command.error.missing_player")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        Set<ResourceLocation> unclaimedPerkIds = playerManager.getUnlockedUnclaimedPerkIds(player);
        if (unclaimedPerkIds.isEmpty()) {
            stack.getSource().sendSuccess(() -> border(Component.literal("Unclaimed Perk IDs")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))), false);
            stack.getSource().sendSuccess(() -> Component.literal("None")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)), false);
            return 1;
        }

        List<ResourceLocation> sortedUnclaimedPerkIds = unclaimedPerkIds.stream()
                .sorted()
                .toList();

        stack.getSource().sendSuccess(() -> border(Component.literal("Unclaimed Perk IDs")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))), false);
        for (ResourceLocation perkId : sortedUnclaimedPerkIds) {
            stack.getSource().sendSuccess(() -> Component.literal("- ")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))
                    .append(Component.literal(perkId.toString())
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))), false);
        }
        return 1;
    }

    private int claimPerk(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ServerPlayer sourcePlayer = stack.getSource().getPlayerOrException();
        IProfessionalPlayer player = playerManager.getPlayer(sourcePlayer.getUUID());
        if (player == null) {
            stack.getSource().sendFailure(Component.translatable("professions.command.error.missing_player")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        ResourceLocation perkId = ResourceLocationArgument.getId(stack, "perk_id");
        if (!playerManager.claimUnlockedReward(player, perkId)) {
            stack.getSource().sendFailure(Component.literal("Could not claim perk id: " + perkId)
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        stack.getSource().sendSuccess(() -> Component.literal("Claimed perk id: ")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.success))
                .append(Component.literal(perkId.toString())
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))), false);
        return 1;
    }

    private int info(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        int page = 1;
        ResourceOrTagArgument.Result<Profession> potentialProfession = ResourceOrTagArgument.getResourceOrTag(stack, "occupation", ProfessionsCommon.PROFESSION_REGISTRY_KEY);
        try {
            page = IntegerArgumentType.getInteger(stack, "page");
        } catch (IllegalArgumentException ignored) {
        }

        Optional<Holder.Reference<Profession>> professionResult = potentialProfession.unwrap().left();
        if (professionResult.isEmpty()) {
            stack.getSource().sendFailure(Component.translatable("professions.command.error.profession_does_not_exist")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        Holder.Reference<Profession> professionHolder = professionResult.get();
        Profession profession = professionHolder.value();

        Collection<Action<?>> actionsByProfession = actionManager.getActionsByProfession(professionHolder);
        Multimap<ActionType, Component> componentsByType = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        buildActionComponents(actionsByProfession, componentsByType);

        List<Component> components = new ArrayList<>();
        List<ActionType> sortedTypes = new ArrayList<>(componentsByType.keySet());
        sortedTypes.sort(Comparator.comparing(type -> Component.translatable(type.translationKey()).getString()));
        for (ActionType actionType : sortedTypes) {
            Collection<Component> typeComponents = componentsByType.get(actionType);
            if (!typeComponents.isEmpty()) {
                components.add(actionTypeBorder(actionType));
                components.addAll(typeComponents);
            }
        }

        if (components.isEmpty()) {
            stack.getSource().sendSuccess(() -> professionHeader(profession), false);
            stack.getSource().sendFailure(Component.literal("No actions are configured for this profession.")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        int maxPage = Math.max((components.size() + MESSAGES_PER_PAGE - 1) / MESSAGES_PER_PAGE, 1);
        if (page > maxPage) {
            stack.getSource().sendFailure(Component.translatable("professions.command.error.missing_page")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return 0;
        }

        int currentPage = page;
        int begin = Math.min(components.size(), (currentPage - 1) * MESSAGES_PER_PAGE);
        int end = Math.min(components.size(), currentPage * MESSAGES_PER_PAGE);

        stack.getSource().sendSuccess(() -> professionHeader(profession), false);
        for (Component component : components.subList(begin, end)) {
            stack.getSource().sendSuccess(() -> component, false);
        }
        stack.getSource().sendSuccess(() -> pageFooter(professionHolder, currentPage, maxPage), false);
        return 1;
    }

    private void buildActionComponents(Collection<Action<?>> actions, Multimap<ActionType, Component> componentsByType) {
        List<Action<?>> sortedActions = new ArrayList<>(actions);
        sortedActions.sort(Comparator
                .comparing((Action<?> action) -> action.getType().translationKey())
                .thenComparing(action -> actionManager.getValuesForAction(action).stream()
                        .map(this::holderSortKey)
                        .sorted()
                        .findFirst()
                        .orElse("")));

        for (Action<?> action : sortedActions) {
            Set<Holder<?>> resolvedValues = new LinkedHashSet<>(actionManager.getValuesForAction(action));
            List<Holder<?>> sortedValues = new ArrayList<>(resolvedValues);
            sortedValues.sort(Comparator.comparing(this::holderSortKey));

            for (Holder<?> value : sortedValues) {
                componentsByType.put(action.getType(), actionLine(value, action.getRewards()));
            }
        }
    }

    private Component professionHeader(Profession profession) {
        return border(profession.displayName().copy().setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)));
    }

    private Component actionTypeBorder(ActionType actionType) {
        return border(Component.translatable(actionType.translationKey()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)));
    }

    private Component border(Component middle) {
        return Component.literal("=-=-=| ")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))
                .append(middle)
                .append(Component.literal(" |=-=-=").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)));
    }

    private Component actionLine(Holder<?> value, List<Reward<?>> perks) {
        MutableComponent component = Component.literal("")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))
                .append(readableValue(value).copy().setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)));

        if (!perks.isEmpty()) {
            component.append(Component.literal(" | ").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)));
            component.append(perkSummary(perks));
        }

        return component;
    }

    private MutableComponent perkSummary(List<Reward<?>> perks) {
        MutableComponent summary = Component.empty();
        for (int i = 0; i < perks.size(); i++) {
            if (i > 0) {
                summary.append(Component.literal(", ").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)));
            }
            summary.append(perks.get(i).getRewardName().copy());
        }
        return summary;
    }

    private Component readableValue(Holder<?> value) {
        Object rawValue = value.value();
        return switch (rawValue) {
            case Block block -> block.getName();
            case Item item -> item.getDescription();
            case ItemLike itemLike -> itemLike.asItem().getDescription();
            default -> Component.literal(value.getRegisteredName());
        };
    }

    private String holderSortKey(Holder<?> holder) {
        return readableValue(holder).getString();
    }

    private Component pageFooter(Holder.Reference<Profession> profession, int page, int maxPage) {
        int previousPage = Math.max(1, page - 1);
        int nextPage = Math.min(maxPage, page + 1);
        String commandBase = "/professions info " + profession.key().location();

        MutableComponent previous = Component.translatable("professions.command.prev")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors));
        if (page > 1) {
            previous = previous.copy().setStyle(previous.getStyle()
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandBase + " " + previousPage)));
        }

        MutableComponent next = Component.translatable("professions.command.next")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
        if (page < maxPage) {
            next = next.copy().setStyle(next.getStyle()
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandBase + " " + nextPage)));
        }

        return Component.literal("=-=-=| ")
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders))
                .append(previous)
                .append(Component.literal(" " + page + "/" + maxPage + " ").setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                .append(next)
                .append(Component.literal(" |=-=-=").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)));
    }
}
