package com.epherical.professions.util;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PacketIdentifiers {
    public static final ResourceLocation OPEN_UI = new ResourceLocation(ProfessionsMod.MOD_ID, "o_ui");
    public static final ResourceLocation RESPOND_UI = new ResourceLocation(ProfessionsMod.MOD_ID, "r_ui");
    public static final ResourceLocation CLICK_PB = new ResourceLocation(ProfessionsMod.MOD_ID, "cl_pb");


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
        buf.writeResourceLocation(PacketIdentifiers.RESPOND_UI);
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
    public static void clickProfessionButton(int id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(PacketIdentifiers.CLICK_PB);
        buf.writeVarInt(id);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }

    public static void readButtonClick() {

    }



    public enum ProfessionButton {
        OPEN,
        LEAVE,
        INFO,
        STATS,
        TOP
    }


}
