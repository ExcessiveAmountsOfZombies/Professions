package com.epherical.professions.loot;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ProfessionUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;


public class UnlockCondition implements LootItemCondition {

    @Override
    public LootItemConditionType getType() {
        return ProfessionsFabric.UNLOCK_CONDITION;
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if ((entity instanceof ServerPlayer serverPlayer)) {
            ProfessionalPlayer player = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(serverPlayer.getUUID());
            if (player == null || player.getActiveOccupations().size() == 0) {
                return true; // don't affect the loot if it's not relevant
            }
            MutableComponent msg = new TextComponent("");
            int level = 0;
            boolean canUseTool = true, canDropEntity = true, canDropBlock = true;
            /*ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
            if (tool != null && !tool.isEmpty()) {
                canUseTool = canUse(player, tool.getItem());
            }

            Entity killedEntity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
            if (killedEntity != null) {
                canDropEntity = canUse(player, killedEntity);
            }*/

            BlockState blockBroken = context.getParamOrNull(LootContextParams.BLOCK_STATE);
            if (blockBroken != null) {
                Pair<Unlock.Singular<Block>, Boolean> pair = ProfessionUtil.canUse(player, Unlocks.BLOCK_UNLOCK, blockBroken.getBlock());
                if (pair.getFirst() != null) {
                    level = pair.getFirst().getUnlockLevel();
                }
                canDropBlock = pair.getSecond();
                msg = blockBroken.getBlock().getName();
            }
            if (!(canUseTool && canDropEntity && canDropBlock)) {
                serverPlayer.sendMessage(new TranslatableComponent("You must be level %s before you can receive drops from %s.",
                        new TextComponent(String.valueOf(level)).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)),
                        msg.setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))
                ).setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
                return false;
            }
        }
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements LootItemCondition.Builder {

        @Override
        public LootItemCondition build() {
            return new UnlockCondition();
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<UnlockCondition> {

        @Override
        public void serialize(JsonObject json, UnlockCondition value, JsonSerializationContext serializationContext) {
        }

        @Override
        public UnlockCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return new UnlockCondition();
        }
    }
}
