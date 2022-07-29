package com.epherical.professions.client.entry;

import com.epherical.professions.client.format.Format;
import com.epherical.professions.client.format.PieceRegistry;
import com.epherical.professions.client.screen.CommonDataScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public class CompoundAwareEntry<T> extends CompoundEntry {

    private final ResourceKey<Registry<T>> registryKey;
    private final RegistryEntry<T> entry;

    private T value;

    private final int embeddedDistance;
    private final int screenWidth;

    public CompoundAwareEntry(int x, int y, int width, int embed, int screenWidth, ResourceKey<Registry<T>> registry, RegistryEntry<T> registryEntry, Type... types) {
        super(x, y, width, List.of(), types);
        this.registryKey = registry;
        this.entry = registryEntry;
        this.embeddedDistance = embed;
        this.screenWidth = screenWidth;
        this.value = registryEntry.getValue();
        children.add(registryEntry);
        Format format = PieceRegistry.PIECES.get(entry.getRegistry().getKey(value));
        if (format != null) {
            children.addAll(format.entries().apply(embed, y, screenWidth));
        }
    }


    @Override
    public void onRebuild(CommonDataScreen screen) {
        if (value != entry.getValue()) {
            this.value = entry.getValue();
            children.clear();
            children.add(entry);
            Format format = PieceRegistry.PIECES.get(entry.getRegistry().getKey(value));
            if (format != null) {
                children.addAll(format.entries().apply(embeddedDistance, y, screenWidth));
            }

        }
        super.onRebuild(screen);
    }


}
