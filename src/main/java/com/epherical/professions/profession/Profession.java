package com.epherical.professions.profession;

import com.epherical.professions.profession.action.Action;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class Profession {
    private final TextColor color = TextColor.parseColor("#FF0000");
    private final TextColor descriptionColor = TextColor.parseColor("#FFFFFF");
    private final String[] description = null;
    private final String displayName = null;
    private final int maxLevel = 100;
    private final Action[] actions = null;



    public static class Serializer implements ProfessionSerializer<Profession> {

        @Override
        public Profession deserialize(ResourceLocation id, JsonObject profession) {
            return null;
        }
    }
}
