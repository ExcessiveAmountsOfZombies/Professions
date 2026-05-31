package com.epherical.professions.api.event.runtime;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.actions.Action;
import net.minecraft.resources.ResourceLocation;

public class ActionValidEvent extends AbstractCancellableProfessionEvent {

    public static final EventKey<ActionValidEvent> KEY =
            new EventKey<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "action_valid"), ActionValidEvent.class);
    private final Action<?> action;
    private final Occupation occupation;
    private final IProfessionalPlayer player;
    private final ProfessionContext context;

    public ActionValidEvent(Action<?> action, Occupation occupation, IProfessionalPlayer player, ProfessionContext context) {
        super(KEY);
        this.action = action;
        this.occupation = occupation;
        this.player = player;
        this.context = context;
    }

    public Action<?> getAction() {
        return action;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public IProfessionalPlayer getPlayer() {
        return player;
    }

    public ProfessionContext getContext() {
        return context;
    }
}
