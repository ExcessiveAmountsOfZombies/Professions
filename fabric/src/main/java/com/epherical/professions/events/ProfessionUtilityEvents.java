package com.epherical.professions.events;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.data.Storage;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.UUID;

public final class ProfessionUtilityEvents {

    /**
     * See {@link SerializerCallback#addProfessionSerializer(GsonBuilder)}
     */
    public static final Event<SerializerCallback> SERIALIZER_CALLBACK = EventFactory.createArrayBacked(SerializerCallback.class, calls -> builder -> {
        for (SerializerCallback call : calls) {
            call.addProfessionSerializer(builder);
        }
    });

    /**
     * See {@link StorageCallback#setStorage(Storage)}
     */
    public static final Event<StorageCallback> STORAGE_CALLBACK = EventFactory.createArrayBacked(StorageCallback.class, calls -> storage -> {
        if (calls.length == 0) {
            return storage;
        }
        Storage<ProfessionalPlayer, UUID> override = null;
        for (StorageCallback call : calls) {
            override = call.setStorage(storage);
        }
        return override;
    });

    public static final Event<FinalizeStorage> STORAGE_FINALIZATION_EVENT = EventFactory.createArrayBacked(FinalizeStorage.class, calls -> storage -> {
        for (FinalizeStorage call : calls) {
            call.onFinalization(storage);
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

    public interface StorageCallback {
        /**
         * Only the <B>latest</B> callback will be used as the main storage method, but it will always override the default.
         * <br>
         * Callback method to change the {@link Storage} field. This could allow for different storage methods (MYSQL, H2, SQLITE, etc)
         * @return the storage to change.
         */
        Storage<ProfessionalPlayer, UUID> setStorage(Storage<ProfessionalPlayer, UUID> storage);
    }

    public interface FinalizeStorage {
        void onFinalization(Storage<ProfessionalPlayer, UUID> storage);
    }
}
