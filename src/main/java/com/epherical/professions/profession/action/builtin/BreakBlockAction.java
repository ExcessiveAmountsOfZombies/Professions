package com.epherical.professions.profession.action.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.ProfessionActions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BreakBlockAction extends AbstractAction {
    private final List<Block> blocks;

    protected BreakBlockAction(ActionCondition[] conditions, Reward[] rewards, List<Block> blocks) {
        super(conditions, rewards);
        this.blocks = blocks;
    }

    @Override
    public ActionType getType() {
        return ProfessionActions.BREAK_BLOCK;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (Block block : blocks) {
            components.add(block.getName().setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(new TranslatableComponent(" (%s | %s & %s)",
                    map.get(Rewards.PAYMENT_REWARD),
                    map.get(Rewards.EXPERIENCE_REWARD),
                    extraRewardInformation(map))));
        }
        return components;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        BlockState state = professionContext.getPossibleParameter(ProfessionParameter.THIS_BLOCKSTATE);
        return state != null && blocks.contains(state.getBlock());
    }

    public static class Builder extends AbstractAction.Builder<BreakBlockAction.Builder> {
        private List<Block> blocks = new ArrayList<>();

        public Builder withBlock(Block block) {
            this.blocks.add(block);
            return this;
        }

        public Builder withBlocks(Block... blocks) {
            this.blocks.addAll(List.of(blocks));
            return this;
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new BreakBlockAction(this.getConditions(), this.getRewards(), blocks);
        }
    }

    public static class Serializer extends ActionSerializer<BreakBlockAction> {

        @Override
        public void serialize(@NotNull JsonObject json, @NotNull BreakBlockAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (Block block : value.blocks) {
                array.add(Registry.BLOCK.getKey(block).toString());
            }
            json.add("blocks", array);
        }

        @Override
        public BreakBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "blocks");
            List<Block> blocks = new ArrayList<>();
            for (JsonElement element : array) {
                String blockID = element.getAsString();
                blocks.add(Registry.BLOCK.get(new ResourceLocation(blockID)));
            }
            return new BreakBlockAction(conditions, rewards, blocks);
        }
    }


}
