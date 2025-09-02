package com.epherical.professions.core;

import com.epherical.professions.api.IProfession;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.mbertoli.jfep.Parser;

import java.util.List;


public record Profession(
        ResourceLocation key, Component displayName, List<String> description,
        TextColor professionColor, TextColor descriptionColor, int maxLevel,
        Parser experienceScalingEquation) implements IProfession {

    public static final Codec<Profession> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(Profession::key), // todo; might be able to remove this.
            ComponentSerialization.CODEC.fieldOf("display").forGetter(Profession::displayName),
            Codec.STRING.listOf().fieldOf("description").forGetter(Profession::description),
            TextColor.CODEC.fieldOf("nameColor").forGetter(Profession::professionColor),
            TextColor.CODEC.fieldOf("descriptionColor").forGetter(Profession::descriptionColor),
            Codec.INT.fieldOf("maxLevel").forGetter(Profession::maxLevel),
            Codec.STRING.fieldOf("expScalingEquation").forGetter(profession -> profession.experienceScalingEquation.getExpression())
    ).apply(instance, (resourceLocation, component,
                       component2, textColor, textColor2,
                       integer, s) ->
            new Profession(resourceLocation, component, component2, textColor, textColor2, integer, new Parser(s))));


    @Override
    public String displayNameRaw() {
        return displayName.getString();
    }

    public double getExperienceForLevel(int level) {
        experienceScalingEquation.setVariable("lvl", level);
        return experienceScalingEquation.getValue();
    }


}
