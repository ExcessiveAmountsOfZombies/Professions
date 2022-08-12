package com.epherical.professions.util;

import com.google.gson.JsonArray;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
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


    private ActionEntry(Stream<? extends Value<T>> values) {
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

    public List<String> serializeString(Registry<T> registry) {
        List<String> array = new ArrayList<>();
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

    public void toNetwork(FriendlyByteBuf buf, Registry<T> registry) {

        buf.writeCollection(Arrays.asList(actionValues), (buf1, tValue) -> {
            Collection<T> values = tValue.getValues(registry);
            buf.writeVarInt(values.size());
            for (T value : values) {
                buf.writeVarInt(registry.getId(value));
            }
        });
    }

    public static <T> List<ActionEntry<T>> fromNetwork(FriendlyByteBuf buf, Registry<T> registry) {
        return buf.readList(buf1 -> {
            List<T> values = new ArrayList<>();
            int collectionSize = buf1.readVarInt();
            for (int i = 0; i < collectionSize; i++) {
                values.add(registry.byId(buf.readVarInt()));
            }
            return of(values.stream());
        });
    }

    public static <T> ActionEntry<T> of(TagKey<T> tag) {
        return fromValues(Stream.of(new TagEntry<>(tag)));
    }

    @SafeVarargs
    public static <T> ActionEntry<T> of(T... values) {
        return of(Arrays.stream(values));
    }

    public static <T> ActionEntry<T> of(Stream<T> stream) {
        return fromValues(stream.map(SingleEntry::new));
    }

    private static <T> ActionEntry<T> fromValues(Stream<? extends Value<T>> stream) {
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
