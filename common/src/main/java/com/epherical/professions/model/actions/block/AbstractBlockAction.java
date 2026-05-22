package com.epherical.professions.model.actions.block;

import com.epherical.professions.model.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public abstract class AbstractBlockAction extends Action<Block> {
    private static final Duration BLOCK_ACTION_COOLDOWN = Duration.ofSeconds(30);
    private static final Cache<CachedBlockPosition, Instant> BLOCK_ACTION_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(BLOCK_ACTION_COOLDOWN)
            .maximumSize(5000)
            .build();

    protected AbstractBlockAction(Common common, List<Either<TagKey<Block>, ResourceKey<Block>>> targets) {
        super(common, targets);
    }

    @Override
    public ResourceKey<? extends Registry<Block>> getRegistryKey() {
        return Registries.BLOCK;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState blockState = context.getPossibleParameter(ProfessionParameter.THIS_BLOCK);
        if (blockState == null) {
            return false;
        }

        if (!matchesTargetBlock(blockState)) {
            return false;
        }

        BlockPos blockPos = context.getPossibleParameter(ProfessionParameter.BLOCKPOS);
        if (blockPos == null) {
            return true;
        }

        CachedBlockPosition cacheKey = new CachedBlockPosition(context.level().dimension(), blockPos.immutable());
        Instant now = Instant.now();
        Instant cooldownEndsAt = BLOCK_ACTION_CACHE.getIfPresent(cacheKey);
        if (cooldownEndsAt != null && now.isBefore(cooldownEndsAt)) {
            sendCooldownMessage(context, now, cooldownEndsAt);
            return false;
        }

        BLOCK_ACTION_CACHE.put(cacheKey, now.plus(BLOCK_ACTION_COOLDOWN));
        return true;
    }

    private void sendCooldownMessage(ProfessionContext context, Instant now, Instant cooldownEndsAt) {
        if (!(context.getParameter(ProfessionParameter.THIS_PLAYER).getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        long seconds = Math.max(0L, Duration.between(now, cooldownEndsAt).toSeconds());
        player.sendSystemMessage(Component.translatable("professions.block.cooldown", seconds), true);
    }

    private boolean matchesTargetBlock(BlockState blockState) {
        for (Either<TagKey<Block>, ResourceKey<Block>> value : getValues()) {
            if (value.left().isPresent() && blockState.getBlockHolder().is(value.left().get())) {
                return true;
            }
            if (value.right().isPresent() && blockState.getBlockHolder().is(value.right().get())) {
                return true;
            }
        }

        return false;
    }

    private record CachedBlockPosition(ResourceKey<Level> dimension, BlockPos pos) {}
}
