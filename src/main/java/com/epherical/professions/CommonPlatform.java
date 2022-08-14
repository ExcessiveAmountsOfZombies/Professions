package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.datapack.CommonProfessionLoader;
import com.epherical.professions.networking.ClientNetworking;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.profession.rewards.RewardType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;
import java.util.Map;

public abstract class CommonPlatform<T> {

    public static CommonPlatform<?> platform;

    protected CommonPlatform() {
        platform = this;
    }

    public static <T> void create(CommonPlatform<T> value) {
        platform = value;
    }

    public abstract T getPlatform();

    public abstract boolean isClientEnvironment();

    public abstract boolean isServerEnvironment();

    public abstract MinecraftServer server();

    public abstract boolean checkPermission(Player player, String perm, int defIntPerm);

    public abstract boolean checkDynamicPermission(Player player, String basePerm, String dynamic, int defIntPerm);

    public abstract boolean checkPermission(Player player, String perm);

    public abstract void sendButtonPacket(CommandButtons buttons);

    public abstract CommonProfessionLoader getProfessionLoader();

    public abstract PlayerManager getPlayerManager();

    public abstract void sendBeforeActionHandledEvent(ProfessionContext context, ProfessionalPlayer player);

    public abstract void professionJoinEvent(ProfessionalPlayer player, Profession profession, OccupationSlot slot, ServerPlayer serverPlayer);

    public abstract void professionLeaveEvent(ProfessionalPlayer player, Profession profession, ServerPlayer serverPlayer);

    public abstract void sendSyncRequest(ServerPlayer player);

    public abstract ClientNetworking getClientNetworking();

    public abstract boolean skipReward(RewardType type);

    public abstract Path getRootConfigPath();

    /**
     * Some platforms may not have the same key rewards that should be displayed in /professions info.
     * E.G forge does not have a payment reward, only experience.
     */
    public abstract Component displayInformation(AbstractAction action, Map<RewardType, Component> map);


}
