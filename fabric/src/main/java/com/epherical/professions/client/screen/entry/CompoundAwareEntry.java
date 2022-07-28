package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.client.screen.piece.PieceRegistry;
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
        children.addAll(PieceRegistry.PIECES.get(entry.getRegistry().getKey(value)).entries().apply(embed, y, screenWidth));
    }


    @Override
    public void onRebuild(DatapackScreen screen) {
        if (value != entry.getValue()) {
            this.value = entry.getValue();
            children.clear();
            children.add(entry);
            children.addAll(PieceRegistry.PIECES.get(entry.getRegistry().getKey(value)).entries().apply(embeddedDistance, y, screenWidth));
        }
        super.onRebuild(screen);
    }


}
