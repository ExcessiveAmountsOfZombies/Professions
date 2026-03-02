package com.epherical.professions.data.player;

import com.epherical.professions.core.progression.Occupation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UuidOccupationDataLoader extends OccupationDataLoader {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Codec<List<Occupation>> OCCUPATION_LIST_CODEC = Occupation.CODEC.listOf();

    private final Supplier<RegistryAccess> registryAccessSupplier;

    public UuidOccupationDataLoader(Path baseDirectory, Supplier<RegistryAccess> registryAccessSupplier) {
        super(baseDirectory);
        this.registryAccessSupplier = registryAccessSupplier;
    }

    @Override
    public CompletableFuture<List<Occupation>> load(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> readFile(uuid, getPath(uuid)));
    }

    @Override
    public CompletableFuture<Map<UUID, List<Occupation>>> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            Map<UUID, List<Occupation>> loadedOccupations = new HashMap<>();
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
    public CompletableFuture<Void> save(UUID uuid, Collection<Occupation> data) {
        return CompletableFuture.runAsync(() -> {
            Path filePath = getPath(uuid);
            try {
                Files.createDirectories(baseDirectory);
                DataResult<JsonElement> encodedResult = OCCUPATION_LIST_CODEC.encodeStart(JsonOps.INSTANCE, List.copyOf(data));
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

    private List<Occupation> readFile(UUID uuid, Path filePath) {
        if (!Files.exists(filePath)) {
            return List.of();
        }

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            DataResult<List<Occupation>> decodedResult = OCCUPATION_LIST_CODEC.parse(JsonOps.INSTANCE, element);
            List<Occupation> occupations = decodedResult.resultOrPartial(message ->
                    LOGGER.error("Failed to decode occupations for {} from {}: {}", uuid, filePath, message)
            ).orElse(List.of());

            RegistryAccess registryAccess = registryAccessSupplier.get();
            for (Occupation occupation : occupations) {
                occupation.resolveProfession(registryAccess);
            }

            return occupations;
        } catch (IOException e) {
            LOGGER.error("Failed to read occupations for {} from {}", uuid, filePath, e);
            return List.of();
        }
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

