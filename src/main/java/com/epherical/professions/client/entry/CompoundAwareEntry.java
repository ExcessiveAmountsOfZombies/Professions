package com.epherical.professions.client.entry;

import com.epherical.professions.client.format.FormatBuilder;
import com.epherical.professions.client.format.FormatRegistry;
import com.epherical.professions.client.screen.CommonDataScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Optional;

public class CompoundAwareEntry<OBJ, T> extends AbstractCompoundEntry<OBJ, CompoundAwareEntry<OBJ, T>> {

    private final ResourceKey<Registry<T>> registryKey;
    private final RegistryEntry<OBJ, T> entry;
    private final Deserializer<OBJ, CompoundAwareEntry<OBJ, T>> deserializer;

    private T value;

    private OBJ objValue;

    private final int embeddedDistance;
    private final int screenWidth;

    public CompoundAwareEntry(int x, int y, int width, int embed, int screenWidth, ResourceKey<Registry<T>> registry, RegistryEntry<OBJ, T> registryEntry,
                              Deserializer<OBJ, CompoundAwareEntry<OBJ, T>> deserializer, Type... types) {
        this(x, y, width, embed, screenWidth, registry, registryEntry, deserializer, Optional.empty(), types);
    }

    public CompoundAwareEntry(int x, int y, int width, int embed, int screenWidth, ResourceKey<Registry<T>> registry, RegistryEntry<OBJ, T> registryEntry,
                              Deserializer<OBJ, CompoundAwareEntry<OBJ, T>> deserializer, Optional<String> key, Type... types) {
        super(x, y, width, key, List.of(), types);
        this.deserializer = deserializer;
        this.registryKey = registry;
        this.entry = registryEntry;
        this.embeddedDistance = embed;
        this.screenWidth = screenWidth;
        this.value = registryEntry.getValue();
        children.add(registryEntry);
        FormatBuilder<OBJ> format = FormatRegistry.grabBuilder(entry.getRegistry(), value);
        if (format != null) {
            children.addAll(format.buildDefaultFormat().entries().apply(embed, y, screenWidth));
        }
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        if (value != entry.getValue()) {
            this.value = entry.getValue();
            children.clear();
            children.add(entry);
            FormatBuilder<OBJ> format = FormatRegistry.grabBuilder(entry.getRegistry(), value);
            if (format != null) {
                children.addAll(format.buildDefaultFormat().entries().apply(embeddedDistance, y, screenWidth));
            }

        }
        super.onRebuild(screen);
    }

    @Override
    public void deserialize(OBJ object) {
        this.objValue = object;
        deserializer.deserialize(object, this);
        object = this.objValue;

        this.value = entry.getValue();
        children.clear();
        children.add(entry);
        FormatBuilder<OBJ> format = FormatRegistry.grabBuilder(entry.getRegistry(), value);
        if (format != null) {
            for (DatapackEntry<OBJ, ?> entry : format.deserializeToFormat(object).entries().apply(embeddedDistance, y, screenWidth)) {
                entry.deserialize(object);
                children.add(entry);
            }
        }
    }

    public RegistryEntry<OBJ, T> getEntry() {
        return entry;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Mutate a value that needs to be changed after deserializing
     */
    public void setNewObj(OBJ obj) {
        // this method is really stupid, but I don't want to refactor all the deserialization code to include some
        // sort of Mutator Class that just holds the object. The only thing that currently uses this is the
        // InvertedCondition code for action conditions.
        this.objValue = obj;
    }
}
