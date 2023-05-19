package com.epherical.professions.profession.operation;

import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.ActionEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;

import java.util.List;

public class TagOperation<T> extends AbstractOperation<T> {

    public TagOperation(List<Operator<Action<T>, List<ResourceLocation>>> actions, List<Operator<Unlock<T>, List<LevelRequirement>>> unlocks) {
        super(actions, unlocks);
    }

    @Override
    public ActionEntry<T> getActionEntryType(MinecraftServer server, ResourceKey<Registry<T>> registry, ResourceLocation key) {
        return ActionEntry.of(TagKey.create(registry, key));
    }

    public static class TagOperationSerializer<V> extends AbstractOperationSerializer<TagOperation<V>, V> {

        @Override
        public String serializeType() {
            return "professions:tag";
        }

        @Override
        public TagOperation<V> deserialize(List<Operator<Action<V>, List<ResourceLocation>>> actions, List<Operator<Unlock<V>, List<LevelRequirement>>> unlocks) {
            return new TagOperation<>(actions, unlocks);
        }
    }
}
