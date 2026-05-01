package com.epherical.professions.core;

import com.epherical.professions.ProfessionsCommon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ProfessionCategory(
        String name,
        String description,
        TextColor chatColor,
        List<ResourceKey<Profession>> professions) {

    private static final Codec<ResourceKey<Profession>> PROFESSION_KEY_CODEC = ResourceLocation.CODEC.xmap(
            resourceLocation -> ResourceKey.create(ProfessionsCommon.PROFESSION_REGISTRY_KEY, resourceLocation),
            ResourceKey::location
    );

    public static final Codec<ProfessionCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(ProfessionCategory::name),
            Codec.STRING.fieldOf("description").forGetter(ProfessionCategory::description),
            TextColor.CODEC.fieldOf("chatColor").forGetter(ProfessionCategory::chatColor),
            PROFESSION_KEY_CODEC.listOf().fieldOf("professions").forGetter(ProfessionCategory::professions)
    ).apply(instance, ProfessionCategory::new));

    public boolean hasProfession(Holder<Profession> profession) {
        return profession.unwrapKey().map(professions::contains).orElse(false);
    }
}
