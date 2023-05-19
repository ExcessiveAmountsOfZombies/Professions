package com.epherical.professions.profession.operation;

import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.ActionEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class ObjectOperation<T> extends AbstractOperation<T> {


    public ObjectOperation(List<Operator<Action<T>, List<ResourceLocation>>> actions, List<Operator<Unlock<T>, List<LevelRequirement>>> unlocks) {
        super(actions, unlocks);
    }

    @Override
    public ActionEntry<T> getActionEntryType(MinecraftServer server, ResourceKey<Registry<T>> registry, ResourceLocation key) {
        return ActionEntry.of(ResourceKey.create(registry, key));
    }


    /*public void applyData(MinecraftServer server, Map<ResourceLocation, ProfessionBuilder> builderMap) {
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
    }*/

    public static class OperationSerializer<V> extends AbstractOperationSerializer<ObjectOperation<V>, V> {
        @Override
        public ObjectOperation<V> deserialize(List<Operator<Action<V>, List<ResourceLocation>>> actions, List<Operator<Unlock<V>, List<LevelRequirement>>> unlocks) {
            return new ObjectOperation<>(actions, unlocks);
        }
    }

    /*public static class OperationSerializer<T> implements JsonSerializer<ObjectOperation<T>>, JsonDeserializer<ObjectOperation<T>> {

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
    }*/
}
