package com.epherical.professions.util;

import com.epherical.professions.profession.operation.AbstractOperation;
import com.epherical.professions.profession.operation.ObjectOperation;
import com.epherical.professions.profession.operation.TagOperation;
import com.google.gson.JsonArray;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    @Deprecated
    public Value<T>[] getActionValues() {
        return actionValues;
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

    @SafeVarargs
    public static <T> ActionEntry<T> of(ResourceKey<T>... keys) {
        return ofKeys(Arrays.stream(keys));
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

    public static <T> ActionEntry<T> ofKeys(Stream<ResourceKey<T>> stream) {
        return fromValues(stream.map(ResourceEntry::new));
    }

    private static <T> ActionEntry<T> fromValues(Stream<? extends Value<T>> stream) {
        ActionEntry<T> entry = new ActionEntry<>(stream);
        return entry.actionValues.length == 0 ? (ActionEntry<T>) EMPTY : entry;
    }

    // todo; change back to private 2.0.0 after maybe
    public interface Value<T> {
        Collection<T> getValues(Registry<T> registry);

        String serializable(Registry<T> registry);

        @Deprecated()
        AbstractOperation<T> createOperation(Registry<T> registry);

        @Deprecated()
        ResourceLocation getKey(Registry<T> registry);
    }

    record ResourceEntry<T>(ResourceKey<T> entry) implements Value<T> {

        @Override
        public Collection<T> getValues(Registry<T> registry) {
            T t = registry.get(entry);
            if (t != null) {
                return Collections.singleton(t);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public String serializable(Registry<T> registry) {
            return entry.location().toString();
        }

        @Override
        public AbstractOperation<T> createOperation(Registry<T> registry) {
            ObjectOperation<T> operation = new ObjectOperation<>(new ArrayList<>(), new ArrayList<>());
            operation.setKey((ResourceKey<Registry<T>>) registry.key(), entry.location());
            return operation;
        }

        @Override
        public ResourceLocation getKey(Registry<T> registry) {
            return entry.location();
        }
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

        @Override
        public AbstractOperation<T> createOperation(Registry<T> registry) {
            TagOperation<T> operation = new TagOperation<>(new ArrayList<>(), new ArrayList<>());
            operation.setKey((ResourceKey<Registry<T>>) registry.key(), entry.location());
            return operation;
        }

        @Override
        public ResourceLocation getKey(Registry<T> registry) {
            return entry.location();
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

        @Override
        public AbstractOperation<T> createOperation(Registry<T> registry) {
            ObjectOperation<T> operation = new ObjectOperation<>(new ArrayList<>(), new ArrayList<>());
            operation.setKey((ResourceKey<Registry<T>>) registry.key(), registry.getKey(entry));
            return operation;
        }

        @Override
        public ResourceLocation getKey(Registry<T> registry) {
            return registry.getKey(entry);
        }
    }
}
