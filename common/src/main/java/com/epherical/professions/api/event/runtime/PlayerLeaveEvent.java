package com.epherical.professions.api.event.runtime;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class PlayerLeaveEvent extends AbstractProfessionEvent {

    public static final EventKey<PlayerLeaveEvent> KEY =
            new EventKey<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "player_leave"), PlayerLeaveEvent.class);
    private final ServerPlayer serverPlayer;
    private final IProfessionalPlayer player;

    public PlayerLeaveEvent(ServerPlayer serverPlayer, IProfessionalPlayer player) {
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
