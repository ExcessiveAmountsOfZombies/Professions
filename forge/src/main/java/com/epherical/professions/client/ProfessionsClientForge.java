package com.epherical.professions.client;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.networking.NetworkHandler;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ProfessionsClientForge {

    private static KeyMapping occupationMenu;
    private static KeyMapping professionData;

    public static NetworkHandler.Client clientHandler;

    public static void initClient() {
        clientHandler = new NetworkHandler.Client();
        occupationMenu = new KeyMapping(
                "key.professions.open_occupation_menu",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R,
                "category.professions.occupation");
        professionData = new KeyMapping(
                "key.professions.show_info",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.professions.occupation"
        );
        ClientRegistry.registerKeyBinding(occupationMenu);
        ClientRegistry.registerKeyBinding(professionData);
    }

    @SubscribeEvent
    public void handleInput(InputEvent.KeyInputEvent event) {
        if (occupationMenu.isDown()) {
            Minecraft client = Minecraft.getInstance();
            if (client.isLocalServer()) {
                ProfessionalPlayer player = ProfessionsForge.getInstance().getPlayerManager().getPlayer(client.player.getUUID());
                client.setScreen(new OccupationScreen<>(player.getActiveOccupations(), client, OccupationScreen::createOccupationEntries, null));
            } else {
                clientHandler.sendOccupationPacket();
            }
        }
    }

    @SubscribeEvent
    public void onClientLogin(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.hasSingleplayerServer()) {
            ProfessionsForge.getInstance().getPlayerManager().playerClientQuit(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public void onToolTip(ItemTooltipEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        ProfessionalPlayer pPlayer = ProfessionsForge.getInstance().getPlayerManager().getPlayer(player.getUUID());
        if (pPlayer == null) {
            return;
        }
        Item item = event.getItemStack().getItem();
        List<Component> comps = new ArrayList<>();
        if (item instanceof BlockItem blockItem) {
            List<Unlock.Singular<Block>> lockedKnowledge = pPlayer.getLockedKnowledge(Unlocks.BLOCK_DROP_UNLOCK, blockItem.getBlock());
            for (Unlock.Singular<Block> singular : lockedKnowledge) {
                if (!singular.canUse(pPlayer)) {
                    comps.add(Component.translatable("professions.tooltip.drop_req",
                                    singular.getProfessionDisplay(),
                                    Component.literal(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)));
                }
            }
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


        boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), professionData.getKey().getValue());

        if (!isKeyDown && !comps.isEmpty()) {
            event.getToolTip().add(Component.translatable("Hold %s to see Professions info", professionData.getKey().getDisplayName()));
        } else if (!comps.isEmpty()) {
            event.getToolTip().addAll(comps);
        }
    }

}
