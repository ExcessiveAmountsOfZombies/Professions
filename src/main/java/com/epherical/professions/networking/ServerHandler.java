package com.epherical.professions.networking;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.util.ActionDisplay;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerHandler {
    private static final Map<ResourceLocation, Handler> subChannelReceivers = new HashMap<>();
    private static final Map<CommandButtons, CommandButtonHandler> buttonReceivers = new HashMap<>();

    public static void receivePacket(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, FriendlyByteBuf buf, PacketSender responseSender) {
        PlayerManager playerManager = ProfessionsMod.getInstance().getPlayerManager();
        ResourceLocation subChannel = buf.readResourceLocation();
        Handler handler = subChannelReceivers.getOrDefault(subChannel, null);
        if (handler != null) {
            handler.handle(server, player, listener, buf, responseSender, playerManager);
        }
    }

    static {
        setupSubChannels();
        setupButtonHandlers();
    }

    private static void setupButtonHandlers() {
        buttonReceivers.put(CommandButtons.JOIN, (player) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            PlayerManager playerManager = ProfessionsMod.getInstance().getPlayerManager();
            ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
            buf.writeResourceLocation(ProfessionConstants.CLICK_PROFESSION_BUTTON_RESPONSE);
            buf.writeEnum(CommandButtons.JOIN);
            Collection<Profession> professions = ProfessionsMod.getInstance().getProfessionLoader().getProfessions()
                    .stream()
                    .filter(profession -> pPlayer != null && !pPlayer.isOccupationActive(profession))
                    .toList();
            Profession.toNetwork(buf, professions);
            ServerPlayNetworking.send(player, ProfessionsMod.MOD_CHANNEL, buf);
        });
        buttonReceivers.put(CommandButtons.LEAVE, player -> {
            ProfessionalPlayer pPlayer = ProfessionsMod.getInstance().getPlayerManager().getPlayer(player.getUUID());
            if (pPlayer != null) {
                FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
                response.writeResourceLocation(ProfessionConstants.CLICK_PROFESSION_BUTTON_RESPONSE);
                response.writeEnum(CommandButtons.LEAVE);
                List<Occupation> occupations = pPlayer.getActiveOccupations();
                //occupations.addAll(pPlayer.getInactiveOccupations());
                Occupation.toNetwork(response, occupations);
                ServerPlayNetworking.send(player, ProfessionsMod.MOD_CHANNEL, response);
            }
        });
        buttonReceivers.put(CommandButtons.INFO, player -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(ProfessionConstants.CLICK_PROFESSION_BUTTON_RESPONSE);
            buf.writeEnum(CommandButtons.INFO);
            Profession.toNetwork(buf, ProfessionsMod.getInstance().getProfessionLoader().getProfessions());
            ServerPlayNetworking.send(player, ProfessionsMod.MOD_CHANNEL, buf);
        });
    }

    private static void setupSubChannels() {
        subChannelReceivers.put(ProfessionConstants.OPEN_UI_REQUEST, (server, player, handler, buf, responseSender, playerManager) -> {
            ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
            if (pPlayer != null) {
                FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
                List<Occupation> occupations = pPlayer.getActiveOccupations();
                //occupations.addAll(pPlayer.getInactiveOccupations());
                response.writeResourceLocation(ProfessionConstants.OPEN_UI_RESPONSE);
                Occupation.toNetwork(response, occupations);
                responseSender.sendPacket(ProfessionsMod.MOD_CHANNEL, response);
            }
        });
        subChannelReceivers.put(ProfessionConstants.JOIN_BUTTON_REQUEST, (server, player, listener, buf, responseSender, playerManager) -> {
            ProfessionLoader loader = ProfessionsMod.getInstance().getProfessionLoader();
            ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
            Profession profession = loader.getProfession(buf.readResourceLocation());
            playerManager.joinOccupation(pPlayer, profession, OccupationSlot.ACTIVE, player);
        });
        subChannelReceivers.put(ProfessionConstants.LEAVE_BUTTON_REQUEST, (server, player, listener, buf, responseSender, playerManager) -> {
            ProfessionLoader loader = ProfessionsMod.getInstance().getProfessionLoader();
            ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
            Profession profession = loader.getProfession(buf.readResourceLocation());
            playerManager.leaveOccupation(pPlayer, profession, player);
        });
        subChannelReceivers.put(ProfessionConstants.INFO_BUTTON_REQUEST, (server, player, listener, buf, responseSender, playerManager) -> {
            ProfessionLoader loader = ProfessionsMod.getInstance().getProfessionLoader();
            Profession profession = loader.getProfession(buf.readResourceLocation());
            FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
            Collection<ActionDisplay> displays = new ArrayList<>();
            for (ActionType actionType : ProfessionConstants.ACTION_TYPE) {
                Collection<Action> actionsFor = profession != null ? profession.getActions(actionType) : null;
                if (actionsFor != null && !actionsFor.isEmpty()) {
                    ActionDisplay display = new ActionDisplay(new TranslatableComponent("=-=-=| %s |=-=-=",
                            new TranslatableComponent(actionType.getTranslationKey()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)), actionsFor);
                    displays.add(display);
                }
            }
            response.writeResourceLocation(ProfessionConstants.INFO_BUTTON_RESPONSE);
            response.writeVarInt(displays.size());
            displays.forEach(actionDisplay -> actionDisplay.toNetwork(response));
            responseSender.sendPacket(ProfessionsMod.MOD_CHANNEL, response);
        });
        subChannelReceivers.put(ProfessionConstants.CLICK_PROFESSION_BUTTON_REQUEST, (server, player, listener, buf, responseSender, playerManager) -> {
            CommandButtons buttonClicked = buf.readEnum(CommandButtons.class);
            CommandButtonHandler buttonHandler = buttonReceivers.getOrDefault(buttonClicked, null);
            if (buttonHandler != null) {
                buttonHandler.handle(player);
            }
        });
    }

    public interface Handler {
        void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener,
                    FriendlyByteBuf buf, PacketSender responseSender, PlayerManager playerManager);
    }

    public interface CommandButtonHandler {
        void handle(ServerPlayer player);
    }


}
