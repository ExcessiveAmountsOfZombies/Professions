package com.epherical.professions.api.event.runtime.perks;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.api.event.runtime.AbstractProfessionEvent;
import com.epherical.professions.model.Occupation;
import net.minecraft.resources.Identifier;

import java.util.Set;

/**
 * Represents an event triggered when a player claims a set of perks for a specific occupation.
 */
public class PerkClaimedEvent extends AbstractProfessionEvent {

    public static final EventKey<PerkClaimedEvent> KEY =
            new EventKey<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation_perks_claimed"), PerkClaimedEvent.class);
    private final Occupation occupation;
    private final IProfessionalPlayer player;
    private final Set<Identifier> claimedPerkIds;

    public PerkClaimedEvent(Occupation occupation, IProfessionalPlayer player, Set<Identifier> claimedPerkIds) {
        super(KEY);
        this.occupation = occupation;
        this.player = player;
        this.claimedPerkIds = claimedPerkIds;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public IProfessionalPlayer getPlayer() {
        return player;
    }

    /**
     * @return An Immutable Set of the claimed perks.
     */
    public Set<Identifier> getClaimedPerkIds() {
        return claimedPerkIds;
    }
}
