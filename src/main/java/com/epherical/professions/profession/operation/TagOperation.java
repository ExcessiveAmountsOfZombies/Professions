package com.epherical.professions.profession.operation;

import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.ActionEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;

import java.util.List;

public class TagOperation<T> extends AbstractOperation<T> {

    public TagOperation(List<ProfessionAction<T>> professionActions, List<Unlock<T>> unlocks) {
        super(professionActions, unlocks);
    }

    @Override
    public ActionEntry<T> getActionEntryType(MinecraftServer server, ResourceKey<Registry<T>> registry, ResourceLocation key) {
        return ActionEntry.of(TagKey.create(registry, key));
    }

    public static class TagOperationSerializer<V> extends AbstractOperationSerializer<TagOperation<V>, V> {

        @Override
        public TagOperation<V> deserialize(List<ProfessionAction<V>> professionActions, List<Unlock<V>> unlocks) {
            return new TagOperation<>(professionActions, unlocks);
        }
    }
}
