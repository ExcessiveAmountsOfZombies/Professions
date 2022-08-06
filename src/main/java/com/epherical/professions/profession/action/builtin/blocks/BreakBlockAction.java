package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.client.entry.ArrayEntry;
import com.epherical.professions.client.entry.CompoundAwareEntry;
import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.client.entry.RegistryEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.client.format.Format;
import com.epherical.professions.client.format.FormatBuilder;
import com.epherical.professions.client.format.RegularFormat;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

import static com.epherical.professions.RegistryConstants.REWARDS;
import static com.epherical.professions.RegistryConstants.REWARD_KEY;

public class BreakBlockAction extends BlockAbstractAction {

    protected BreakBlockAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blocks) {
        super(conditions, rewards, blocks);
    }

    @Override
    public ActionType getType() {
        return Actions.BREAK_BLOCK;
    }

    public static Builder breakBlock() {
        return new Builder();
    }

    public static class Serializer extends BlockAbstractAction.Serializer<BreakBlockAction> {

        @Override
        public BreakBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new BreakBlockAction(conditions, rewards, deserializeBlocks(object));
        }
    }

    public static class Builder extends BlockAbstractAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new BreakBlockAction(this.getConditions(), this.getRewards(), blocks);
        }
    }

    public static class DatapackBuilder implements FormatBuilder<BreakBlockAction> {

        @Override
        public Format<BreakBlockAction> deserializeToFormat(BreakBlockAction breakBlockAction) {
            RegularFormat<BreakBlockAction> format = new RegularFormat<>((embed, y, width) -> List.of(
                    new ArrayEntry<BreakBlockAction, StringEntry<String>>(embed, y, width, "blocks", (x1, y2, wid) -> {
                        return new StringEntry<>(x1, y2, wid, "blocks", "minecraft:stone", Optional.of("blocks"), (o, entry) -> {
                            entry.setValue(o);
                        }, DatapackEntry.Type.REMOVE);
                    }, (o, entry) -> {
                        for (ActionEntry<Block> block : o.blocks) {
                            for (String s : block.serializeString(Registry.BLOCK)) {
                                StringEntry<String> entry1 = entry.createEntry();
                                entry1.deserialize(s);
                                entry.addEntry(entry1);
                            }
                        }
                    }),
                    new ArrayEntry<BreakBlockAction, CompoundAwareEntry<Reward, RewardType>>(embed, y, width, "rewards", (x1, y2, wid) -> {
                        return new CompoundAwareEntry<>(embed, y, width / 2, x1, wid, REWARD_KEY,
                                new RegistryEntry<>(x1, y, wid / 2, REWARDS, Rewards.EXPERIENCE_REWARD, Optional.of("reward"),
                                        (reward, entry) -> entry.setValue(reward.getType()), DatapackEntry.Type.REMOVE),
                                (reward, entry) -> entry.getEntry().deserialize(reward));
                    }, (o, entry) -> {
                        for (Reward reward : o.rewards) {
                            CompoundAwareEntry<Reward, RewardType> entry1 = entry.createEntry();
                            entry1.deserialize(reward);
                            entry.addEntry(entry1);
                        }
                    })/*,
                    new ArrayEntry<>(embed, y, width, "conditions", (x1, y2, wid) -> {
                        return new CompoundAwareEntry<>(embed, y, width / 2, x1, wid, ACTION_CONDITION_KEY,
                                new RegistryEntry<>(x1, y, wid / 2, ACTION_CONDITION_TYPE,
                                        ActionConditions.FULLY_GROWN_CROP_CONDITION, Optional.of("condition"), DatapackEntry.Type.REMOVE));
                    })*/
            ));

            return format;
        }
    }


}
