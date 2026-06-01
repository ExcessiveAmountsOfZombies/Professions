package com.epherical.professions.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.listener.client.FabricGateListenerClient;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.ExperienceNotificationHandler;
import com.epherical.professions.networking.client.ExperienceOccupationSyncHandler;
import com.epherical.professions.networking.client.PlayerActionsSyncPayloadHandler;
import com.epherical.professions.networking.client.PlayerDataSyncPayloadHandler;
import com.epherical.professions.networking.client.PlayerGatesSyncPayloadHandler;
import com.epherical.professions.networking.client.PlayerPerksSyncPayloadHandler;
import com.epherical.professions.networking.client.ProfessionCategorySyncPayloadHandler;
import com.epherical.professions.networking.server.S2CCategorySyncPayload;
import com.epherical.professions.networking.server.S2CExperienceGainPayload;
import com.epherical.professions.networking.server.S2CPlayerActionsSyncPayload;
import com.epherical.professions.networking.server.S2CPlayerDataSyncPayload;
import com.epherical.professions.networking.server.S2CPlayerGatesSyncPayload;
import com.epherical.professions.networking.server.S2CPlayerPerksSyncPayload;
import com.epherical.professions.presentation.client.gui.screen.OccupationCategorySelectionScreen;
import com.epherical.professions.presentation.client.gui.screen.OccupationMenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class FabricClientInitializer implements ClientModInitializer {

    private static KeyMapping occupationMenu;
    private static final Identifier PROFESSION_XP =
            Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "profession_xp");

    @Override
    public void onInitializeClient() {
        NetworkPayloadDispatcher.setServerboundPayloadSender(ClientPlayNetworking::send);
        FabricGateListenerClient.register();

        ClientPlayNetworking.registerGlobalReceiver(S2CExperienceGainPayload.TYPE, (payload, context) -> {
            ExperienceOccupationSyncHandler.handle(payload);
            ExperienceNotificationHandler.handle(payload);
        });
        ClientPlayNetworking.registerGlobalReceiver(S2CCategorySyncPayload.TYPE,
                (payload, context) -> ProfessionCategorySyncPayloadHandler.handle(payload));
        ClientPlayNetworking.registerGlobalReceiver(S2CPlayerDataSyncPayload.TYPE,
                (payload, context) -> PlayerDataSyncPayloadHandler.handle(payload));
        ClientPlayNetworking.registerGlobalReceiver(S2CPlayerActionsSyncPayload.TYPE,
                (payload, context) -> PlayerActionsSyncPayloadHandler.handle(payload));
        ClientPlayNetworking.registerGlobalReceiver(S2CPlayerPerksSyncPayload.TYPE,
                (payload, context) -> PlayerPerksSyncPayloadHandler.handle(payload));
        ClientPlayNetworking.registerGlobalReceiver(S2CPlayerGatesSyncPayload.TYPE,
                (payload, context) -> PlayerGatesSyncPayloadHandler.handle(payload));


        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "category")
        );

        occupationMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.professions.open_occupation_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (occupationMenu.consumeClick()) {
                openMenu(client);
            }
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                PROFESSION_XP,
                ExperienceNotificationHandler::render
        );
    }

    private static void openMenu(Minecraft minecraft) {
        if (minecraft.player == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = ProfessionsCommon.INSTANCE.getPlayerManager().getPlayer(minecraft.player.getUUID());
        if (professionalPlayer == null) {
            return;
        }

        if (professionalPlayer.getCategory() != null) {
            List<Occupation> activeOccupations = professionalPlayer.getActiveOccupations();
            minecraft.setScreen(new OccupationMenuScreen(activeOccupations));
            return;
        }

        minecraft.setScreen(new OccupationCategorySelectionScreen(ProfessionsCommon.INSTANCE.getCategoryManager().getCategories()));
    }
}
