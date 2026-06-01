package com.epherical.professions.api.event.runtime;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Represents an event that is triggered when a player joins the game.
 * This event provides access to the server-side player instance and
 * the corresponding professional player data.
 *
 */
public class PlayerJoinEvent extends AbstractProfessionEvent {

    public static final EventKey<PlayerJoinEvent> KEY =
            new EventKey<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "player_join"), PlayerJoinEvent.class);
    private final ServerPlayer serverPlayer;
    private final IProfessionalPlayer player;

    public PlayerJoinEvent(ServerPlayer serverPlayer, IProfessionalPlayer player) {
        super(KEY);
        this.serverPlayer = serverPlayer;
        this.player = player;
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public IProfessionalPlayer getPlayer() {
        return player;
    }
}
