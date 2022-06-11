package com.epherical.professions.client;

import com.epherical.professions.Constants;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.networking.ClientHandler;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ProfessionUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ProfessionsClient implements ClientModInitializer {

    private static KeyMapping occupationMenu;
    private static KeyMapping professionData;

    @Override
    public void onInitializeClient() {
        occupationMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.professions.open_occupation_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.professions.occupation"
        ));
        professionData = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.professions.show_info",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.professions.occupation"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (occupationMenu.isDown()) {
                if (client.isLocalServer()) {
                    ProfessionalPlayer player = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(client.player.getUUID());
                    client.setScreen(new OccupationScreen<>(player.getActiveOccupations(), client, OccupationScreen::createOccupationEntries, null));
                } else {
                    ClientHandler.sendOccupationPacket();
                }

            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!client.hasSingleplayerServer()) {
                ProfessionsFabric.getInstance().getPlayerManager().playerClientQuit(client.player.getUUID());
            }
        });
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null) {
                return;
            }
            ProfessionalPlayer pPlayer = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(player.getUUID());
            if (pPlayer == null) {
                return;
            }
            Item item = stack.getItem();
            Component locked = null;
            if (item instanceof BlockItem blockItem) {
                Pair<Unlock.Singular<Block>, Boolean> pair = ProfessionUtil.canUse(pPlayer, Unlocks.BLOCK_UNLOCK, blockItem.getBlock());
                if (!pair.getSecond()) {
                    Unlock.Singular<Block> singular = pair.getFirst();

                    locked = new TranslatableComponent("professions.tooltip.drop_req",
                            singular.getProfessionDisplay(),
                            new TextComponent(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
                }
            } else {
                Pair<Unlock.Singular<Item>, Boolean> pair = ProfessionUtil.canUse(pPlayer, Unlocks.TOOL_UNLOCK, item);
                if (!pair.getSecond()) {
                    Unlock.Singular<Item> singular = pair.getFirst();
                    locked = new TranslatableComponent("professions.tooltip.use_req",
                            singular.getProfessionDisplay(),
                            new TextComponent(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors));
                }
            }

            boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(professionData).getValue());

            if (!isKeyDown && locked != null) {
                lines.add(new TranslatableComponent("Hold %s to see Professions info", KeyBindingHelper.getBoundKeyOf(professionData).getDisplayName()));
            } else if (locked != null) {
                lines.add(locked);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.MOD_CHANNEL, ClientHandler::receivePacket);
    }
}
