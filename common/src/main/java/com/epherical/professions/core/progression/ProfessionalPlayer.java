package com.epherical.professions.core.progression;

import com.epherical.professions.CommonClass;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfessionalPlayer implements IProfessionalPlayer {

    private final Map<Holder<Profession>, Occupation> occupationMap = new HashMap<>();

    private volatile boolean dirty = false;


    public static final Codec<IProfessionalPlayer> CODEC = RecordCodecBuilder.create(i -> i.group(
            Occupation.CODEC.listOf().fieldOf("occupations").forGetter(IProfessionalPlayer::getAllOccupations)
    ).apply(i, ProfessionalPlayer::new));



    public ProfessionalPlayer(List<Occupation> occupations) {
        for (Occupation occupation : occupations) {
            occupationMap.put(occupation.getProfession(), occupation);
        }
    }


    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public @Nullable ServerPlayer getPlayer() {
        return null;
    }

    @Override
    public void setNeedsToBeSaved() {

    }

    @Override
    public void save() {
        if (dirty) {

            // todo; save Player
            dirty = false;
        }

    }

    @Override
    public <T> void handleAction(ProfessionContext context, Holder<T> holder) {
        Collection<Action> actions = CommonClass.ACTION_LOAD2.getActionsByHolder(holder);
        ActionType actionType = context.getPossibleParameter(ProfessionParameter.ACTION_TYPE);
        // todo; check if the player is in any occupations
        if (actionType != null) {
            for (Action action : actions) {
                if (actionType.equals(action.getType())) {
                    action.handleAction(context);
                }
            }
        }
    }

    @Override
    public boolean alreadyHasOccupation(Holder<Profession> profession) {
        return false;
    }

    @Override
    public boolean isOccupationActive(Holder<Profession> profession) {
        return false;
    }

    @Override
    public boolean joinOccupation(Holder<Profession> profession, OccupationSlot slot) {
        if (!alreadyHasOccupation(profession)) {
            occupationMap.put(profession, new Occupation(profession, 0, 1, slot));
            resetMaxExperience();
            return true;
        } else {
            for (Occupation occupation : occupationMap.values()) {
                if (occupation.isProfession(profession)) {
                    occupation.setSlot(slot);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean leaveOccupation(Holder<Profession> profession) {
        return false;
    }

    @Override
    public boolean fireFromOccupation(Holder<Profession> profession) {
        return false;
    }

    @Override
    public Occupation getOccupation(Holder<Profession> profession) {
        return occupationMap.get(profession);
    }

    @Override
    public void updateOccupationPerks() {

    }

    @Override
    public List<Occupation> getActiveOccupations() {
        return List.of();
    }

    @Override
    public List<Occupation> getInactiveOccupations() {
        return List.of();
    }

    @Override
    public List<Occupation> getAllOccupations() {
        return List.copyOf(occupationMap.values());
    }


    public Map<Holder<Profession>, Occupation> getOccupationMap() {
        return occupationMap;
    }

    public void resetMaxExperience() {
        for (Occupation occupation : occupationMap.values()) {
            occupation.resetMaxExperience();
        }
    }
}
