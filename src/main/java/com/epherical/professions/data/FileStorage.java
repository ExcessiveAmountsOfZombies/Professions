package com.epherical.professions.data;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.NullOccupation;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.profession.progression.ProfessionalPlayerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileStorage implements Storage<ProfessionalPlayer, UUID> {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting().disableHtmlEscaping()
            .registerTypeAdapter(ProfessionalPlayerImpl.class, new ProfessionalPlayerImpl.Serializer())
            .registerTypeAdapter(Occupation.class, new Occupation.Serializer())
            .registerTypeAdapter(NullOccupation.class, new NullOccupation.Serializer())
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
        Path path = resolve(uuid);
        try {
            return Files.exists(path) && Files.size(path) > 0 ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public @Nullable ProfessionalPlayer getUser(@Nullable UUID uuid) {
        if (uuid != null && hasUser(uuid)) {
            try (FileReader reader = new FileReader(resolve(uuid).toFile())) {
                ProfessionalPlayerImpl player = GSON.fromJson(reader, ProfessionalPlayerImpl.class);
                player.setUuid(uuid);
                player.resetMaxExperience();
                return player;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return createUser(uuid);
        }
        return null;
    }

    @Override
    public ProfessionalPlayer createUser(@Nullable UUID uuid) {
        if (uuid == null) {
            return null;
        }
        ProfessionalPlayerImpl player = new ProfessionalPlayerImpl(uuid);
        if (ProfessionConfig.autoJoinProfessions) {
            for (Profession profession : ProfessionPlatform.platform.getProfessionLoader().getProfessions()) {
                player.joinOccupation(profession, OccupationSlot.ACTIVE);
            }
        }
        try (FileWriter writer = new FileWriter(resolve(uuid).toFile())) {
            GSON.toJson(player, ProfessionalPlayerImpl.class, writer);
            player.resetMaxExperience();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }

    @Override
    public void saveUser(ProfessionalPlayer player) {
        try {
            FileWriter writer = new FileWriter(resolve(player.getUuid()).toFile());
            GSON.toJson(player, ProfessionalPlayerImpl.class, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isDatabase() {
        return false;
    }

    @Override
    // debugging only
    public List<ProfessionalPlayer> getUsers(int from, int to, Profession profession) {
        List<ProfessionalPlayer> players = new ArrayList<>();
        try {
            Files.walk(basePath).forEach(path -> {
                try {
                    FileReader reader = new FileReader(path.toFile());
                    ProfessionalPlayerImpl player = GSON.fromJson(reader, ProfessionalPlayerImpl.class);
                    String name = path.toFile().getName();
                    player.setUuid(UUID.fromString(name.substring(0, name.length() - 5)));
                    players.add(player);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return players;
    }
}
