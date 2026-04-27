package com.epherical.professions.runtime.event;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.actions.Action;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public class ActionProcessingEvent extends AbstractCancellableProfessionEvent {

    public static final EventKey<ActionProcessingEvent> KEY =
            new EventKey<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "action_processing"), ActionProcessingEvent.class);

    private final Collection<Action<?>> actions;
    private final IProfessionalPlayer player;
    private final ProfessionContext context;

    public ActionProcessingEvent(Collection<Action<?>> actions, IProfessionalPlayer player, ProfessionContext context) {
        super(KEY);
        this.actions = actions;
        this.player = player;
        this.context = context;
    }


    public Collection<Action<?>> getActions() {
        return actions;
    }

    public IProfessionalPlayer getPlayer() {
        return player;
    }

    public ProfessionContext getContext() {
        return context;
    }
}
