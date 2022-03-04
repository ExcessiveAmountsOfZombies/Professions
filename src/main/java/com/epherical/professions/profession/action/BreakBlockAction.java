package com.epherical.professions.profession.action;

import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class BreakBlockAction extends AbstractAction {
    private static final Logger LOGGER = LogUtils.getLogger();
    private List<Block> blocks;


    protected BreakBlockAction(ActionCondition[] conditions, Reward[] rewards) {
        super(conditions, rewards);
    }

    @Override
    public ActionType getType() {
        return ProfessionActions.BREAK_BLOCK;
    }


    public static class Serializer extends ActionSerializer<BreakBlockAction> {

        @Override
        public void serialize(@NotNull JsonObject json, @NotNull BreakBlockAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);

        }

        @Override
        public BreakBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return null;
        }
    }


}
