package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.ProfessionsClient;
import com.epherical.professions.datapack.CommonProfessionLoader;
import com.epherical.professions.events.OccupationEvents;
import com.epherical.professions.networking.ClientHandler;
import com.epherical.professions.networking.ClientNetworking;
import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.networking.ServerHandler;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class FabricPlatform extends CommonPlatform<FabricPlatform> {


    @Override
    public FabricPlatform getPlatform() {
        return this;
    }

    @Override
    public boolean isClientEnvironment() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isServerEnvironment() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public boolean checkPermission(Player player, String perm, int defIntPerm) {
        return Permissions.check(player, perm, defIntPerm);
    }

    @Override
    public boolean checkDynamicPermission(Player player, String basePerm, String dynamic, int defIntPerm) {
        return Permissions.check(player, basePerm + "." + dynamic, defIntPerm);
    }

    @Override
    public boolean checkPermission(Player player, String perm) {
        return Permissions.check(player, perm);
    }

    @Override
    public void sendButtonPacket(CommandButtons buttons) {
        ClientHandler.sendButtonPacket(buttons);
    }

    @Override
    public CommonProfessionLoader getProfessionLoader() {
        return ProfessionsFabric.getInstance().getProfessionLoader();
    }

    @Override
    public PlayerManager getPlayerManager() {
        return ProfessionsFabric.getInstance().getPlayerManager();
    }

    @Override
    public void sendBeforeActionHandledEvent(ProfessionContext context, ProfessionalPlayer player) {
        OccupationEvents.BEFORE_ACTION_HANDLED_EVENT.invoker().onHandleAction(context, player);
    }

    @Override
    public void professionJoinEvent(ProfessionalPlayer player, Profession profession, OccupationSlot slot, ServerPlayer serverPlayer) {
        OccupationEvents.PROFESSION_JOIN_EVENT.invoker().onProfessionJoin(player, profession, slot, serverPlayer);
    }

    @Override
    public void professionLeaveEvent(ProfessionalPlayer player, Profession profession, ServerPlayer serverPlayer) {
        OccupationEvents.PROFESSION_LEAVE_EVENT.invoker().onProfessionLeave(player, profession, serverPlayer);
    }

    @Override
    public void sendSyncRequest(ServerPlayer player) {
        ServerHandler.sendSyncRequest(player);
    }

    @Override
    public ClientNetworking getClientNetworking() {
        return ProfessionsClient.clientHandler;
    }

    @Override
    public boolean skipReward(RewardType type) {
        return type.equals(FabricRegConstants.PAYMENT_REWARD);
    }

    @Override
    public Component displayInformation(AbstractAction action, Map<RewardType, Component> map) {
        return Component.translatable(" (%s | %s%s)",
                map.get(FabricRegConstants.PAYMENT_REWARD),
                map.get(Rewards.EXPERIENCE_REWARD),
                action.extraRewardInformation(map));
    }
}
