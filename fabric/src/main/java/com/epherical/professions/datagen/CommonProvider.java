package com.epherical.professions.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class CommonProvider {

    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

}
