package com.epherical.professions.profession.operation;

import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ObjectOperation<T> {

    private List<ProfessionAction<T>> actions;
    private List<Unlock<T>> unlocks;

    private ResourceKey<? extends Registry<?>> resourceKey;

    public ObjectOperation(List<ProfessionAction<T>> actions, List<Unlock<T>> unlocks) {
        this.unlocks = unlocks;
        this.actions = actions;
    }

    public void setKey(ResourceKey<? extends Registry<?>> object) {
        resourceKey = object;
    }


    public void applyData(MinecraftServer server, Map<ResourceLocation, ProfessionBuilder> builderMap) {
        for (ProfessionAction<T> action : actions) {
            for (ResourceLocation occupation : action.getOccupations()) {
                ProfessionBuilder professionBuilder = builderMap.get(occupation);
                if (professionBuilder != null) {
                    action.getAction().addActionEntry((ActionEntry<T>) ActionEntry.of(resourceKey));
                    // todo; assign this to the action.
                    professionBuilder.addAction(action.getAction().getType(), action.getAction());
                    for (Unlock<T> unlock : unlocks) {
                        professionBuilder.addUnlock(unlock.getType(), unlock);
                    }
                } else {
                    // todo; throw error and continue on.
                }
            }
        }

    }


    public static class OperationSerializer<T> implements JsonSerializer<ObjectOperation<T>>, JsonDeserializer<ObjectOperation<T>> {

        @Override
        public JsonElement serialize(ObjectOperation<T> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();

            for (ProfessionAction<T> action : src.actions) {
                JsonObject object = new JsonObject();
                object.add("single_action", context.serialize(action.getAction()));
                JsonArray occupations = new JsonArray();
                for (ResourceLocation profession : action.getOccupations()) {
                    occupations.add(profession.toString());
                }
                object.add("occupations", occupations);
                array.add(object);
            }
            json.add("actions", array);
            return json;
        }

        @Override
        public ObjectOperation<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray actions = json.getAsJsonObject().getAsJsonArray("actions");
            List<ProfessionAction<T>> list = new ArrayList<>();
            for (JsonElement action : actions) {
                JsonObject singleAction = (JsonObject) action;
                Action<T> anAction = context.deserialize(GsonHelper.getAsJsonObject(singleAction, "single_action"), Action.class);
                String[] strOccupations = GsonHelper.getAsObject(singleAction, "occupations", context, String[].class);
                List<ResourceLocation> occupations = Arrays.stream(strOccupations).map(ResourceLocation::new).toList();
                list.add(new ProfessionAction<>(occupations, anAction));
            }
            return new ObjectOperation<>(list, List.of());
        }
    }
}
