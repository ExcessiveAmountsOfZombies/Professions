package com.epherical.professions.client;

import com.epherical.professions.Constants;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.networking.ClientHandler;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.profession.unlock.Unlocks;
import com.mojang.blaze3d.platform.InputConstants;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ProfessionsClient implements ClientModInitializer {

    private static KeyMapping occupationMenu;
    private static KeyMapping professionData;

    public static ClientHandler clientHandler;

    @Override
    public void onInitializeClient() {
        clientHandler = new ClientHandler();
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
                    clientHandler.sendOccupationPacket();
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
            List<Component> comps = new ArrayList<>();
            if (item instanceof BlockItem blockItem) {
                // TODO: ok this right here will show our code is really screwed up. we can only have one unlock type for one profession for one block/item.
                //  so we need to rewrite some of the UnlockableDataImpl code to handle MULTIPLE unlocks at once. Maybe you can break a block
                //  at level 2 but you can only receive the drops at level 3.
                List<Unlock.Singular<Block>> list = pPlayer.getLockedKnowledge(blockItem.getBlock());
                for (Unlock.Singular<Block> singular : list) {
                    if (!singular.canUse(pPlayer)) {
                        comps.add(Component.translatable(singular.getType().getTranslationKey(),
                                        singular.getProfessionDisplay(),
                                        Component.literal(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)));
                    }
                }
                /*for (UnlockType<?> unlock : RegistryConstants.UNLOCKS) {
                    if (unlock.getType().equals(Block.class)) {
                        // poorly designed? maybe. I'm not sure of another way to get the locked knowledge of only block unlock types though.
                        // the reason we do this: block unlock types rely on block tags.
                        UnlockType<Block> blockUnlockType = (UnlockType<Block>) unlock;

                        pPlayer.getLockedKnowledge(blockItem.getBlock(), unlock)
                        List<Unlock.Singular<Block>> lockedKnowledge = pPlayer.getLockedKnowledge(blockUnlockType, blockItem.getBlock());
                        for (Unlock.Singular<Block> singular : lockedKnowledge) {

                        }
                    }
                }*/
            } else {
                List<Unlock.Singular<Item>> lockedKnowledge = pPlayer.getLockedKnowledge(Unlocks.TOOL_UNLOCK, item);
                for (Unlock.Singular<Item> singular : lockedKnowledge) {
                    if (!singular.canUse(pPlayer)) {
                        comps.add(Component.translatable("professions.tooltip.use_req",
                                        singular.getProfessionDisplay(),
                                        Component.literal(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                                .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)));
                    }
                }
            }

            boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(professionData).getValue());

            if (!isKeyDown && !comps.isEmpty()) {
                lines.add(Component.translatable("Hold %s to see Professions info", KeyBindingHelper.getBoundKeyOf(professionData).getDisplayName()));
            } else if (!comps.isEmpty()) {
                lines.addAll(comps);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.MOD_CHANNEL, ClientHandler::receivePacket);
    }
}
