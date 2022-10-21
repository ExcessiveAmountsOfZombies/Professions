package com.epherical.professions.commands;

import com.epherical.professions.profession.editor.Append;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.profession.unlock.builtin.AdvancementUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockBreakUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockDropUnlock;
import com.epherical.professions.profession.unlock.builtin.EquipmentUnlock;
import com.epherical.professions.profession.unlock.builtin.InteractionUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.epherical.professions.commands.ProfessionsCommands.SUGGEST_ADVANCEMENTS;

public enum UnlockCommands {


    BLOCK_DROP_UNLOCK(Commands.argument("blocks", ResourceOrTagLocationArgument.resourceOrTag(Registry.BLOCK_REGISTRY))
            .then(Commands.argument("level", IntegerArgumentType.integer(1))
                    .executes(UnlockCommands::blockDrop))),
    BLOCK_BREAK_UNLOCK(Commands.argument("blocks", ResourceOrTagLocationArgument.resourceOrTag(Registry.BLOCK_REGISTRY))
            .then(Commands.argument("level", IntegerArgumentType.integer(1))
                    .executes(UnlockCommands::blockBreak))),
    TOOL_UNLOCK(Commands.argument("items", ResourceOrTagLocationArgument.resourceOrTag(Registry.ITEM_REGISTRY))
            .then(Commands.argument("level", IntegerArgumentType.integer(1))
                    .executes(UnlockCommands::tool))),
    ADVANCEMENT_UNLOCK(Commands.argument("advancement", ResourceLocationArgument.id())
            .suggests(SUGGEST_ADVANCEMENTS)
            .then(Commands.argument("items", ResourceOrTagLocationArgument.resourceOrTag(Registry.ITEM_REGISTRY))
                    .executes(UnlockCommands::advancement))),
    EQUIPMENT_UNLOCK(Commands.argument("items", ResourceOrTagLocationArgument.resourceOrTag(Registry.ITEM_REGISTRY))
            .then(Commands.argument("level", IntegerArgumentType.integer(1))
                    .executes(UnlockCommands::equipmentUnlock))),
    INTERACTION_UNLOCK(Commands.argument("items", ResourceOrTagLocationArgument.resourceOrTag(Registry.ITEM_REGISTRY))
            .then(Commands.argument("level", IntegerArgumentType.integer(1))
                    .executes(UnlockCommands::interactionUnlock))
    );

    public final LiteralArgumentBuilder<CommandSourceStack> source;

    public static Map<ResourceLocation, Append.Builder> builderMap = new HashMap<>();

    UnlockCommands(RequiredArgumentBuilder<CommandSourceStack, ?> source) {
        this.source = Commands.literal(this.name().toLowerCase(Locale.ROOT))
                .then(source);
    }

    private static int blockDrop(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation professionID = ResourceLocationArgument.getId(stack, "occupation");
        ResourceOrTagLocationArgument.Result<Block> registryType = ResourceOrTagLocationArgument.getRegistryType(stack, "blocks", Registry.BLOCK_REGISTRY, new DynamicCommandExceptionType(o -> {
            return new LiteralMessage("it broke");
        }));
        int level = IntegerArgumentType.getInteger(stack, "level");

        BlockDropUnlock.Builder unlockBuilder = BlockDropUnlock.builder().level(level);
        registryType.unwrap().ifRight(unlockBuilder::tag);
        registryType.unwrap().ifLeft(itemResourceKey -> unlockBuilder.block(Registry.BLOCK.get(itemResourceKey.location())));
        Append.Builder builder;
        if (builderMap.containsKey(professionID)) {
            builder = builderMap.get(professionID);
        } else {
            builder = Append.Builder.appender(professionID);
        }

        builder.addUnlock(Unlocks.BLOCK_DROP_UNLOCK, unlockBuilder);
        builderMap.put(professionID, builder);
        return writeToFile(professionID, builder);
    }

    private static int blockBreak(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation professionID = ResourceLocationArgument.getId(stack, "occupation");
        ResourceOrTagLocationArgument.Result<Block> registryType = ResourceOrTagLocationArgument.getRegistryType(stack, "blocks", Registry.BLOCK_REGISTRY, new DynamicCommandExceptionType(o -> {
            return new LiteralMessage("it broke");
        }));
        int level = IntegerArgumentType.getInteger(stack, "level");
        BlockBreakUnlock.Builder unlockBuilder = BlockBreakUnlock.builder().level(level);
        registryType.unwrap().ifRight(unlockBuilder::tag);
        registryType.unwrap().ifLeft(itemResourceKey -> unlockBuilder.block(Registry.BLOCK.get(itemResourceKey.location())));
        Append.Builder builder;
        if (builderMap.containsKey(professionID)) {
            builder = builderMap.get(professionID);
        } else {
            builder = Append.Builder.appender(professionID);
        }
        builder.addUnlock(Unlocks.BLOCK_BREAK_UNLOCK, unlockBuilder);
        builderMap.put(professionID, builder);
        return writeToFile(professionID, builder);
    }

    private static int tool(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation professionID = ResourceLocationArgument.getId(stack, "occupation");
        ResourceOrTagLocationArgument.Result<Item> registryType = ResourceOrTagLocationArgument.getRegistryType(stack, "items", Registry.ITEM_REGISTRY, new DynamicCommandExceptionType(o -> {
            return new LiteralMessage("it broke");
        }));
        int level = IntegerArgumentType.getInteger(stack, "level");
        ToolUnlock.Builder unlockBuilder = ToolUnlock.builder().level(level);
        registryType.unwrap().ifRight(unlockBuilder::tag);
        registryType.unwrap().ifLeft(itemResourceKey -> unlockBuilder.item(Registry.ITEM.get(itemResourceKey.location())));
        Append.Builder builder;
        if (builderMap.containsKey(professionID)) {
            builder = builderMap.get(professionID);
        } else {
            builder = Append.Builder.appender(professionID);
        }
        builder.addUnlock(Unlocks.TOOL_UNLOCK, unlockBuilder);
        builderMap.put(professionID, builder);
        return writeToFile(professionID, builder);
    }

    private static int advancement(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation professionID = ResourceLocationArgument.getId(stack, "occupation");
        ResourceOrTagLocationArgument.Result<Item> registryType = ResourceOrTagLocationArgument.getRegistryType(stack, "items", Registry.ITEM_REGISTRY, new DynamicCommandExceptionType(o -> {
            return new LiteralMessage("it broke");
        }));
        ResourceLocation advancement = ResourceLocationArgument.getId(stack, "advancement");
        AdvancementUnlock.Builder unlockBuilder = AdvancementUnlock.builder().id(advancement);
        registryType.unwrap().ifRight(unlockBuilder::tag);
        registryType.unwrap().ifLeft(itemResourceKey -> unlockBuilder.item(Registry.ITEM.get(itemResourceKey.location())));
        Append.Builder builder;
        if (builderMap.containsKey(professionID)) {
            builder = builderMap.get(professionID);
        } else {
            builder = Append.Builder.appender(professionID);
        }
        builder.addUnlock(Unlocks.ADVANCEMENT_UNLOCK, unlockBuilder);
        builderMap.put(professionID, builder);
        return writeToFile(professionID, builder);
    }

    private static int equipmentUnlock(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation professionID = ResourceLocationArgument.getId(stack, "occupation");
        ResourceOrTagLocationArgument.Result<Item> registryType = ResourceOrTagLocationArgument.getRegistryType(stack, "items", Registry.ITEM_REGISTRY, new DynamicCommandExceptionType(o -> {
            return new LiteralMessage("it broke");
        }));
        int level = IntegerArgumentType.getInteger(stack, "level");
        EquipmentUnlock.Builder unlockBuilder = EquipmentUnlock.builder().level(level);
        registryType.unwrap().ifRight(unlockBuilder::tag);
        registryType.unwrap().ifLeft(itemResourceKey -> unlockBuilder.item(Registry.ITEM.get(itemResourceKey.location())));
        Append.Builder builder;
        if (builderMap.containsKey(professionID)) {
            builder = builderMap.get(professionID);
        } else {
            builder = Append.Builder.appender(professionID);
        }
        builder.addUnlock(Unlocks.EQUIPMENT_UNLOCK, unlockBuilder);
        builderMap.put(professionID, builder);
        return writeToFile(professionID, builder);
    }

    private static int interactionUnlock(CommandContext<CommandSourceStack> stack) throws CommandSyntaxException {
        ResourceLocation professionID = ResourceLocationArgument.getId(stack, "occupation");
        ResourceOrTagLocationArgument.Result<Item> registryType = ResourceOrTagLocationArgument.getRegistryType(stack, "items", Registry.ITEM_REGISTRY, new DynamicCommandExceptionType(o -> {
            return new LiteralMessage("it broke");
        }));
        int level = IntegerArgumentType.getInteger(stack, "level");
        InteractionUnlock.Builder unlockBuilder = InteractionUnlock.builder().level(level);
        registryType.unwrap().ifRight(unlockBuilder::tag);
        registryType.unwrap().ifLeft(itemResourceKey -> unlockBuilder.item(Registry.ITEM.get(itemResourceKey.location())));
        Append.Builder builder;
        if (builderMap.containsKey(professionID)) {
            builder = builderMap.get(professionID);
        } else {
            builder = Append.Builder.appender(professionID);
        }
        builder.addUnlock(Unlocks.INTERACTION_UNLOCK, unlockBuilder);
        builderMap.put(professionID, builder);
        return writeToFile(professionID, builder);
    }

    private static int writeToFile(ResourceLocation professionID, Append.Builder builder) {
        return 1;
    }


}
