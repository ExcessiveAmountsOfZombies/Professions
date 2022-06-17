package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.ProfessionsClientForge;
import com.epherical.professions.datapack.CommonProfessionLoader;
import com.epherical.professions.networking.ClientNetworking;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.networking.NetworkHandler;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Map;

public class ForgePlatform extends CommonPlatform<ForgePlatform> {


    @Override
    public ForgePlatform getPlatform() {
        return this;
    }

    @Override
    public boolean isClientEnvironment() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    @Override
    public boolean isServerEnvironment() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    @Override
    public boolean checkPermission(Player player, String perm, int defIntPerm) {
        PermissionAPI.getPermission((ServerPlayer) player, ProfPermissions.CREATIVE_PAYMENT);
        return false;
    }

    @Override
    public boolean checkPermission(Player player, String perm) {
        return false;
    }

    @Override
    public void sendButtonPacket(CommandButtons buttons) {
        NetworkHandler.Client.sendButtonPacket(buttons);
    }

    @Override
    public CommonProfessionLoader getProfessionLoader() {
        return ProfessionsForge.getInstance().getProfessionLoader();
    }

    @Override
    public PlayerManager getPlayerManager() {
        return ProfessionsForge.getInstance().getPlayerManager();
    }

    @Override
    public void sendBeforeActionHandledEvent(ProfessionContext context, ProfessionalPlayer player) {

    }

    @Override
    public void professionJoinEvent(ProfessionalPlayer player, Profession profession, OccupationSlot slot, ServerPlayer serverPlayer) {

    }

    @Override
    public void professionLeaveEvent(ProfessionalPlayer player, Profession profession, ServerPlayer serverPlayer) {

    }

    @Override
    public void sendSyncRequest(ServerPlayer player) {

    }

    @Override
    public ClientNetworking getClientNetworking() {
        return ProfessionsClientForge.clientHandler;
    }

    @Override
    public boolean skipReward(RewardType type) {
        return false;
    }

    @Override
    public Component displayInformation(AbstractAction action, Map<RewardType, Component> map) {
        return Component.translatable(" (%s | %s)",
                map.get(Rewards.EXPERIENCE_REWARD),
                action.extraRewardInformation(map));
    }
}
