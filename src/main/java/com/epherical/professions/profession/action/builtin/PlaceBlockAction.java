package com.epherical.professions.profession.action.builtin;

import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class PlaceBlockAction extends BasicBlockAbstractAction {


    protected PlaceBlockAction(ActionCondition[] conditions, Reward[] rewards, List<Block> blocks) {
        super(conditions, rewards, blocks);
    }

    @Override
    public ActionType getType() {
        return Actions.PLACE_BLOCK;
    }

    public static class Serializer extends BasicBlockAbstractAction.Serializer<PlaceBlockAction> {

        @Override
        public PlaceBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "blocks");
            List<Block> blocks = new ArrayList<>();
            for (JsonElement element : array) {
                String blockID = element.getAsString();
                blocks.add(Registry.BLOCK.get(new ResourceLocation(blockID)));
            }
            return new PlaceBlockAction(conditions, rewards, blocks);
        }
    }
}
