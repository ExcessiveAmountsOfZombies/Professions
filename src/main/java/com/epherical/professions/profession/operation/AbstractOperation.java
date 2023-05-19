package com.epherical.professions.profession.operation;

import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.builtin.AbstractLevelUnlock;
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
import java.util.Objects;

public abstract class AbstractOperation<T> {

    List<Operator<Action<T>, List<ResourceLocation>>> actions;
    List<Operator<Unlock<T>, List<LevelRequirement>>> unlocks;

    private CompoundKey<T> compoundKey;

    public AbstractOperation(List<Operator<Action<T>, List<ResourceLocation>>> actions, List<Operator<Unlock<T>, List<LevelRequirement>>> unlocks) {
        this.unlocks = unlocks;
        this.actions = actions;
    }

    public void applyData(MinecraftServer server, Map<ResourceLocation, ProfessionBuilder> builderMap) {
        for (Operator<Action<T>, List<ResourceLocation>> action : actions) {
            for (ResourceLocation occupation : action.getOccupations()) {
                ProfessionBuilder professionBuilder = builderMap.get(occupation);
                if (professionBuilder != null) {
                    action.getOperator().addActionEntry(getActionEntryType(server, compoundKey.getRegistry(), compoundKey.getKey()));
                    // todo; assign this to the action.
                    professionBuilder.addAction(action.getOperator().getType(), action.getOperator());
                } else {
                    // todo; throw error and continue on.
                }
            }
        }
        for (Operator<Unlock<T>, List<LevelRequirement>> unlock : unlocks) {
            for (LevelRequirement occupation : unlock.getOccupations()) {
                ProfessionBuilder professionBuilder = builderMap.get(occupation.getOccupationKey());
                if (professionBuilder != null) {
                    unlock.getOperator().addActionEntry(getActionEntryType(server, compoundKey.getRegistry(), compoundKey.getKey()));
                    if (unlock.getOperator() instanceof AbstractLevelUnlock<T> abstractLevelUnlock) {
                        abstractLevelUnlock.setLevel(occupation.getLevel());
                    }
                    professionBuilder.addUnlock(unlock.getOperator().getType(), unlock.getOperator());
                } else {
                    // todo; error and continue
                }
                /*if (professionBuilder == null) {
                    throw new NullPointerException("Occupation does not exist in map. " + occupation.getOccupationKey());
                }*/

            }
        }
    }

    public void setActions(List<Operator<Action<T>, List<ResourceLocation>>> actions) {
        this.actions = actions;
    }

    public void setUnlocks(List<Operator<Unlock<T>, List<LevelRequirement>>> unlocks) {
        this.unlocks = unlocks;
    }

    public void addAction(Operator<Action<T>, List<ResourceLocation>> actionOperator) {
        this.actions.add(actionOperator);
    }

    public void addUnlock(Operator<Unlock<T>, List<LevelRequirement>> unlockOperator) {
        this.unlocks.add(unlockOperator);
    }

    public List<Operator<Action<T>, List<ResourceLocation>>> getActions() {
        return actions;
    }

    public List<Operator<Unlock<T>, List<LevelRequirement>>> getUnlocks() {
        return unlocks;
    }

    public void setKey(ResourceKey<Registry<T>> registry, ResourceLocation resourceLocation) {
        this.compoundKey = new CompoundKey<>(registry, resourceLocation);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractOperation<?> that = (AbstractOperation<?>) o;

        return Objects.equals(compoundKey, that.compoundKey);
    }

    @Override
    public int hashCode() {
        return compoundKey != null ? compoundKey.hashCode() : 0;
    }

    public abstract ActionEntry<T> getActionEntryType(MinecraftServer server, ResourceKey<Registry<T>> registry, ResourceLocation key);

    public abstract static class AbstractOperationSerializer<T extends AbstractOperation<V>, V> implements JsonSerializer<T>, JsonDeserializer<T> {

        public abstract String serializeType();

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            JsonArray actions = new JsonArray();


            if (!json.has("type")) {
                json.addProperty("type", serializeType());
            }
            for (Operator<Action<V>, List<ResourceLocation>> action : src.actions) {
                JsonObject object = new JsonObject();
                object.add("single_action", context.serialize(action.getOperator()));
                JsonArray occupations = new JsonArray();
                for (ResourceLocation profession : action.getOccupations()) {
                    occupations.add(profession.toString());
                }
                object.add("occupations", occupations);
                actions.add(object);
            }
            JsonArray unlocks = new JsonArray();
            for (Operator<Unlock<V>, List<LevelRequirement>> unlock : src.unlocks) {
                JsonObject object = context.serialize(unlock.getOperator()).getAsJsonObject();
                JsonArray occupations = new JsonArray();
                for (LevelRequirement occupation : unlock.getOccupations()) {
                    occupations.add(context.serialize(occupation));
                }
                object.add("occupations", occupations);
                unlocks.add(object);
            }
            json.add("actions", actions);
            json.add("unlocks", unlocks);
            return json;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray actions = json.getAsJsonObject().getAsJsonArray("actions");
            List<Operator<Action<V>, List<ResourceLocation>>> actionList = new ArrayList<>();
            for (JsonElement action : actions) {
                JsonObject singleAction = (JsonObject) action;
                Action<V> anAction = context.deserialize(GsonHelper.getAsJsonObject(singleAction, "single_action"), Action.class);
                String[] strOccupations = GsonHelper.getAsObject(singleAction, "occupations", context, String[].class);
                List<ResourceLocation> occupations = Arrays.stream(strOccupations).map(ResourceLocation::new).toList();
                actionList.add(new Operator<>(occupations, anAction));
            }
            JsonArray unlocks = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "unlocks", new JsonArray());
            List<LevelRequirement> levelRequirements = new ArrayList<>();
            List<Operator<Unlock<V>, List<LevelRequirement>>> unlockList = new ArrayList<>();
            for (JsonElement unlock : unlocks) {
                JsonObject singeUnlock = (JsonObject) unlock;
                Unlock<V> anUnlock = context.deserialize(singeUnlock, Unlock.class);
                levelRequirements.addAll(List.of(GsonHelper.getAsObject(singeUnlock, "occupations", context, LevelRequirement[].class)));
                unlockList.add(new Operator<>(levelRequirements, anUnlock));
            }
            return deserialize(actionList, unlockList);
        }

        public abstract T deserialize(List<Operator<Action<V>, List<ResourceLocation>>> actions, List<Operator<Unlock<V>, List<LevelRequirement>>> unlocks);

        /*@Override
        public ObjectOperation<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            return new ObjectOperation<>(list, List.of());
        }*/
    }

}
