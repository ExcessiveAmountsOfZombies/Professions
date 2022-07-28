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
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

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

    public static final Format PLACE_BLOCK_FORMAT = register(modID("place_block"), new RegularFormat((embed, y, width) -> List.of(
            createItemArrayEntry(embed, y, width, "items"),
            createRewardArray(embed, y, width, "rewards"),
            new ArrayEntry<>(embed + 14, y, width - 14, "conditions", (x1, y2) -> {
                return new RegistryEntry<>(embed + 18, y2, width / 2, RegistryConstants.ACTION_CONDITION_TYPE, ActionConditions.FULLY_GROWN_CROP_CONDITION);
            })
    )));

    public static final Format BREAK_BLOCK_FORMAT = register(modID("break_block"), new RegularFormat((embed, y, width) -> List.of(
            createBlockArrayEntry(embed, y, width, "blocks"),
            createRewardArray(embed, y, width, "rewards")
    )));


    public static void init() {
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

    private static ArrayEntry<DatapackEntry> createRewardArray(int embed, int y, int width, String usage) {
        // todo: we should find a way to not have these values be magical,
        //  so they can be used in more contexts. not just for rewards, but for all of them.
        return createArrayEntry(embed + 14, y, width - 14, usage, (x1, y2) -> {
            return new CompoundAwareEntry<>(embed + 18, y, width / 2, embed, width, RegistryConstants.REWARD_KEY,
                    new RegistryEntry<>(embed + 18, y, width / 2, RegistryConstants.REWARDS, Rewards.EXPERIENCE_REWARD, Optional.of("reward"), DatapackEntry.Type.REMOVE));
        });
    }

}
