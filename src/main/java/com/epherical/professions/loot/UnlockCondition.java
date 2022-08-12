package com.epherical.professions.loot;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.Constants;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
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

import java.util.List;


public class UnlockCondition implements LootItemCondition {

    @Override
    public LootItemConditionType getType() {
        return Constants.UNLOCK_CONDITION;
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if ((entity instanceof ServerPlayer serverPlayer)) {
            ProfessionalPlayer player = CommonPlatform.platform.getPlayerManager().getPlayer(serverPlayer.getUUID());
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
            // todo: translations
            MutableComponent error = new TextComponent("=-=-=-= Level Requirements =-=-=-=");
            if (blockBroken != null) {
                List<Unlock.Singular<Block>> lockedKnowledge = player.getLockedKnowledge(Unlocks.BLOCK_DROP_UNLOCK, blockBroken.getBlock());
                for (Unlock.Singular<Block> singular : lockedKnowledge) {
                    if (!singular.canUse(player)) {
                        canDropBlock = false;
                        error.append("\n");
                        error.append(singular.createUnlockComponent());
                    }
                }
            }
            if (!(canUseTool && canDropEntity && canDropBlock)) {
                Component hover = new TextComponent("Hover to see which occupations prevented the drop.")
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)
                                        .withUnderlined(true)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error)));
                serverPlayer.sendMessage(new TranslatableComponent("Drops for this action are not unlocked yet. %s", hover)
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
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
