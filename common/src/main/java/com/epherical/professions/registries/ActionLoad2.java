package com.epherical.professions.registries;

import com.epherical.professions.core.actions.Action;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Loads all actions from JSON.  Decoding cost is O(total JSON size) and there is
 * no quadratic walk over the already–decoded data.
 */
public class ActionLoad2 implements PreparableReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PATH = "professions/actions";


    private final Multimap<Holder<?>, Action> byHolder = MultimapBuilder.hashKeys().arrayListValues().build();
    private final RegistryAccess registryAccess;
    private static final List<Resolver> deferred = new ArrayList<>();
    private volatile boolean resolved = false;

    public ActionLoad2(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier barrier,
                                                   @NotNull ResourceManager resourceManager,
                                                   @NotNull ProfilerFiller prepProfiler,
                                                   @NotNull ProfilerFiller applyProfiler,
                                                   @NotNull Executor background,
                                                   @NotNull Executor gameThread) {

        return CompletableFuture
                .supplyAsync(() -> decodeAll(resourceManager), background)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(map -> {
                    byHolder.clear();
                    byHolder.putAll(map);
                }, gameThread);
    }

    private <T> Multimap<Holder<?>, Action> decodeAll(ResourceManager manager) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);

        Multimap<Holder<?>, Action> result = MultimapBuilder.hashKeys().arrayListValues().build();



        Map<ResourceKey<?>, Codec<ActionOf<T>>> codecs = new HashMap<>();

        registryAccess.registries().forEach(reg -> {
            final ResourceKey<? extends Registry<?>> regKey = reg.key();
            FileToIdConverter converter = FileToIdConverter.json(PATH + "/" + folderOf(regKey.location()));

            converter.listMatchingResourceStacks(manager).forEach((ignored, resources) ->
                    decodeFile(ops, regKey, resources, result, codecs, registryAccess));
        });

        return result;
    }

    private static <T> void decodeFile(RegistryOps<JsonElement> ops,
                                       ResourceKey<? extends Registry<?>> regKeyRaw,
                                       List<Resource> resources,
                                       Multimap<Holder<?>, Action> sink, Map<ResourceKey<?>, Codec<ActionOf<T>>> codecMap,
                                       RegistryAccess registryAccess) {

        @SuppressWarnings("unchecked")
        ResourceKey<Registry<T>> regKey = (ResourceKey<Registry<T>>) regKeyRaw;

        Codec<ActionOf<T>> codec = codecMap.computeIfAbsent(regKeyRaw, k -> buildCodec(regKey));
        resources.forEach(res -> {
            try (Reader rd = res.openAsReader()) {
                JsonElement elem = GSON.fromJson(rd, JsonElement.class);
                ActionOf<T> actionOf = codec.decode(ops, elem).getOrThrow().getFirst();
                // --- inside decodeFile: replace the immediate registry lookup --------------
                actionOf.values().forEach((entry, single) ->
                        single.actions().forEach(action ->
                                deferred.add((regAccess, sink2) ->
                                        entry.ifLeft(tag ->
                                                        regAccess.registry(regKey)
                                                                .flatMap(r -> r.getTag(tag))
                                                                .ifPresent(set -> set.forEach(h -> sink2.put(h, action))))
                                             .ifRight(key ->
                                                             regAccess.registry(regKey)
                                                                     .flatMap(r -> r.getHolder(key))
                                                                     .ifPresent(h -> sink2.put(h, action))))));
            } catch (Exception ex) {
                LOGGER.warn("Failed to read {}", res.sourcePackId(), ex);
            }
        });
    }


    private static <T> Codec<ActionOf<T>> buildCodec(ResourceKey<Registry<T>> registry) {
        Codec<Either<TagKey<T>, ResourceKey<T>>> either = ExtraCodecs.TAG_OR_ELEMENT_ID.xmap(
                rl -> rl.tag() ? Either.left(TagKey.create(registry, rl.id()))
                        : Either.right(ResourceKey.create(registry, rl.id())),
                e -> e.map(tk -> new ExtraCodecs.TagOrElementLocation(tk.location(), true),
                        rk -> new ExtraCodecs.TagOrElementLocation(rk.location(), false))
        );

        Codec<SingleAction<T>> single = RecordCodecBuilder.create(i -> i.group(
                either.fieldOf("entry").forGetter(SingleAction::entry),
                Action.TYPED_CODEC.listOf().fieldOf("actions").forGetter(SingleAction::actions)
        ).apply(i, SingleAction::new));

        Codec<Map<Either<TagKey<T>, ResourceKey<T>>, SingleAction<T>>> values =
                single.listOf().xmap(
                        l -> l.stream().collect(toMap(SingleAction::entry, Function.identity())),
                        m -> new ArrayList<>(m.values())
                );

        return RecordCodecBuilder.create(i -> i.group(
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(ActionOf::replace),
                values.fieldOf("values").forGetter(ActionOf::values)
        ).apply(i, ActionOf::new));
    }

    private static String folderOf(ResourceLocation rl) {
        return rl.getNamespace() + "/" + rl.getPath();
    }

    public Multimap<Holder<?>, Action> getByHolder() {
        ensureResolved();
        return byHolder;
    }

    public Collection<Action> getActionsByHolder(Holder<?> holder) {
        ensureResolved();
        return byHolder.get(holder);
    }

    // --- resolver ---------------------------------------------------------------
    private void ensureResolved() {
        if (resolved) return;
        synchronized (this) {
            if (resolved) return;
            deferred.forEach(r -> r.resolve(registryAccess, byHolder));
            deferred.clear();
            resolved = true;
        }
    }

    private interface Resolver {
        void resolve(RegistryAccess regAccess, Multimap<Holder<?>, Action> sink);
    }
}
