package com.epherical.professions.mixin;

import com.epherical.professions.util.mixins.GameProfileHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.GameProfileCache;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Locale;
import java.util.Map;

@Mixin(GameProfileCache.class)
public class GameProfileCacheMixin implements GameProfileHelper {

    @Shadow @Final private Map<String, GameProfileCache.GameProfileInfo> profilesByName;

    @Override
    public @Nullable GameProfile professions$getProfileNoLookup(String name) {
        String string = name.toLowerCase(Locale.ROOT);
        GameProfileCache.GameProfileInfo gameProfileInfo = this.profilesByName.get(string);
        return gameProfileInfo.getProfile();
    }
}
