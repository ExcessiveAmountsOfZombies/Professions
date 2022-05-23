package com.epherical.professions.util;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public record LevelDisplay(UUID uuid, int level) {
}
