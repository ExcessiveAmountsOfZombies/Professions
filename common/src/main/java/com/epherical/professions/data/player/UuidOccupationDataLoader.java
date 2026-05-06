package com.epherical.professions.data.player;

import com.epherical.professions.model.Occupation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UuidOccupationDataLoader extends OccupationDataLoader {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Codec<List<Occupation>> OCCUPATION_LIST_CODEC = Occupation.CODEC.listOf();
    private static final Codec<PlayerOccupationData> PLAYER_OCCUPATION_DATA_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OCCUPATION_LIST_CODEC.fieldOf("occupations").forGetter(PlayerOccupationData::occupations),
            Identifier.CODEC.optionalFieldOf("professionCategory").forGetter(data -> Optional.ofNullable(data.professionCategoryId()))
    ).apply(instance, (occupations, professionCategoryId) -> new PlayerOccupationData(occupations, professionCategoryId.orElse(null))));

    private final Supplier<RegistryAccess> registryAccessSupplier;

    public UuidOccupationDataLoader(Path baseDirectory, Supplier<RegistryAccess> registryAccessSupplier) {
        super(baseDirectory);
        this.registryAccessSupplier = registryAccessSupplier;
    }

    @Override
    public CompletableFuture<PlayerOccupationData> load(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> readFile(uuid, getPath(uuid)));
    }

    @Override
    public CompletableFuture<Map<UUID, PlayerOccupationData>> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            Map<UUID, PlayerOccupationData> loadedOccupations = new HashMap<>();
            try {
                Files.createDirectories(baseDirectory);
                try (var paths = Files.list(baseDirectory)) {
                    paths.filter(Files::isRegularFile)
                            .filter(path -> path.getFileName().toString().endsWith(".json"))
                            .forEach(path -> {
                                UUID uuid = toUUID(path.getFileName().toString());
                                if (uuid == null) {
                                    return;
                                }

                                loadedOccupations.put(uuid, readFile(uuid, path));
                            });
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load occupations from {}", baseDirectory, e);
            }
            return loadedOccupations;
        });
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, Collection<Occupation> occupations, @Nullable Identifier professionCategoryId) {
        return CompletableFuture.runAsync(() -> {
            Path filePath = getPath(uuid);
            try {
                Files.createDirectories(baseDirectory);
                PlayerOccupationData data = new PlayerOccupationData(List.copyOf(occupations), professionCategoryId);
                DataResult<JsonElement> encodedResult = PLAYER_OCCUPATION_DATA_CODEC.encodeStart(JsonOps.INSTANCE, data);
                JsonElement element = encodedResult.resultOrPartial(message ->
                        LOGGER.error("Failed to encode occupations for {}: {}", uuid, message)
                ).orElse(null);

                if (element == null) {
                    return;
                }

                try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                    GSON.toJson(element, writer);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to save occupations for {}", uuid, e);
            }
        });
    }

    private PlayerOccupationData readFile(UUID uuid, Path filePath) {
        if (!Files.exists(filePath)) {
            return PlayerOccupationData.empty();
        }

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            PlayerOccupationData data = decodePlayerData(uuid, filePath, element);
            List<Occupation> occupations = data.occupations();

            RegistryAccess registryAccess = registryAccessSupplier.get();
            for (Occupation occupation : occupations) {
                occupation.resolveProfession(registryAccess);
            }

            return data;
        } catch (IOException e) {
            LOGGER.error("Failed to read occupations for {} from {}", uuid, filePath, e);
            return PlayerOccupationData.empty();
        }
    }

    private PlayerOccupationData decodePlayerData(UUID uuid, Path filePath, JsonElement element) {
        DataResult<PlayerOccupationData> decodedResult = PLAYER_OCCUPATION_DATA_CODEC.parse(JsonOps.INSTANCE, element);
        return decodedResult.resultOrPartial(message ->
                LOGGER.error("Failed to decode occupations for {} from {}: {}", uuid, filePath, message)
        ).orElse(PlayerOccupationData.empty());
    }

    private Path getPath(UUID uuid) {
        return baseDirectory.resolve(uuid + ".json");
    }

    private UUID toUUID(String fileName) {
        String withoutExt = fileName.substring(0, fileName.length() - ".json".length());
        try {
            return UUID.fromString(withoutExt);
        } catch (IllegalArgumentException ignored) {
            LOGGER.warn("Skipping invalid occupation data file name: {}", fileName);
            return null;
        }
    }
}
