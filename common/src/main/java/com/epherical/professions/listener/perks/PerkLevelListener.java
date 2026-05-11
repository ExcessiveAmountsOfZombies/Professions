package com.epherical.professions.listener.perks;

import com.epherical.professions.PerkManager;
import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.rewards.OccupationLevelEvent;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.server.S2CPlayerDataSyncPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PerkLevelListener implements EventListener<OccupationLevelEvent> {


    private final PerkManager perkManager;

    public PerkLevelListener(PerkManager manager) {
        this.perkManager = manager;
    }

    @Override
    public void handle(OccupationLevelEvent event) {
        IProfessionalPlayer player = event.getPlayer();
        Occupation occupation = event.getOccupation();
        int level = event.getNewLevel();

        // todo; when we allow changing categories, this could become a problem
        // right now if you set someones level to a lower than what they were they'll keep their IStartupPerks
        // if they relog they'll be removed.


        if (player != null) {
            perkManager.setUnclaimedPerks(occupation, level, player);

            PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();

            S2CPlayerDataSyncPayload syncPayload = new S2CPlayerDataSyncPayload(
                    event.getPlayer().getPlayer().getUUID(),
                    player.getAllOccupations(),
                    Optional.ofNullable(playerManager.getCategoryIdFor(player)),
                    playerManager.getRelevantActionsForCategory(player.getCategory()),
                    playerManager.getAllPerks(player.getCategory())
            );
            NetworkPayloadDispatcher.sendToPlayer((ServerPlayer) event.getPlayer().getPlayer(), syncPayload);

        }
    }
}

