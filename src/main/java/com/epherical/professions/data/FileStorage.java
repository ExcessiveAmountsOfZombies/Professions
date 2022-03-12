package com.epherical.professions.data;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.progression.ProfessionalPlayerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileStorage implements Storage<ProfessionalPlayer, UUID> {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting().disableHtmlEscaping()
            .create();

    private final Path basePath;


    public FileStorage(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.basePath = path;
    }

    private Path resolve(UUID uuid) {
        return basePath.resolve(uuid.toString() + ".json");
    }

    @Override
    public boolean hasUser(UUID uuid) {
        return Files.exists(resolve(uuid));
    }

    @Override
    public @Nullable ProfessionalPlayer getUser(UUID uuid) {
        try (FileReader reader = new FileReader(resolve(uuid).toFile())) {
            return GSON.fromJson(reader, ProfessionalPlayerImpl.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ProfessionalPlayer createUser(UUID uuid) {
        ProfessionalPlayer player = new ProfessionalPlayerImpl(uuid);
        try (FileWriter writer = new FileWriter(resolve(uuid).toFile())) {
            GSON.toJson(player, ProfessionalPlayerImpl.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }
}