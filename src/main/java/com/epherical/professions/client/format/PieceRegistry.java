package com.epherical.professions.client.format;

import com.epherical.professions.Constants;
import com.epherical.professions.client.entry.ArrayEntry;
import com.epherical.professions.client.entry.CompoundAwareEntry;
import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.RegistryEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.common.collect.Lists;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epherical.professions.RegistryConstants.*;

public class PieceRegistry {

    public static final ResourceKey<Registry<Format>> PIECES_KEY = ResourceKey.createRegistryKey(Constants.modID("pieces"));
    public static final Registry<Format> PIECES = new MappedRegistry<>(PIECES_KEY, Lifecycle.experimental(), null);

    public static final Format BLOCK_DROP_UNLOCK_FORMAT = register(formatID(UNLOCK_KEY, "block_drop"), new RegularFormat((embed, y, width) -> List.of(
            createBlockArrayEntry(embed, y, width / 2, "blocks"),
            new NumberEntry<>(embed, y, width / 2, "level", 1)
    )));

    public static final Format BLOCK_BREAK_UNLOCK_FORMAT = register(formatID(UNLOCK_KEY, "block_break"), new RegularFormat((embed, y, width) -> List.of(
            createBlockArrayEntry(embed, y, width / 2, "blocks"),
            new NumberEntry<>(embed, y, width / 2, "level", 1)
    )));

    public static final Format TOOL_UNLOCK_FORMAT = register(formatID(UNLOCK_KEY, "tool_unlock"), new RegularFormat((embed, y, width) -> List.of(
            createItemArrayEntry(embed, y, width / 2, "items"),
            new NumberEntry<>(embed, y, width / 2, "level", 1)
    )));


    public static final Format PAYMENT_REWARD_FORMAT = register(formatID(REWARD_KEY, "payment"), new RegularFormat((embed, y, width) -> List.of(
            new NumberEntry<>(embed, y, width / 2, "amount", 1.0),
            new StringEntry(embed, y, width / 2, "currency", ProfessionConfig.overriddenCurrencyID)
    )));

    public static final Format ITEM_REWARD_FORMAT = register(formatID(REWARD_KEY, "item"), new RegularFormat((embed, y, width) -> List.of(
            new StringEntry(embed, y, width / 2, "item", "minecraft:grass_block"),
            new NumberEntry<>(embed, y, width / 2, "count", 1)
    )));

    public static final Format EXPERIENCE_REWARD_FORMAT = register(formatID(REWARD_KEY, "occupation_exp"), new RegularFormat((embed, y, width) -> List.of(
            new NumberEntry<>(embed, y, width / 2, "amount", 1.0)
    )));

    // TODO: maybe make all these resource locations constants that are paired with their appropriate actions.
    // block formats
    public static final Format PLACE_BLOCK_FORMAT = createBlockFormat(formatID(ACTION_TYPE_KEY, "place_block"), Lists.newArrayList());
    public static final Format BREAK_BLOCK_FORMAT = createBlockFormat(formatID(ACTION_TYPE_KEY, "break_block"), Lists.newArrayList());
    public static final Format TNT_DESTROY_FORMAT = createBlockFormat(formatID(ACTION_TYPE_KEY, "tnt_destroy"), Lists.newArrayList());
    // entity formats
    public static final Format KILL_ENTITY_FORMAT = createEntityFormat(formatID(ACTION_TYPE_KEY, "kill_entity"), Lists.newArrayList());
    public static final Format BREED_ENTITY_FORMAT = createEntityFormat(formatID(ACTION_TYPE_KEY, "breed"), Lists.newArrayList());
    public static final Format TAME_ENTITY_FORMAT = createEntityFormat(formatID(ACTION_TYPE_KEY, "tame"), Lists.newArrayList());
    // item formats
    public static final Format FISH_FORMAT = createItemFormat(formatID(ACTION_TYPE_KEY, "catch_fish"), Lists.newArrayList());
    public static final Format CRAFT_ITEM_FORMAT = createItemFormat(formatID(ACTION_TYPE_KEY, "craft_item"), Lists.newArrayList());
    public static final Format TAKE_COOKED_FORMAT = createItemFormat(formatID(ACTION_TYPE_KEY, "take_smelted_item"), Lists.newArrayList());
    public static final Format ON_ITEM_COOKED_FORMAT = createItemFormat(formatID(ACTION_TYPE_KEY, "on_item_smelted"), Lists.newArrayList());
    public static final Format BREW_ITEM_FORMAT = createItemFormat(formatID(ACTION_TYPE_KEY, "brew"), Lists.newArrayList());
    public static final Format ENCHANT_ITEM_FORMAT = createItemFormat(formatID(ACTION_TYPE_KEY, "enchant"), Lists.newArrayList(
            (embed, y, width) -> createEnchantArrayEntry(embed, y, width, "enchants")
    ));
    public static final Format TRADE_FORMAT = createItemFormat(formatID(ACTION_TYPE_KEY, "villager_trade"), Lists.newArrayList());


    public static <T> ResourceLocation formatID(ResourceKey<T> registryKey, String id) {
        return formatID(registryKey, "professions", id);
    }

    public static <T> ResourceLocation formatID(ResourceKey<T> registryKey, String modID, String name) {
        return formatID(registryKey.location().toString().replaceAll(":", "_"), modID, name);
    }

    public static ResourceLocation formatID(String registryKey, String modID, String name) {
        return new ResourceLocation(modID, registryKey + "_" + name);
    }

    @Nullable
    public static <T> Format grabFormat(Registry<T> registry, T value) {
        ResourceLocation valueID = registry.getKey(value);
        if (valueID != null) {
            ResourceLocation formatLocation = formatID(registry.key(), valueID.getNamespace(), valueID.getPath());
            return PIECES.get(formatLocation);
        }
        return null;
    }

    public static void init() {
    }

    // todo; change the other lists into trifunctions as well
    private static Format createItemFormat(ResourceLocation modID, List<TriFunction<Integer, Integer, Integer, DatapackEntry>> entries) {
        return register(modID, new RegularFormat((embed, y, width) -> {
            List<DatapackEntry> copy = entries.stream().map(function -> function.apply(embed, y, width)).collect(Collectors.toList());
            copy.addAll(List.of(
                    createItemArrayEntry(embed, y, width, "items"),
                    createRewardArray(embed, y, width, "rewards"),
                    createConditionArray(embed, y, width, "conditions")
            ));
            return copy;
        }));
    }

    private static Format createBlockFormat(ResourceLocation modID, List<DatapackEntry> entries) {
        return register(modID, new RegularFormat((embed, y, width) -> {
            List<DatapackEntry> copy = new ArrayList<>(entries);
            copy.addAll(List.of(
                    createBlockArrayEntry(embed, y, width, "blocks"),
                    createRewardArray(embed, y, width, "rewards"),
                    createConditionArray(embed, y, width, "conditions")
            ));
            return copy;
        }));
    }

    private static Format createEntityFormat(ResourceLocation modID, List<DatapackEntry> entries) {
        return register(modID, new RegularFormat((embed, y, width) -> {
            List<DatapackEntry> copy = new ArrayList<>(entries);
            copy.addAll(List.of(
                    createEntityArrayEntry(embed, y, width, "entities"),
                    createRewardArray(embed, y, width, "rewards"),
                    createConditionArray(embed, y, width, "conditions")
            ));
            return copy;
        }));
    }

    private static Format register(ResourceLocation id, Format format) {
        return Registry.register(PIECES, id, format);
    }

    private static <T extends DatapackEntry> ArrayEntry<T> createArrayEntry(int x, int y, int width, String usage, TriFunction<Integer, Integer, Integer, T> add) {
        return new ArrayEntry<>(x, y, width, usage, add);
    }

    private static ArrayEntry<DatapackEntry> createItemArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x, y, width, usage, (x1, y2, wid) -> {
            return new StringEntry(x1, y2, wid, "items", "minecraft:stone_sword", Optional.of("items"), DatapackEntry.Type.REMOVE);
        });
    }

    private static ArrayEntry<DatapackEntry> createBlockArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x, y, width, usage, (x1, y2, wid) -> {
            return new StringEntry(x1, y2, wid, "blocks", "minecraft:stone", Optional.of("blocks"));
        });
    }

    private static ArrayEntry<DatapackEntry> createEntityArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x, y, width, usage, (x1, y2, wid) -> {
            return new StringEntry(x1, y2, wid, "entities", "minecraft:cow", Optional.of("entities"));
        });
    }

    private static ArrayEntry<DatapackEntry> createEnchantArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x, y, width, usage, (x1, y2, wid) -> {
            return new StringEntry(x1, y2, wid, "enchants", "minecraft:sharpness#2", Optional.of("enchants"));
        });
    }

    private static ArrayEntry<DatapackEntry> createRewardArray(int embed, int y, int width, String usage) {
        return createArrayEntry(embed, y, width, usage, (x1, y2, wid) -> {
            return new CompoundAwareEntry<>(embed, y, width / 2, x1, wid, REWARD_KEY,
                    new RegistryEntry<>(x1, y, wid / 2, REWARDS, Rewards.EXPERIENCE_REWARD, Optional.of("reward"), DatapackEntry.Type.REMOVE));
        });
    }

    private static ArrayEntry<DatapackEntry> createConditionArray(int embed, int y, int width, String usage) {
        return createArrayEntry(embed, y, width, usage, (x1, y2, wid) -> {
            return new CompoundAwareEntry<>(embed, y, width / 2, x1, wid, ACTION_CONDITION_KEY,
                    new RegistryEntry<>(x1, y, wid / 2, ACTION_CONDITION_TYPE,
                            ActionConditions.FULLY_GROWN_CROP_CONDITION, Optional.of("condition"), DatapackEntry.Type.REMOVE));
        });
    }

}
