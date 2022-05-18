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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BlockAbstactAction extends AbstractAction {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final List<ActionEntry<Block>> blocks;
    @Nullable
    protected List<Block> realBlocks;

    protected static final Cache<BlockPos, Instant> cache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .maximumSize(5000)
            .build();

    protected BlockAbstactAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blockList) {
        super(conditions, rewards);
        this.blocks = blockList;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (Block block : getRealBlocks()) {
            components.add(block.getName().setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)).append(new TranslatableComponent(" (%s | %s%s)",
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
        logAction(professionContext, state != null ? state.getBlock().getName() : Component.nullToEmpty(""));
        return state != null && getRealBlocks().contains(state.getBlock());
    }

    @Override
    public boolean internalCondition(ProfessionContext context) {
        Instant placementTime = cache.getIfPresent(context.getParameter(ProfessionParameter.BLOCKPOS));
        if (placementTime != null) {
            Instant now = Instant.now();
            if (now.isAfter(placementTime)) {
                cache.cleanUp();
                return true;
            } else {
                long seconds = Duration.between(now, placementTime).get(ChronoUnit.SECONDS);
                sendCooldownMessage(context.getParameter(ProfessionParameter.THIS_PLAYER).getPlayer(), seconds);
                return false;
            }
        }
        return true;
    }

    private static void sendCooldownMessage(ServerPlayer player, long seconds) {
        if (seconds < 0) {
            seconds = 0;
        }
        // need bright colors in the action bar for whatever reason, otherwise would use variables/errors
        MutableComponent message = new TranslatableComponent("professions.block.cooldown",
                new TextComponent(String.valueOf(seconds)).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
        player.sendMessage(message, ChatType.GAME_INFO, Util.NIL_UUID);
    }

    public abstract static class Builder<T extends Builder<T>> extends AbstractAction.Builder<T> {
        protected final List<ActionEntry<Block>> blocks = new ArrayList<>();

        public Builder<T> block(Block... item) {
            this.blocks.add(ActionEntry.of(item));
            return this;
        }

        public Builder<T> block(TagKey<Block> block) {
            this.blocks.add(ActionEntry.of(block));
            return this;
        }
    }

    public abstract static class Serializer<T extends BlockAbstactAction> extends ActionSerializer<T> {

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
