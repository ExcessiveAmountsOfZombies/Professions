package com.epherical.professions.model;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.progression.OccupationSlot;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.epherical.professions.ProfessionsCommon.PROFESSION_REGISTRY_KEY;

public class ProfessionalPlayer implements IProfessionalPlayer {

    private final Map<ResourceLocation, Occupation> occupationMap = new HashMap<>();

    private volatile boolean dirty = false;

    private volatile ServerPlayer player;
    private UUID uuid;


    public ProfessionalPlayer(UUID uuid, List<Occupation> occupations) {
        this.uuid = uuid;
        for (Occupation occupation : occupations) {
            occupationMap.put(occupation.getProfessionKey(), occupation);
        }
    }

    public ProfessionalPlayer(List<Occupation> occupations, ServerPlayer player, RegistryAccess access) {
        this.player = player;
        this.uuid = player.getUUID();


        for (Occupation occupation : occupations) {
            occupationMap.put(occupation.getProfessionKey(), occupation);
        }

        access.lookupOrThrow(PROFESSION_REGISTRY_KEY).listElements().forEach(element -> {
            Occupation occupation = new Occupation(element, 0,0, OccupationSlot.ACTIVE);
            occupation.resetMaxExperience();
            occupationMap.putIfAbsent(occupation.getProfessionKey(), occupation);
        });

    }




    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public @Nullable ServerPlayer getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(@Nullable ServerPlayer player) {
        this.player = player;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public <T> void handleAction(ProfessionContext context, Holder<T> holder) {

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
        /*if (!alreadyHasOccupation(profession)) {
            addOccupationInternal(new Occupation(profession, 0, 1, slot));
            resetMaxExperience();
            return true;
        } else {
            for (Occupation occupation : allOccupations) {
                if (occupation.isProfession(profession)) {
                    occupation.setSlot(slot);
                    return true;
                }
            }
        }*/
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
        return occupationMap.get(profession.unwrapKey().get().location());
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
}
