package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
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

public abstract class BasicBlockAbstractAction extends AbstractAction {
    protected final List<Block> blocks;

    protected BasicBlockAbstractAction(ActionCondition[] conditions, Reward[] rewards, List<Block> blockList) {
        super(conditions, rewards);
        this.blocks = blockList;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (Block block : blocks) {
            components.add(block.getName().setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)).append(new TranslatableComponent(" (%s | %s & %s)",
                    map.get(Rewards.PAYMENT_REWARD),
                    map.get(Rewards.EXPERIENCE_REWARD),
                    extraRewardInformation(map))));
        }
        return components;
    }

    @Override
    public List<Component> clientFriendlyInformation() {
        List<Component> components = new ArrayList<>();
        for (Block block : blocks) {
            components.add(block.getName().setStyle(Style.EMPTY
                    .withColor(ProfessionConfig.descriptors)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, allRewardInformation()))));
        }
        return components;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        BlockState state = professionContext.getPossibleParameter(ProfessionParameter.THIS_BLOCKSTATE);
        return state != null && blocks.contains(state.getBlock());
    }

    public abstract static class Builder<T extends BasicBlockAbstractAction.Builder<T>> extends AbstractAction.Builder<T> {
        protected final List<Block> blocks = new ArrayList<>();

        public BasicBlockAbstractAction.Builder<T> block(Block... item) {
            this.blocks.addAll(List.of(item));
            return this;
        }
    }

    public abstract static class Serializer<T extends BasicBlockAbstractAction> extends ActionSerializer<T> {

        @Override
        public void serialize(@NotNull JsonObject json, T value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (Block block : value.blocks) {
                array.add(Registry.BLOCK.getKey(block).toString());
            }
            json.add("blocks", array);
        }

        public List<Block> deserializeBlocks(JsonObject object) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "blocks");
            List<Block> blocks = new ArrayList<>();
            for (JsonElement element : array) {
                String blockID = element.getAsString();
                blocks.add(Registry.BLOCK.get(new ResourceLocation(blockID)));
            }
            return blocks;
        }
    }
}
