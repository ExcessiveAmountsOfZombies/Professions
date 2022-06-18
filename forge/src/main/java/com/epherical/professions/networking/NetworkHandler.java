package com.epherical.professions.networking;

import com.epherical.professions.Constants;
import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.LevelDisplay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.epherical.professions.Constants.*;

public class NetworkHandler {
    private static final String VERSION = "1";
    private static int id = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            MOD_CHANNEL,
            () -> VERSION,
            s -> true, // accept vanilla
            s -> true);


    public NetworkHandler() {
        // fabric equivalent is ClientHandler#sendOccupationPacket
        // then the server handles it as ServerHandler -> OPEN_UI_REQUEST
        // then the server sends its response,
        // then the client reads it in ClientHandler -> OPEN_UI_RESPONSE
        INSTANCE.registerMessage(id++, OpenUI.class, (professionMessage, buf) -> {
            buf.writeResourceLocation(professionMessage.location);
        }, Server.openUI(), (professionMessage, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            ProfessionalPlayer pPlayer = ProfessionsForge.getInstance().getPlayerManager().getPlayer(context.getSender().getUUID());
            if (pPlayer != null) {
                List<Occupation> occupations = pPlayer.getActiveOccupations();
                INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender), new RespondUI(Constants.OPEN_UI_RESPONSE, occupations));
            }
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, Attempt.class, (attempt, buf) -> {
            buf.writeResourceLocation(attempt.subChannel);
            buf.writeResourceLocation(attempt.professionKey);
        }, Server.attemptAction(), (attempt, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            PlayerManager playerManager = ProfessionsForge.getInstance().getPlayerManager();
            ProfessionLoader loader = ProfessionsForge.getInstance().getProfessionLoader();
            ServerPlayer player = context.getSender();
            if (attempt.subChannel.equals(JOIN_BUTTON_REQUEST)) {
                ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
                Profession profession = loader.getProfession(attempt.professionKey);
                playerManager.joinOccupation(pPlayer, profession, OccupationSlot.ACTIVE, player);
            } else if (attempt.subChannel.equals(LEAVE_BUTTON_REQUEST)) {
                ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
                Profession profession = loader.getProfession(attempt.professionKey);
                playerManager.leaveOccupation(pPlayer, profession, player);
            } else if (attempt.subChannel.equals(INFO_BUTTON_REQUEST)) {
                Profession profession = loader.getProfession(attempt.professionKey);
                List<ActionDisplay> displays = new ArrayList<>();
                for (ActionType actionType : RegistryConstants.ACTION_TYPE) {
                    Collection<Action> actionsFor = profession != null ? profession.getActions(actionType) : null;
                    if (actionsFor != null && !actionsFor.isEmpty()) {
                        ActionDisplay display = new ActionDisplay(Component.translatable("=-=-=| %s |=-=-=",
                                Component.translatable(actionType.getTranslationKey()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)), actionsFor);
                        displays.add(display);
                    }
                }
                INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender), new RespondInfo(displays));
            }
            context.setPacketHandled(true);
        });
        INSTANCE.registerMessage(id++, Button.class, (button, buf) -> {
            buf.writeResourceLocation(CLICK_PROFESSION_BUTTON_REQUEST);
            buf.writeEnum(button.button);
        }, Server.buttonClicked(), (button, contextSupplier) -> {
            PlayerManager playerManager = ProfessionsForge.getInstance().getPlayerManager();
            NetworkEvent.Context context = contextSupplier.get();
            ServerPlayer player = context.getSender();
            switch (button.button) {
                case JOIN -> {
                    //FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
                    //buf.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_RESPONSE);
                    //buf.writeEnum(CommandButtons.JOIN);
                    List<Profession> professions = ProfessionsForge.getInstance().getProfessionLoader().getProfessions()
                            .stream()
                            .filter(profession -> pPlayer != null && !pPlayer.isOccupationActive(profession))
                            .toList();
                    //Profession.toNetwork(buf, professions);
                    INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender), new ButtonJoin(professions));
                }
                case LEAVE -> {
                    ProfessionalPlayer pPlayer = playerManager.getPlayer(player.getUUID());
                    if (pPlayer != null) {
                        //FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
                        //response.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_RESPONSE);
                        //response.writeEnum(CommandButtons.LEAVE);
                        List<Occupation> occupations = pPlayer.getActiveOccupations();
                        //occupations.addAll(pPlayer.getInactiveOccupations());
                        //Occupation.toNetwork(response, occupations);
                        INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender), new ButtonLeave(occupations));
                        //ServerPlayNetworking.send(player, Constants.MOD_CHANNEL, response);
                    }
                }
                case TOP -> {
                    /*FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_RESPONSE);
                    buf.writeEnum(CommandButtons.TOP);
                    buf.writeVarInt(playerManager.getPlayers().size());
                    for (ProfessionalPlayer singlePlayer : playerManager.getPlayers()) {
                        buf.writeUUID(singlePlayer.getUuid());
                        buf.writeVarInt(singlePlayer.getActiveOccupations().stream().mapToInt(Occupation::getLevel).sum());
                    }*/
                    INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender), new ButtonTop(playerManager.getPlayers()));
                }
                case INFO -> {
                    INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender),
                            new ButtonInfo(ProfessionsForge.getInstance().getProfessionLoader().getProfessions()));
                }
            }
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, RespondUI.class, (respondUI, buf) -> {
            buf.writeResourceLocation(respondUI.location());
            Occupation.toNetwork(buf, respondUI.occupations(), false);
        }, Client.respondUI(), (respondUI, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(respondUI, contextSupplier)));
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, RespondInfo.class, (respondInfo, buf) -> {
            buf.writeVarInt(respondInfo.displays().size());
            respondInfo.displays().forEach(actionDisplay -> actionDisplay.toNetwork(buf));
        }, Client.respondInfo(), (respondInfo, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(respondInfo, contextSupplier)));
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, ButtonJoin.class, (buttonJoin, buf) -> {
            Profession.toNetwork(buf, buttonJoin.professions());
        }, Client.join(), (buttonJoin, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(buttonJoin, contextSupplier)));
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, ButtonLeave.class, (buttonLeave, buf) -> {
            Occupation.toNetwork(buf, buttonLeave.occupations(), false);
        }, Client.leave(), (buttonLeave, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(buttonLeave, contextSupplier)));
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, ButtonTop.class, (buttonTop, buf) -> {
            buf.writeVarInt(buttonTop.players().size());
            for (ProfessionalPlayer player : buttonTop.players()) {
                buf.writeUUID(player.getUuid());
                buf.writeVarInt(player.getActiveOccupations().stream().mapToInt(Occupation::getLevel).sum());
            }
        }, Client.top(), (buttonTop, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(buttonTop, contextSupplier)));
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, ButtonInfo.class, (buttonInfo, buf) -> {
            Profession.toNetwork(buf, ProfessionsForge.getInstance().getProfessionLoader().getProfessions());
        }, Client.info(), (buttonInfo, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(buttonInfo, contextSupplier)));
            context.setPacketHandled(true);
        });

        // why does this make my head spin
        INSTANCE.registerMessage(id++, SynchronizeRequest.class, (synchronizeRequest, buf) -> {
        }, Client.empty(), (synchronizeRequest, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(synchronizeRequest, contextSupplier, INSTANCE)));
            context.setPacketHandled(true);
        });

        INSTANCE.registerMessage(id++, SynchronizeResponse.class, (synchronizeResponse, buf) -> {
        }, Client.syncResponse(), (synchronizeResponse, contextSupplier) -> {
            PlayerManager playerManager = ProfessionsForge.getInstance().getPlayerManager();
            NetworkEvent.Context context = contextSupplier.get();
            ServerPlayer player = context.getSender();
            INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender), new SyncData(playerManager.synchronizePlayer(player)));
        });

        INSTANCE.registerMessage(id++, SyncData.class, (syncData, buf) -> {
            Occupation.toNetwork(buf, syncData.occupations(), true);
        }, Client.syncData(), (syncData, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientMessages.handlePacket(syncData, contextSupplier)));
            context.setPacketHandled(true);
        });
    }

    /**
     * Methods that return {@link Function} are responses from requests the client made
     * 'r' (Open UI) -> {@link OpenUI Open UI packet gets sent to the server.} -> {@link Server#openUI() Processed in Server#openUI}
     * <br>
     * {@link #INSTANCE} then sends a {@link RespondUI} back to the client and the UI should open.
     */
    public static class Client implements ClientNetworking {

        public static Function<FriendlyByteBuf, RespondUI> respondUI() {
            return buf -> new RespondUI(buf.readResourceLocation(), ProfessionSerializer.fromNetwork(buf));
        }

        public static Function<FriendlyByteBuf, RespondInfo> respondInfo() {
            return buf -> {
                int size = buf.readVarInt();
                List<ActionDisplay> displays = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    displays.add(ActionDisplay.fromNetwork(buf));
                }
                return new RespondInfo(displays);
            };
        }

        public static Function<FriendlyByteBuf, ButtonJoin> join() {
            return buf -> new ButtonJoin(Profession.fromNetwork(buf));
        }


        public static Function<FriendlyByteBuf, ButtonLeave> leave() {
            return buf -> new ButtonLeave(ProfessionSerializer.fromNetwork(buf));
        }

        public static Function<FriendlyByteBuf, ButtonTop> top() {
            return buf -> {
                int read = buf.readVarInt();
                List<LevelDisplay> displays = new ArrayList<>();
                for (int i = 0; i < read; i++) {
                    displays.add(new LevelDisplay(buf.readUUID(), buf.readVarInt()));
                }
                return new ButtonTop(displays);
            };
        }

        public static Function<FriendlyByteBuf, ButtonInfo> info() {
            return buf -> new ButtonInfo(Profession.fromNetwork(buf));
        }

        public static Function<FriendlyByteBuf, SynchronizeResponse> syncResponse() {
            return buf -> new SynchronizeResponse();
        }

        public static Function<FriendlyByteBuf, SyncData> syncData() {
            return buf -> new SyncData(ProfessionSerializer.fromNetwork(buf));
        }

        public static Function<FriendlyByteBuf, SynchronizeRequest> empty() {
            return buf -> new SynchronizeRequest();
        }

        public void sendOccupationPacket() {
            INSTANCE.sendToServer(new OpenUI(Constants.OPEN_UI_REQUEST));
        }

        public void attemptJoinPacket(ResourceLocation location) {
            INSTANCE.sendToServer(new Attempt(Constants.JOIN_BUTTON_REQUEST, location));
        }

        public void attemptLeavePacket(ResourceLocation location) {
            INSTANCE.sendToServer(new Attempt(Constants.LEAVE_BUTTON_REQUEST, location));
        }

        public void attemptInfoPacket(ResourceLocation location) {
            INSTANCE.sendToServer(new Attempt(Constants.INFO_BUTTON_REQUEST, location));
        }

        public static void sendButtonPacket(CommandButtons buttons) {
            INSTANCE.sendToServer(new Button(buttons));
        }
    }

    public static class Server {

        public static Function<FriendlyByteBuf, OpenUI> openUI() {
            return buf -> new OpenUI(buf.readResourceLocation());
        }

        public static Function<FriendlyByteBuf, Attempt> attemptAction() {
            return buf -> new Attempt(buf.readResourceLocation(), buf.readResourceLocation());
        }

        public static Function<FriendlyByteBuf, Button> buttonClicked() {
            return buf -> {
                buf.readResourceLocation();
                return new Button(buf.readEnum(CommandButtons.class));
            };
        }

        public static void sendSyncRequest(ServerPlayer player) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SynchronizeRequest());
        }
    }

    // Serverbound Packets
    public record OpenUI(ResourceLocation location) {}
    public record Attempt(ResourceLocation subChannel, ResourceLocation professionKey) {}
    public record Button(CommandButtons button) {}
    public record SynchronizeResponse() {}

    // Clientbound Packets
    public record RespondUI(ResourceLocation location, List<Occupation> occupations) {}
    public record RespondInfo(List<ActionDisplay> displays) {}
    public record ButtonJoin(List<Profession> professions) {}
    public record ButtonLeave(List<Occupation> occupations) {}
    public record ButtonInfo(Collection<Profession> professions) {}
    public record SynchronizeRequest() {}
    public record SyncData(List<Occupation> occupations) {}

    public static class ButtonTop {
        private Collection<ProfessionalPlayer> players;
        private List<LevelDisplay> displays;

        public ButtonTop(Collection<ProfessionalPlayer> players) {
            this.players = players;
        }

        public ButtonTop(List<LevelDisplay> displays) {
            this.displays = displays;
        }

        public Collection<ProfessionalPlayer> players() {
            return players;
        }

        public List<LevelDisplay> displays() {
            return displays;
        }
    }

}
