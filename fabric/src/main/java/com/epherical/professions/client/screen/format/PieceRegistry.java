package com.epherical.professions.client.screen.format;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.client.screen.entry.ArrayEntry;
import com.epherical.professions.client.screen.entry.CompoundAwareEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.NumberEntry;
import com.epherical.professions.client.screen.entry.RegistryEntry;
import com.epherical.professions.client.screen.entry.StringEntry;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.epherical.professions.Constants.modID;

public class PieceRegistry {

    public static final ResourceKey<Registry<Format>> PIECES_KEY = ResourceKey.createRegistryKey(Constants.modID("pieces"));
    public static final Registry<Format> PIECES = new MappedRegistry<>(PIECES_KEY, Lifecycle.experimental(), null);


    public static final Format PAYMENT_REWARD_FORMAT = register(modID("payment"), new RegularFormat((embed, y, width) -> List.of(
            new NumberEntry<>(embed + 22, y, width / 2, "amount", 1.0),
            new StringEntry(embed + 22, y, width / 2, "currency", ProfessionConfig.overriddenCurrencyID)
    )));

    public static final Format ITEM_REWARD_FORMAT = register(modID("item"), new RegularFormat((embed, y, width) -> List.of(
            new StringEntry(embed + 22, y, width / 2, "item", "minecraft:grass_block"),
            new NumberEntry<>(embed + 22, y, width / 2, "count", 1)
    )));

    public static final Format EXPERIENCE_REWARD_FORMAT = register(modID("occupation_exp"), new RegularFormat((embed, y, width) -> List.of(
            new NumberEntry<>(embed + 22, y, width / 2, "amount", 1.0)
    )));

    // TODO: maybe make all these resource locations constants that are paired with their appropriate actions.
    // block formats
    public static final Format PLACE_BLOCK_FORMAT = createBlockFormat(modID("place_block"), Lists.newArrayList());
    public static final Format BREAK_BLOCK_FORMAT = createBlockFormat(modID("break_block"), Lists.newArrayList());
    public static final Format TNT_DESTROY_FORMAT = createBlockFormat(modID("tnt_destroy"), Lists.newArrayList());
    // entity formats
    public static final Format KILL_ENTITY_FORMAT = createEntityFormat(modID("kill_entity"), Lists.newArrayList());
    public static final Format BREED_ENTITY_FORMAT = createEntityFormat(modID("breed"), Lists.newArrayList());
    public static final Format TAME_ENTITY_FORMAT = createEntityFormat(modID("tame"), Lists.newArrayList());
    // item formats
    public static final Format FISH_FORMAT = createItemFormat(modID("catch_fish"), Lists.newArrayList());
    public static final Format CRAFT_ITEM_FORMAT = createItemFormat(modID("craft_item"), Lists.newArrayList());
    public static final Format TAKE_COOKED_FORMAT = createItemFormat(modID("take_smelted_item"), Lists.newArrayList());
    public static final Format ON_ITEM_COOKED_FORMAT = createItemFormat(modID("on_item_smelted"), Lists.newArrayList());
    public static final Format BREW_ITEM_FORMAT = createItemFormat(modID("brew"), Lists.newArrayList());
    public static final Format ENCHANT_ITEM_FORMAT = createItemFormat(modID("enchant"), Lists.newArrayList(
            (embed, y, width) -> createEnchantArrayEntry(embed, y, width, "enchants")
    ));
    public static final Format TRADE_FORMAT = createItemFormat(modID("villager_trade"), Lists.newArrayList());


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

    private static <T extends DatapackEntry> ArrayEntry<T> createArrayEntry(int x, int y, int width, String usage, BiFunction<Integer, Integer, T> add) {
        return new ArrayEntry<>(x, y, width, usage, add);
    }

    private static ArrayEntry<DatapackEntry> createItemArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x + 14, y, width - 14, usage, (x1, y2) -> {
            return new StringEntry(x + 18, y2, width / 2, "items", "#minecraft:crops", Optional.of("items"), DatapackEntry.Type.REMOVE);
        });
    }

    private static ArrayEntry<DatapackEntry> createBlockArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x + 14, y, width - 14, usage, (x1, y2) -> {
            return new StringEntry(x + 18, y2, width / 2, "blocks", "#minecraft:crops", Optional.of("blocks"));
        });
    }

    private static ArrayEntry<DatapackEntry> createEntityArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x + 14, y, width - 14, usage, (x1, y2) -> {
            return new StringEntry(x + 18, y2, width / 2, "entities", "minecraft:cow", Optional.of("entities"));
        });
    }

    private static ArrayEntry<DatapackEntry> createEnchantArrayEntry(int x, int y, int width, String usage) {
        return createArrayEntry(x + 14, y, width - 14, usage, (x1, y2) -> {
            return new StringEntry(x + 18, y2, width / 2, "enchants", "minecraft:sharpness#2", Optional.of("enchants"));
        });
    }

    private static ArrayEntry<DatapackEntry> createRewardArray(int embed, int y, int width, String usage) {
        // todo: we should find a way to not have these values be magical,
        //  so they can be used in more contexts. not just for rewards, but for all of them.
        return createArrayEntry(embed + 14, y, width - 14, usage, (x1, y2) -> {
            return new CompoundAwareEntry<>(embed + 18, y, width / 2, embed, width, RegistryConstants.REWARD_KEY,
                    new RegistryEntry<>(embed + 18, y, width / 2, RegistryConstants.REWARDS, Rewards.EXPERIENCE_REWARD, Optional.of("reward"), DatapackEntry.Type.REMOVE));
        });
    }

    private static ArrayEntry<DatapackEntry> createConditionArray(int embed, int y, int width, String usage) {
        return createArrayEntry(embed + 14, y, width - 14, usage, (x1, y2) -> {
            return new CompoundAwareEntry<>(embed + 18, y, width / 2, embed, width, RegistryConstants.ACTION_CONDITION_KEY,
                    new RegistryEntry<>(embed + 18, y, width / 2, RegistryConstants.ACTION_CONDITION_TYPE,
                            ActionConditions.FULLY_GROWN_CROP_CONDITION, Optional.of("condition"), DatapackEntry.Type.REMOVE));
        });
    }

}
