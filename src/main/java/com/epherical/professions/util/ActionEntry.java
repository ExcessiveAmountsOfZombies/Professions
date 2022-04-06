package com.epherical.professions.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Basically the same thing as the Ingredient class for recipes, but accepts generics, so we can put entities, blocks and item
 * tags in our Actions.
 */
public class ActionEntry<T> {
    public static final ActionEntry<?> EMPTY = new ActionEntry<>(Stream.empty());
    private final Value<T>[] actionValues;
    @Nullable
    private List<T> values;


    private ActionEntry(Stream<? extends ActionEntry.Value<T>> values) {
        this.actionValues = values.toArray(Value[]::new);
    }

    public List<T> getActionValues(Registry<T> registry) {
        this.pullValues(registry);
        return values;
    }

    public JsonArray serialize(Registry<T> registry) {
        JsonArray array = new JsonArray();
        for (Value<T> actionValue : actionValues) {
            array.add(actionValue.serializable(registry));
        }
        return array;
    }

    private void pullValues(Registry<T> registry) {
        if (this.values == null) {
            this.values = Arrays.stream(actionValues)
                    .flatMap(value -> value.getValues(registry).stream())
                    .distinct().collect(Collectors.toList());
        }
    }

    public static <T> ActionEntry<T> of(TagKey<T> tag) {
        return fromValues(Stream.of(new ActionEntry.TagEntry<>(tag)));
    }

    @SafeVarargs
    public static <T> ActionEntry<T> of(T... values) {
        return of(Arrays.stream(values));
    }

    public static <T> ActionEntry<T> of(Stream<T> stream) {
        return fromValues(stream.map(SingleEntry::new));
    }

    private static <T> ActionEntry<T> fromValues(Stream<? extends ActionEntry.Value<T>> stream) {
        ActionEntry<T> entry = new ActionEntry<>(stream);
        return entry.actionValues.length == 0 ? (ActionEntry<T>) EMPTY : entry;
    }

    private interface Value<T> {
        Collection<T> getValues(Registry<T> registry);

        String serializable(Registry<T> registry);
    }

    record TagEntry<T>(TagKey<T> entry) implements Value<T> {

        @Override
        public Collection<T> getValues(Registry<T> registry) {
            List<T> list = new ArrayList<>();
            registry.getTagOrEmpty(entry).forEach(tHolder -> list.add(tHolder.value()));
            return list;
        }

        @Override
        public String serializable(Registry<T> registry) {
            return "#" + entry.location();
        }
    }

    record SingleEntry<T>(T entry) implements Value<T> {

        @Override
        public Collection<T> getValues(Registry<T> registry) {
            return Collections.singleton(entry);
        }

        @Override
        public String serializable(Registry<T> registry) {
            return registry.getKey(entry).toString();
        }
    }
}