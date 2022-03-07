package com.epherical.professions.events;


import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.ProfessionContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class OccupationEvents {


    public static final Event<BeforeActionHandled> BEFORE_ACTION_HANDLED_EVENT = EventFactory.createArrayBacked(BeforeActionHandled.class, calls -> (context, player) -> {
        for (BeforeActionHandled call : calls) {
            call.onHandleAction(context, player);
        }
    });


    public interface BeforeActionHandled {
        void onHandleAction(ProfessionContext context, ProfessionalPlayer player);
    }
}
