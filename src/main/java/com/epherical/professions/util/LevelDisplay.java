package com.epherical.professions.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public record LevelDisplay(UUID uuid, int level) {
}
