package com.epherical.professions.profession.action.builtin;

import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.ProfessionActions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    public boolean action() {
        return false;
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
