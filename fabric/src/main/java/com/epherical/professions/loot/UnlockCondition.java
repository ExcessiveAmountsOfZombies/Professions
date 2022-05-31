package com.epherical.professions.loot;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.profession.unlock.Unlocks;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import static com.epherical.professions.util.Tristate.UNKNOWN;


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
                canDropBlock = canUse(player, Unlocks.BLOCK_UNLOCK, blockBroken.getBlock());
            }
            return canUseTool && canDropEntity && canDropBlock;
        }
        return true;
    }

    private <T> boolean canUse(ProfessionalPlayer player, UnlockType<T> type, T object) {
        UnlockableData data = player.getUnlockableData(type, object);
        if (data == null || data.canUse(object) == UNKNOWN) {
            return true;
        }
        return data.canUse(object).valid();
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
        public void serialize(JsonObject json, UnlockCondition value, JsonSerializationContext serializationContext) {}

        @Override
        public UnlockCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return new UnlockCondition();
        }
    }
}
