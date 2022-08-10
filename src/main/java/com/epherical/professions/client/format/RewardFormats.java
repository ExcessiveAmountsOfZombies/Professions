package com.epherical.professions.client.format;

import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.profession.rewards.builtin.ItemReward;
import com.epherical.professions.profession.rewards.builtin.OccupationExperience;
import net.minecraft.core.Registry;

public class RewardFormats {


    public static class OccupationExp implements FormatBuilder<OccupationExperience> {

        @Override
        public Format<OccupationExperience> deserializeToFormat(OccupationExperience occupationExp) {
            return new RegularFormat<>((embed, y, width) -> new FormatEntryBuilder<OccupationExperience>()
                    .addEntry(new NumberEntry<>(embed, y, width, "amount", 1.0, (o, entry) -> {
                        entry.setValue(String.valueOf(o.expAmount()));
                    })));
        }
    }

    public static class Item implements FormatBuilder<ItemReward> {

        @Override
        public Format<ItemReward> deserializeToFormat(ItemReward reward) {
            return new RegularFormat<>((embed, y, width) -> new FormatEntryBuilder<ItemReward>()
                    .addEntry(new NumberEntry<>(embed, y, width, "count", 1, (itemReward, entry) -> {
                        entry.setValue(String.valueOf(itemReward.count()));
                    }))
                    .addEntry(new StringEntry<>(embed, y, width, "item", "minecraft:stone_sword", (itemReward, entry) -> {
                        entry.setValue(Registry.ITEM.getKey(itemReward.item()).toString());
                    })));
        }
    }


}
