package com.epherical.professions.events;

import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ProfessionEvents {

    /**
     * See {@link SerializerCallback#addProfessionSerializer(GsonBuilder)}
     */
    public static final Event<SerializerCallback> SERIALIZER_CALLBACK = EventFactory.createArrayBacked(SerializerCallback.class, calls -> builder -> {
        for (SerializerCallback call : calls) {
            call.addProfessionSerializer(builder);
        }
    });


    public interface SerializerCallback {
        /**
         * Callback method to register your own Profession types. {@link com.epherical.professions.profession.action.Action Actions},
         * {@link com.epherical.professions.profession.conditions.ActionCondition ActionConditions},
         * and {@link com.epherical.professions.profession.rewards.Reward rewards} all get serialized on their own.
         */
        void addProfessionSerializer(GsonBuilder builder);
    }
}
