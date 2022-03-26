package com.epherical.professions.util;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PacketIdentifiers {
    public static final ResourceLocation OPEN_UI_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "o_ui");
    public static final ResourceLocation OPEN_UI_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "r_ui");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "cl_pb");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "re_pb");
    public static final ResourceLocation JOIN_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "jo_prf");


    @Environment(EnvType.CLIENT)
    public static List<Occupation> occupationsFromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Occupation> occupations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation serializer = buf.readResourceLocation();
            Profession profession = ProfessionConstants.PROFESSION_SERIALIZER.getOptional(serializer).orElseThrow(() -> {
                return new IllegalArgumentException("Unknown profession serializer " + serializer);
            }).fromServer(buf);
            int level = buf.readVarInt();
            double exp = buf.readDouble();
            int maxExp = buf.readVarInt();
            Occupation occupation = new Occupation(profession, exp, maxExp, level);
            occupations.add(occupation);
        }
        return occupations;
    }


    public static void toNetwork(FriendlyByteBuf buf, List<Occupation> occupations) {
        buf.writeResourceLocation(PacketIdentifiers.OPEN_UI_RESPONSE);
        buf.writeVarInt(occupations.size());
        for (Occupation occupation : occupations) {
            Profession profession = occupation.getProfession();
            buf.writeResourceLocation(ProfessionConstants.PROFESSION_SERIALIZER.getKey(profession.getSerializer()));
            profession.getSerializer().toClient(buf, profession);
            buf.writeVarInt(occupation.getLevel());
            buf.writeDouble(occupation.getExp());
            buf.writeVarInt(occupation.getMaxExp());
        }
    }

    @Environment(EnvType.CLIENT)
    public static void clickProfessionButton(ProfessionButtons buttons) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(PacketIdentifiers.CLICK_PROFESSION_BUTTON_REQUEST);
        buf.writeEnum(buttons);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }

    public static void readButtonClick(ServerPlayer player, FriendlyByteBuf buf) {
        ProfessionButtons buttonClicked = buf.readEnum(ProfessionButtons.class);

        if (buttonClicked != null) {
            buttonClicked.handleServer(player);
        }
    }

    public static void readButtonClick(Minecraft minecraft, FriendlyByteBuf buf) {
        ProfessionButtons buttonClicked = buf.readEnum(ProfessionButtons.class);
        if (buttonClicked != null) {
            buttonClicked.handleClient(minecraft, buf);
        }
    }

    public static void attemptJoin(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(JOIN_BUTTON_REQUEST);
        buf.writeResourceLocation(location);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }



    public enum ProfessionButtons {
        // todo: make this better, not a fan. maybe class with registry.
        JOIN {
            @Override
            public void handleClient(Minecraft minecraft, FriendlyByteBuf buf) {
                int size = buf.readVarInt();
                List<Profession> professions = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    ResourceLocation serializer = buf.readResourceLocation();
                    Profession profession = ProfessionConstants.PROFESSION_SERIALIZER.getOptional(serializer).orElseThrow(()
                            -> new IllegalArgumentException("Unknown profession serializer " + serializer)).fromServer(buf);
                    professions.add(profession);
                }
                minecraft.execute(() -> {
                    minecraft.setScreen(new OccupationScreen(Collections.emptyList(), this, professions));
                });
            }

            @Override
            public void handleServer(ServerPlayer player) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeResourceLocation(CLICK_PROFESSION_BUTTON_RESPONSE);
                buf.writeEnum(this);
                Collection<Profession> professions = ProfessionsMod.getInstance().getProfessionLoader().getProfessions();
                buf.writeVarInt(professions.size());
                for (Profession profession : professions) {
                    // todo; kinda repeats
                    buf.writeResourceLocation(ProfessionConstants.PROFESSION_SERIALIZER.getKey(profession.getSerializer()));
                    profession.getSerializer().toClient(buf, profession);
                }
                ServerPlayNetworking.send(player, ProfessionsMod.MOD_CHANNEL, buf);
            }
        },
        LEAVE {
            @Override
            public void handleClient(Minecraft minecraft, FriendlyByteBuf buf) {

            }

            @Override
            public void handleServer(ServerPlayer player) {

            }
        },
        INFO {
            @Override
            public void handleClient(Minecraft minecraft, FriendlyByteBuf buf) {

            }

            @Override
            public void handleServer(ServerPlayer player) {

            }
        },
        TOP {
            @Override
            public void handleClient(Minecraft minecraft, FriendlyByteBuf buf) {

            }

            @Override
            public void handleServer(ServerPlayer player) {

            }
        };

        public abstract void handleClient(Minecraft minecraft, FriendlyByteBuf buf);

        public abstract void handleServer(ServerPlayer player);
    }


}
