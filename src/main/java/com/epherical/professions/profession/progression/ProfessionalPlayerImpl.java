package com.epherical.professions.profession.progression;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.data.Storage;
import com.epherical.professions.events.OccupationEvents;
import com.epherical.professions.profession.Profession;
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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfessionalPlayerImpl implements ProfessionalPlayer {
    private UUID uuid;
    private final List<Occupation> occupations;

    private volatile boolean dirty = false;

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

    @Override
    public void save(Storage<ProfessionalPlayer, UUID> storage) {
        if (dirty) {
            storage.saveUser(this);
            dirty = false;
        }

    }

    @Override
    public void needsToBeSaved() {
        dirty = true;
    }

    @Override
    public boolean alreadyHasProfession(Profession profession) {
        return getOccupation(profession) != null;
    }

    @Override
    public boolean joinOccupation(Profession profession) {
        if (!alreadyHasProfession(profession)) {
            occupations.add(new Occupation(profession, 0, 0, true));
            resetMaxExperience();
            return true;
        }
        return false;
    }

    @Nullable
    public Occupation getOccupation(Profession profession) {
        for (Occupation occupation : occupations) {
            if (occupation.isProfession(profession)) {
                return occupation;
            }
        }
        return null;
    }

    private void resetMaxExperience() {
        for (Occupation occupation : occupations) {
            occupation.resetMaxExperience();
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
            object.add("occupations", context.serialize(src.occupations.toArray(), Occupation[].class));
            return object;
        }
    }
}
