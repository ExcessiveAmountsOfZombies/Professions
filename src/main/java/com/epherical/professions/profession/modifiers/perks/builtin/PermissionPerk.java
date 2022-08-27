package com.epherical.professions.profession.modifiers.perks.builtin;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.modifiers.perks.PerkType;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.profession.modifiers.perks.Perks.PERMISSION_PERK_TYPE;

public class PermissionPerk implements Perk {

    protected final String permission;
    protected final int level;

    public PermissionPerk(String permission, int level) {
        this.permission = permission;
        this.level = level;
    }

    @Override
    public PerkType getType() {
        return PERMISSION_PERK_TYPE;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean applyPerkToPlayer(String permission, ProfessionalPlayer context, Occupation occupation) {
        return occupation.getLevel() >= level && permission.equalsIgnoreCase(this.permission);
    }


    public static class PerkSerializer implements Serializer<PermissionPerk> {

        @Override
        public void serialize(JsonObject jsonObject, PermissionPerk perk, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("level", perk.level);
            jsonObject.addProperty("permission", perk.permission);
        }

        @Override
        public PermissionPerk deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int level = GsonHelper.getAsInt(jsonObject, "level");
            String permission = GsonHelper.getAsString(jsonObject, "permission");
            return new PermissionPerk(permission, level);
        }
    }
}
