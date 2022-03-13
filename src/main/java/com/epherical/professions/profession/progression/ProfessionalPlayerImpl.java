package com.epherical.professions.profession.progression;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.events.OccupationEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfessionalPlayerImpl implements ProfessionalPlayer {
    private UUID uuid;
    private final List<Occupation> occupations;

    public ProfessionalPlayerImpl(List<Occupation> occupations) {
        this.occupations = occupations;
    }

    public ProfessionalPlayerImpl(UUID uuid) {
        this.uuid = uuid;
        this.occupations = new ArrayList<>();
    }


    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void handleAction(ProfessionContext context) {
        OccupationEvents.BEFORE_ACTION_HANDLED_EVENT.invoker().onHandleAction(context, this);
        for (Occupation occupation : occupations) {
            if (occupation.isActive()) {
                for (Action action : occupation.getProfession().getActions()) {
                    if (action.getType() == context.getParameter(ProfessionParameter.ACTION_TYPE)) {
                        action.handleRewards(context, occupation);
                    }
                }
            }
        }
    }

    public static class Serializer implements JsonSerializer<ProfessionalPlayerImpl>, JsonDeserializer<ProfessionalPlayerImpl> {
        @Override
        public ProfessionalPlayerImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Occupation[] occupations = GsonHelper.getAsObject(object, "occupations", new Occupation[0], context, Occupation[].class);
            return new ProfessionalPlayerImpl(List.of(occupations));
        }

        @Override
        public JsonElement serialize(ProfessionalPlayerImpl src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add("occupations", context.serialize(src.occupations, Occupation[].class));
            return object;
        }
    }
}
