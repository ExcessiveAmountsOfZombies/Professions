package com.epherical.professions;

import com.epherical.octoecon.api.Economy;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.ProfessionsClientForge;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.networking.ClientNetworking;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.networking.NetworkHandler;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.epherical.professions.util.ProfPermissions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.nio.file.Path;
import java.util.Map;

public class ForgePlatform extends ProfessionPlatform<ForgePlatform> {


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
    public MinecraftServer server() {
        return ProfessionsForge.getInstance().getMinecraftServer();
    }

    @Override
    public Economy economy() {
        return ProfessionsForge.getInstance().getEconomy();
    }

    @Override
    public boolean checkPermission(Player player, String perm, int defIntPerm) {
        return PermissionAPI.getPermission((ServerPlayer) player, ProfPermissions.getNode(perm, Boolean.TYPE));
    }

    @Override
    public boolean checkDynamicPermission(Player player, String basePerm, String dynamic, int defIntPerm) {
        PermissionNode<Boolean> perm = ProfPermissions.getNode(basePerm, Boolean.TYPE);
        // todo: this isn't great, but it's our only dynamic currently so i'm not going to figure out how to abstract it more.
        // maybe the key becomes a parameter, and a type  so we can keep the type.
        return PermissionAPI.getPermission((ServerPlayer) player, perm, ProfPermissions.PROFESSION_CONTEXT.createContext(dynamic));
    }

    @Override
    public boolean checkPermission(Player player, String perm) {
        return PermissionAPI.getPermission((ServerPlayer) player, ProfPermissions.getNode(perm, Boolean.TYPE));
    }

    @Override
    public void sendButtonPacket(CommandButtons buttons) {
        NetworkHandler.Client.sendButtonPacket(buttons);
    }

    @Override
    public ProfessionLoader getProfessionLoader() {
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
    public void datapackFinishedLoadingEvent() {

    }

    @Override
    public void sendSyncRequest(ServerPlayer player) {
        NetworkHandler.Server.sendSyncRequest(player);
    }

    @Override
    public ClientNetworking getClientNetworking() {
        return ProfessionsClientForge.clientHandler;
    }

    @Override
    public boolean skipReward(RewardType type) {
        return type.equals(Rewards.PAYMENT_REWARD);
    }

    @Override
    public Path getRootConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Component displayInformation(AbstractAction action, Map<RewardType, Component> map) {
        return Component.translatable(" (%s | %s%s)",
                map.get(Rewards.PAYMENT_REWARD),
                map.get(Rewards.EXPERIENCE_REWARD),
                action.extraRewardInformation(map));
    }
}
