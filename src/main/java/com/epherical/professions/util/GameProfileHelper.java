package com.epherical.professions.util;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

public interface GameProfileHelper {

    @Nullable GameProfile getProfileNoLookup(String name);

}
