package com.epherical.professions.api;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public interface IProfession {

    TextColor professionColor();

    TextColor descriptionColor();

    Component description();

    String displayNameRaw();

    Component displayName();

    int maxLevel();

    ResourceLocation key();




}
