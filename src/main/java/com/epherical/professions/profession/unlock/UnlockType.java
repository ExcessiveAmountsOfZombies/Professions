package com.epherical.professions.profession.unlock;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class UnlockType<T> extends SerializerType<Unlock<?>> {

    private final Class<T> type;
    private final String translationKey;

    /**
     * @param translationKey Should be a translation key that will go into a lang file, so they can be called in translatable components.
     */
    public UnlockType(Serializer<? extends Unlock<?>> serializer, Class<T> type, String translationKey) {
        super(serializer);
        this.type = type;
        this.translationKey = translationKey;
    }

    public Class<T> getType() {
        return type;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
