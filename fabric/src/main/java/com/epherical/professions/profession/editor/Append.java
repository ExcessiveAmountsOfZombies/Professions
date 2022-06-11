package com.epherical.professions.profession.editor;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.ProfessionEditorSerializer;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * An editor that adds additional actions onto an already existing profession.
 * Currently only supports adding additional {@link Action actions} onto the profession.
 */
public class Append implements Editor {

    private final ResourceLocation editKey;
    protected final Map<ActionType, Collection<Action>> actions;
    protected final Map<UnlockType<?>, Collection<Unlock<?>>> unlocks;
    private ResourceLocation editLocation;


    public Append(ResourceLocation editKey, Map<ActionType, Collection<Action>> actions, Map<UnlockType<?>, Collection<Unlock<?>>> unlocks) {
        this.editKey = editKey;
        this.actions = actions;
        this.unlocks = unlocks;
    }


    @Override
    public ResourceLocation getProfessionKey() {
        return editKey;
    }

    @Override
    public void setLocation(ResourceLocation location) {
        this.editLocation = location;
    }

    @Override
    public ResourceLocation getLocationOfEdit() {
        return editLocation;
    }

    @Override
    public void applyEdit(ProfessionBuilder builder) {
        for (Map.Entry<ActionType, Collection<Action>> entry : actions.entrySet()) {
            for (Action action : entry.getValue()) {
                builder.addAction(entry.getKey(), action);
            }
        }
        for (Map.Entry<UnlockType<?>, Collection<Unlock<?>>> entry : unlocks.entrySet()) {
            for (Unlock<?> unlock : entry.getValue()) {
                builder.addUnlock(entry.getKey(), unlock);
            }
        }
    }

    @Override
    public ProfessionEditorSerializer<?> getType() {
        return ProfessionEditorSerializer.APPEND_EDITOR;
    }

    public static class Serializer implements ProfessionEditorSerializer<Append> {

        @Override
        public Append deserialize(JsonObject json, Gson gson) {
            JsonObject object = GsonHelper.convertToJsonObject(json, "append editor object");
            ResourceLocation appendKey = new ResourceLocation(GsonHelper.getAsString(object, "to_edit_profession_key"));
            Action[] actions = gson.fromJson(GsonHelper.getAsJsonArray(object, "actions"), Action[].class);
            Multimap<ActionType, Action> actionMap = LinkedHashMultimap.create();
            for (Action action : actions) {
                actionMap.put(action.getType(), action);
            }
            Unlock<?>[] unlocks = gson.fromJson(GsonHelper.getAsJsonArray(object, "unlocks"), Unlock[].class);
            Multimap<UnlockType<?>, Unlock<?>> unlockMap = LinkedHashMultimap.create();
            for (Unlock<?> unlock : unlocks) {
                unlockMap.put(unlock.getType(), unlock);
            }
            return new Append(appendKey, actionMap.asMap(), unlockMap.asMap());
        }

        @Override
        public JsonElement serialize(Append src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("function_type", RegistryConstants.PROFESSION_EDITOR_SERIALIZER.getKey(src.getType()).toString());
            object.addProperty("to_edit_profession_key", src.editKey.toString());
            JsonArray actionArray = new JsonArray();
            for (Collection<Action> value : src.actions.values()) {
                for (Action action : value) {
                    actionArray.add(context.serialize(action));
                }
            }
            object.add("actions", actionArray);
            JsonArray unlockArray = new JsonArray();
            for (Collection<Unlock<?>> value : src.unlocks.values()) {
                for (Unlock<?> unlock : value) {
                    unlockArray.add(context.serialize(unlock));
                }
            }
            object.add("unlocks", unlockArray);
            return object;
        }

        @Override
        public Class<Append> getType() {
            return Append.class;
        }
    }

    public static class Builder {
        protected ResourceLocation professionToEdit;
        protected LinkedHashMultimap<UnlockType<?>, Unlock<?>> unlocks;
        protected LinkedHashMultimap<ActionType, Action> actions;
        //protected ResourceLocation key;  skip this for now, might need it like in ProfessionBuilder later though. maybe we deserialize directly to builder?


        private Builder(ResourceLocation professionToEdit) {
            this.professionToEdit = professionToEdit;
            this.unlocks = LinkedHashMultimap.create();
            this.actions = LinkedHashMultimap.create();
        }

        public static Builder appender(ResourceLocation professionToEdit) {
            return new Builder(professionToEdit);
        }

        public Builder addUnlock(UnlockType<?> type, Unlock<?> unlock) {
            this.unlocks.put(type, unlock);
            return this;
        }

        public Builder addAction(ActionType type, Action action) {
            this.actions.put(type, action);
            return this;
        }

        public Append build() {
            return new Append(professionToEdit, actions.asMap(), unlocks.asMap());
        }

    }
}
