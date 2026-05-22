package com.epherical.professions.util;

import com.epherical.professions.mixin.client.ClientAdvancementsAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClientAdvancementUtil {

    public static boolean hasAdvancements(List<ResourceLocation> advancementKeys) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection == null) {
            return false;
        }
        
        ClientAdvancements clientAdvancements = connection.getAdvancements();

        for (ResourceLocation advancementKey : advancementKeys) {
            AdvancementHolder advancement = clientAdvancements.get(advancementKey);
            if (advancement == null) {
                return false;
            }

            AdvancementProgress progress = ((ClientAdvancementsAccessor) clientAdvancements).professionsGetProgress().get(advancement);
            return progress != null && progress.isDone();
        }
        return true;
    }

    public static boolean isSingleplayer() {
        return Minecraft.getInstance().isSingleplayer();
    }

}
