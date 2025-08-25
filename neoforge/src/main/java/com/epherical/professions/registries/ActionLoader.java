package com.epherical.professions.registries;

import com.epherical.professions.core.actions.Action;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class ActionLoader implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    private static final String PATH = "actions";

    private final RegistryAccess registry;
    private Map<?, ?> actions;


    public ActionLoader(RegistryAccess registryAccess) {
        this.registry = registryAccess;
    }


    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier prepBarrier, @NotNull ResourceManager resourceManager,
                                                   @NotNull ProfilerFiller prepProfiler, @NotNull ProfilerFiller reloadProfiler,
                                                   @NotNull Executor backgroundExecutor, @NotNull Executor gameExecution) {

        return this.load(resourceManager, gameExecution)
                .thenCompose(prepBarrier::wait)
                .thenAcceptAsync(map -> actions = map, gameExecution);
    }


    private CompletableFuture<Map<?, ?>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> load(registry, manager));
    }

    private static Map<?, ?> load(RegistryAccess registryAccess, ResourceManager manager) {
        RegistryOps<JsonElement> op = RegistryOps.create(JsonOps.INSTANCE, registryAccess);

        final Map<ResourceKey<? extends Registry<?>>, ?> loaded = new HashMap<>();
        registryAccess.registries().forEach(registry -> {
            ResourceKey<? extends Registry<?>> resourceKey = registry.key();
            // actions/minecraft/item
            FileToIdConverter converter = FileToIdConverter.json(PATH + "/" + getFolder(resourceKey.location()));
            // actions/minecraft/item
            //    silly.json
            //    bozo.json
            //    dingus.json
            for (Map.Entry<ResourceLocation, List<Resource>> entry : converter.listMatchingResourceStacks(manager).entrySet()) {
                // professions:actions/miencraft/item/shiddd.json
                ResourceLocation aFacsimileOfTheRealDeal = entry.getKey();
                // turns into professions:shiddd.json
                ResourceLocation realFile = converter.fileToId(aFacsimileOfTheRealDeal);

                List<ActionOf<?>> list = parseFile(op, (ResourceKey) resourceKey, entry.getValue());


                // ExtraCodecs

            }

        });

        return null;
    }

    public static String getFolder(ResourceLocation rl) {
        return rl.getNamespace() + "/" + rl.getPath();
    }


    private static <T> List<ActionOf<T>> parseFile(RegistryOps<JsonElement> op, ResourceKey<Registry<T>> registry, List<Resource> resources) {
        // this is very much heavily inspired by the datamaps in neoforge
        Codec<Either<TagKey<T>, ResourceKey<T>>> eitherTagElement = ExtraCodecs.TAG_OR_ELEMENT_ID.xmap(
                rl -> rl.tag() ? Either.left(TagKey.create(registry, rl.id()))
                        : Either.right(ResourceKey.create(registry, rl.id())),
                e -> e.map(tk -> new ExtraCodecs.TagOrElementLocation(tk.location(), true),
                        rk -> new ExtraCodecs.TagOrElementLocation(rk.location(), false))
        );


        Codec<SingleAction<T>> singleActionCodec = RecordCodecBuilder.create(inst -> inst.group(
                eitherTagElement.fieldOf("entry").forGetter(SingleAction::entry),
                Action.TYPED_CODEC.listOf().fieldOf("actions").forGetter(SingleAction::actions)
        ).apply(inst, SingleAction::new));

        Codec<Map<Either<TagKey<T>, ResourceKey<T>>, SingleAction<T>>> valuesCodec =
                singleActionCodec.listOf().xmap(
                        list -> list.stream()
                                .collect(toMap(SingleAction::entry, Function.identity())),
                        map  -> new java.util.ArrayList<>(map.values())
                );

        Codec<ActionOf<T>> codec = RecordCodecBuilder.create(inst -> inst.group(
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(ActionOf::replace),
                valuesCodec.fieldOf("values").forGetter(ActionOf::values)
        ).apply(inst, ActionOf::new));


        List<ActionOf<T>> actions = new ArrayList<>();
        for (Resource resource : resources) {
            try (Reader reader = resource.openAsReader()) {
                JsonElement element = GSON.fromJson(reader, JsonElement.class);
                actions.add(codec.decode(op, element).getOrThrow().getFirst());
            } catch (Exception e) {
                LOGGER.warn("Couldn't finish reading resource file! {}", registry, e);
            }
        }

        return actions;
    }


}
