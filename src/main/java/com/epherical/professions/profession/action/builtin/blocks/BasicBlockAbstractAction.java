package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicBlockAbstractAction extends AbstractAction {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final List<ActionEntry<Block>> blocks;
    @Nullable
    protected List<Block> realBlocks;

    protected BasicBlockAbstractAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blockList) {
        super(conditions, rewards);
        this.blocks = blockList;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (Block block : getRealBlocks()) {
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
        for (Block block : getRealBlocks()) {
            components.add(block.getName().setStyle(Style.EMPTY
                    .withColor(ProfessionConfig.descriptors)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, allRewardInformation()))));
        }
        return components;
    }

    protected List<Block> getRealBlocks() {
        if (realBlocks == null) {
            realBlocks = new ArrayList<>();
            for (ActionEntry<Block> block : blocks) {
                realBlocks.addAll(block.getActionValues(Registry.BLOCK));
            }
        }
        return realBlocks;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        BlockState state = professionContext.getPossibleParameter(ProfessionParameter.THIS_BLOCKSTATE);
        return state != null && getRealBlocks().contains(state.getBlock());
    }

    public abstract static class Builder<T extends BasicBlockAbstractAction.Builder<T>> extends AbstractAction.Builder<T> {
        protected final List<ActionEntry<Block>> blocks = new ArrayList<>();

        public BasicBlockAbstractAction.Builder<T> block(Block... item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public BasicBlockAbstractAction.Builder<T> block(TagKey<Block> block) {
            this.blocks.add(ActionEntry.of(block));
            return this;
        }
    }

    public abstract static class Serializer<T extends BasicBlockAbstractAction> extends ActionSerializer<T> {

        @Override
        public void serialize(@NotNull JsonObject json, T value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (ActionEntry<Block> block : value.blocks) {
                array.addAll(block.serialize(Registry.BLOCK));
            }
            json.add("blocks", array);
        }

        public List<ActionEntry<Block>> deserializeBlocks(JsonObject object) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "blocks");
            List<ActionEntry<Block>> blocks = new ArrayList<>();
            for (JsonElement element : array) {
                String blockID = element.getAsString();
                if (blockID.startsWith("#")) {
                    TagKey<Block> blockTagKey = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(blockID.substring(1)));
                    // TODO: first load wont have any tags loaded, so we'll have to fix this somehow.
                    blocks.add(ActionEntry.of(blockTagKey));
                } else {
                    Registry.BLOCK.getOptional(new ResourceLocation(blockID)).ifPresentOrElse(
                            block -> blocks.add(ActionEntry.of(block)),
                            () -> LOGGER.warn("Attempted to add unknown block {}. Was not added, but will continue processing the list.", blockID));
                }
            }
            return blocks;
        }
    }
}
