package com.epherical.professions.client.entry;

import com.epherical.professions.client.format.FormatBuilder;
import com.epherical.professions.client.format.FormatRegistry;
import com.epherical.professions.client.screen.CommonDataScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public class CompoundAwareEntry<OBJ, T> extends AbstractCompoundEntry<OBJ, CompoundAwareEntry<OBJ, T>> {

    private final ResourceKey<Registry<T>> registryKey;
    private final RegistryEntry<OBJ, T> entry;
    private final Deserializer<OBJ, CompoundAwareEntry<OBJ, T>> deserializer;

    private T value;

    private final int embeddedDistance;
    private final int screenWidth;

    public CompoundAwareEntry(int x, int y, int width, int embed, int screenWidth, ResourceKey<Registry<T>> registry, RegistryEntry<OBJ, T> registryEntry,
                              Deserializer<OBJ, CompoundAwareEntry<OBJ, T>> deserializer, Type... types) {
        super(x, y, width, List.of(), types);
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
        deserializer.deserialize(object, this);
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
}
