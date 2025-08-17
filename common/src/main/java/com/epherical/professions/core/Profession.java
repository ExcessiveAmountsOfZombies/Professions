package com.epherical.professions.core;

import com.epherical.professions.api.IProfession;
import com.epherical.professions.core.actions.Action;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record Profession(
        ResourceLocation key, Component displayName, Component description,
        TextColor professionColor, TextColor descriptionColor, int maxLevel,
        List<Action> actions) implements IProfession {


    public static final Codec<Profession> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(Profession::key), // todo; might be able to remove this.
            ComponentSerialization.CODEC.fieldOf("display").forGetter(Profession::displayName),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Profession::description),
            TextColor.CODEC.fieldOf("name_color").forGetter(Profession::professionColor),
            TextColor.CODEC.fieldOf("description_color").forGetter(Profession::descriptionColor),
            Codec.INT.fieldOf("max_level").forGetter(Profession::maxLevel),
            Action.TYPED_CODEC.listOf().fieldOf("actions").forGetter(Profession::actions)
    ).apply(instance, Profession::new));


    // Item
    //  Action
    //  Rewards
    //   Occupation,
    //   Reward
    //     Conditions

    // Can have many per file
    // Item
    //  Occupation
    //    Action
    //    Conditions - to activate
    //    Rewards - for completion


    // One Per File
    // Action
    //  Conditions - to activate
    //  Target - blocks/items/etc
    //  Rewards
    //    Occupation


    @Override
    public String displayNameRaw() {
        return displayName.getString();
    }


}
