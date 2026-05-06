package com.epherical.professions.core;

import com.epherical.professions.ProfessionsCommon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ProfessionCategory {

    private final String name;
    private final String description;
    private final TextColor chatColor;
    private final List<ResourceKey<Profession>> professions;
    private final Map<String, Dynamic<?>> features;
    private @Nullable ResourceLocation fileId;

    private static final Codec<ResourceKey<Profession>> PROFESSION_KEY_CODEC = Identifier.CODEC.xmap(
            resourceLocation -> ResourceKey.create(ProfessionsCommon.PROFESSION_REGISTRY_KEY, resourceLocation),
            ResourceKey::identifier
    );
    private static final Codec<Map<String, Dynamic<?>>> FEATURES_CODEC = Codec.unboundedMap(Codec.STRING, Codec.PASSTHROUGH);

    private static final MapCodec<ProfessionCategory> BASE_FIELDS_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(ProfessionCategory::name),
            Codec.STRING.fieldOf("description").forGetter(ProfessionCategory::description),
            TextColor.CODEC.fieldOf("chatColor").forGetter(ProfessionCategory::chatColor),
            PROFESSION_KEY_CODEC.listOf().fieldOf("professions").forGetter(ProfessionCategory::professions),
            FEATURES_CODEC.optionalFieldOf("features", Map.of()).forGetter(ProfessionCategory::features)
    ).apply(instance, ProfessionCategory::new));

    public static final Codec<ProfessionCategory> CODEC = BASE_FIELDS_CODEC.codec();

    public static final Codec<ProfessionCategory> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BASE_FIELDS_CODEC.forGetter(category -> category),
            ResourceLocation.CODEC.optionalFieldOf("fileId").forGetter(category -> Optional.ofNullable(category.getFileId()))
    ).apply(instance, (category, fileId) -> {
        fileId.ifPresent(category::setId);
        return category;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProfessionCategory> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(NETWORK_CODEC);



    public ProfessionCategory(String name, String description, TextColor chatColor, List<ResourceKey<Profession>> professions,
                              Map<String, Dynamic<?>> features) {
        this.name = name;
        this.description = description;
        this.chatColor = chatColor;
        this.professions = professions;
        this.features = features == null ? Map.of() : Map.copyOf(features);
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public TextColor chatColor() {
        return chatColor;
    }

    public List<ResourceKey<Profession>> professions() {
        return professions;
    }

    public Map<String, Dynamic<?>> features() {
        return features;
    }

    public boolean hasProfession(Holder<Profession> profession) {
        return profession.unwrapKey().map(professions::contains).orElse(false);
    }

    public <T> DataResult<T> getFeatureValue(Codec<T> codec, String key) {
        Dynamic<?> value = features.get(key);
        if (value == null) {
            return DataResult.error(() ->  "Missing feature value for key '" + key + "'");
        }
        return codec.parse(value);
    }

    public <T> DataResult<T> getFeatureValueOrDefault(Codec<T> codec, String key, T defaultValue) {
        Dynamic<?> value = features.get(key);
        if (value == null) {
            return DataResult.success(defaultValue);
        }

        return codec.parse(value);
    }

    // todo; i'd like to find a better way to handle this.
    public @Nullable ResourceLocation getFileId() {
        return fileId;
    }

    public void setId(ResourceLocation fileId) {
        if (this.fileId != null && !this.fileId.equals(fileId)) {
            throw new IllegalStateException("Category file id already set to " + this.fileId + ", cannot reset to " + fileId);
        }
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfessionCategory that)) {
            return false;
        }
        if (fileId != null || that.fileId != null) {
            return Objects.equals(fileId, that.fileId);
        }
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(chatColor, that.chatColor)
                && Objects.equals(professions, that.professions)
                && Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        if (fileId != null) {
            return fileId.hashCode();
        }
        return Objects.hash(name, description, chatColor, professions, features);
    }
}
