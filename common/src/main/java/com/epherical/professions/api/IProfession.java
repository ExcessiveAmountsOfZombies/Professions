package com.epherical.professions.api;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IProfession {

    TextColor professionColor();

    TextColor descriptionColor();

    List<String> description();

    String displayNameRaw();

    Component displayName();

    int maxLevel();

    ResourceLocation key();




}
