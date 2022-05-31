package com.epherical.professions.profession.progression;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.data.Storage;
import com.epherical.professions.events.OccupationEvents;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.UnlockableDataImpl;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.util.ActionLogger;
import com.epherical.professions.util.Tristate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ProfessionalPlayerImpl implements ProfessionalPlayer {
    private UUID uuid;
    private ServerPlayer player;
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
    public @Nullable ServerPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public void handleAction(ProfessionContext context, ServerPlayer player) {
        OccupationEvents.BEFORE_ACTION_HANDLED_EVENT.invoker().onHandleAction(context, this);
        for (Occupation occupation : occupations) {
            if (occupation.isActive()) {
                Collection<Action> actions = occupation.getProfession().getActions(context.getParameter(ProfessionParameter.ACTION_TYPE));
                if (actions != null && !actions.isEmpty()) {
                    ActionLogger logger = context.getParameter(ProfessionParameter.ACTION_LOGGER);
                    for (Action action : actions) {
                        if (action.handleAction(context, occupation)) {
                            logger.startMessage(occupation);
                            action.giveRewards(context, occupation);
                            logger.sendMessage(player);
                        }
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
    public boolean alreadyHasOccupation(Profession profession) {
        return getOccupation(profession) != null;
    }

    @Override
    public boolean isOccupationActive(Profession profession) {
        Occupation occupation = getOccupation(profession);
        return occupation != null && occupation.isActive();
    }

    @Override
    public boolean joinOccupation(Profession profession, OccupationSlot slot) {
        if (!alreadyHasOccupation(profession)) {
            occupations.add(new Occupation(profession, 0, 1, slot));
            resetMaxExperience();
            return true;
        } else {
            for (Occupation occupation : occupations) {
                if (occupation.isProfession(profession)) {
                    occupation.setSlot(slot);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean leaveOccupation(Profession profession) {
        if (!this.isOccupationActive(profession)) {
            return false;
        } else {
            if (ProfessionConfig.clearProgressOnLeave) {
                fireFromOccupation(profession);
            } else {
                getOccupation(profession).setSlot(OccupationSlot.INACTIVE);
            }
        }

        return true;
    }

    @Override
    public boolean fireFromOccupation(Profession profession) {
        return occupations.remove(getOccupation(profession));
    }

    public List<Occupation> getOccupations() {
        return Collections.unmodifiableList(occupations);
    }

    @Override
    public List<Occupation> getActiveOccupations() {
        return getOccupations(true);
    }

    @Override
    public List<Occupation> getInactiveOccupations() {
        return getOccupations(false);
    }

    @Override // passing the unlockType but not doing anything with it might be silly, but it lets us enforce typed data.
    // so other people coding know what they're sending/need to send
    public <T> UnlockableData getUnlockableData(UnlockType<T> unlockType, T object) {
        for (Occupation activeOccupation : getActiveOccupations()) {
            if (activeOccupation.getData().canUse(object) != Tristate.UNKNOWN) {
                return activeOccupation.getData();
            }
        }
        return null;
    }


    private List<Occupation> getOccupations(boolean active) {
        ImmutableList.Builder<Occupation> occ = ImmutableList.builder();
        for (Occupation occupation : occupations) {
            if (occupation.isActive() == active) {
                occ.add(occupation);
            }
        }
        return occ.build();
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

    public void resetMaxExperience() {
        for (Occupation occupation : occupations) {
            occupation.resetMaxExperience();
        }
    }

    public static class Serializer implements JsonSerializer<ProfessionalPlayerImpl>, JsonDeserializer<ProfessionalPlayerImpl> {
        @Override
        public ProfessionalPlayerImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Occupation[] occupations = GsonHelper.getAsObject(object, "occupations", new Occupation[0], context, Occupation[].class);
            return new ProfessionalPlayerImpl(Lists.newArrayList(occupations));
        }

        @Override
        public JsonElement serialize(ProfessionalPlayerImpl src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add("occupations", context.serialize(src.occupations.toArray(), Occupation[].class));
            return object;
        }
    }
}
