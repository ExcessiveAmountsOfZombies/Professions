package com.epherical.professions.profession.editor;

import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.ProfessionEditorSerializer;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
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
    private ResourceLocation editLocation;


    public Append(ResourceLocation editKey, Map<ActionType, Collection<Action>> actions) {
        this.editKey = editKey;
        this.actions = actions;
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
            return new Append(appendKey, actionMap.asMap());
        }

        @Override
        public JsonElement serialize(Append src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("to_edit_profession_key", src.editKey.toString());
            JsonArray actionArray = new JsonArray();
            for (Collection<Action> value : src.actions.values()) {
                for (Action action : value) {
                    actionArray.add(context.serialize(action));
                }
            }
            object.add("actions", actionArray);
            return object;
        }

        @Override
        public Class<Append> getType() {
            return Append.class;
        }
    }
}
