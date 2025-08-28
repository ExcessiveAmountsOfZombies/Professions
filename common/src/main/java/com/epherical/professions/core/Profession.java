package com.epherical.professions.core;

import com.epherical.professions.api.IProfession;
import com.epherical.professions.core.actions.Action;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;


public record Profession(
        ResourceLocation key, Component displayName, Component description,
        TextColor professionColor, TextColor descriptionColor, int maxLevel) implements IProfession {


    public static final Codec<Profession> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(Profession::key), // todo; might be able to remove this.
            ComponentSerialization.CODEC.fieldOf("display").forGetter(Profession::displayName),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Profession::description),
            TextColor.CODEC.fieldOf("nameColor").forGetter(Profession::professionColor),
            TextColor.CODEC.fieldOf("descriptionColor").forGetter(Profession::descriptionColor),
            Codec.INT.fieldOf("maxLevel").forGetter(Profession::maxLevel)
    ).apply(instance, Profession::new));


    @Override
    public String displayNameRaw() {
        return displayName.getString();
    }


}
