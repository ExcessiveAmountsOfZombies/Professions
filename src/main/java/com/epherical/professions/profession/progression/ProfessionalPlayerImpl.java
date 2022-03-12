package com.epherical.professions.profession.progression;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.events.OccupationEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfessionalPlayerImpl implements ProfessionalPlayer, JsonSerializer<ProfessionalPlayerImpl>, JsonDeserializer<ProfessionalPlayerImpl> {
    private final UUID uuid;
    private List<Occupation> occupations = new ArrayList<>();

    public ProfessionalPlayerImpl(UUID uuid) {
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

    @Override
    public ProfessionalPlayerImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(ProfessionalPlayerImpl src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
