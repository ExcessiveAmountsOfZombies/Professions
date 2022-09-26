package com.epherical.professions.client;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.Constants;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.MenuScreen;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.unlock.Unlock;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CommonClient {

    private KeyMapping occupationMenu;
    private KeyMapping professionData;
    private KeyMapping openDatapackMenu;


    public CommonClient() {
        occupationMenu = new KeyMapping(
                "key.professions.open_occupation_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.professions.occupation");
        professionData = new KeyMapping(
                "key.professions.show_info",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.professions.occupation");
        openDatapackMenu = new KeyMapping(
                "key.professions.datapack_menu",
                InputConstants.UNKNOWN.getValue(),
                "category.professions.occupation"
        );
    }

    public void openMenus(Minecraft client) {
        if (getOpenDatapackMenu().consumeClick()) {
            client.setScreen(new MenuScreen());
        }

        if (occupationMenu.consumeClick()) {
            if (client.isLocalServer()) {
                ProfessionalPlayer player = CommonPlatform.platform.getPlayerManager().getPlayer(client.player.getUUID());
                client.setScreen(new OccupationScreen<>(player.getActiveOccupations(), client, OccupationScreen::createOccupationEntries, null));
            } else {
                CommonPlatform.platform.getClientNetworking().sendOccupationPacket();
            }

        }
    }

    public void appendToolTip(Player player, Item item, int keyValue, Component keyDisplayName, List<Component> lines) {
        if (player == null) {
            return;
        }
        ProfessionalPlayer pPlayer = CommonPlatform.platform.getPlayerManager().getPlayer(player.getUUID());
        if (pPlayer == null) {
            return;
        }
        List<Component> comps = new ArrayList<>();

        if (Constants.devDebug) {
            boolean foundForBlock;
            boolean foundForItem;
            comps.add(Component.literal("Unlocks?").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            if (item instanceof BlockItem blockItem) {

                List<Unlock.Singular<Block>> list = pPlayer.getLockedKnowledge(blockItem.getBlock());
                for (Unlock.Singular<Block> blockSingular : list) {
                    comps.add(blockSingular.createUnlockComponent());
                }
                foundForBlock = list.size() == 0;

                List<Unlock.Singular<Item>> itemList = pPlayer.getLockedKnowledge(blockItem);
                for (Unlock.Singular<Item> singular : itemList) {
                    comps.add(singular.createUnlockComponent());
                }
                foundForItem = itemList.size() == 0;

                if (foundForBlock && foundForItem) {
                    comps.add(Component.literal("No Unlocks Present."));
                }
                comps.add(Component.literal("Actions:").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

                List<Action.Singular<Block>> actions = pPlayer.getActions(blockItem.getBlock());
                for (Action.Singular<Block> action : actions) {
                    comps.add(Component.literal("").append(action.getProfessionDisplay()).append(" - ").append(action.createActionComponent()));
                }
                foundForBlock = actions.size() == 0;

                List<Action.Singular<Item>> actions1 = pPlayer.getActions(blockItem);
                for (Action.Singular<Item> singular : actions1) {
                    comps.add(Component.literal("").append(singular.getProfessionDisplay()).append(" - ").append(singular.createActionComponent()));
                }
                foundForItem = actions1.size() == 0;
                if (foundForBlock && foundForItem) {
                    comps.add(Component.literal("No Actions Present."));
                }
            } else {
                List<Unlock.Singular<Item>> list = pPlayer.getLockedKnowledge(item);
                for (Unlock.Singular<Item> singular : list) {
                    comps.add(singular.createUnlockComponent());
                }
                if (list.size() == 0) {
                    comps.add(Component.literal("No Unlocks Present."));
                }
                comps.add(Component.literal("Actions:").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

                List<Action.Singular<Item>> actions = pPlayer.getActions(item);
                for (Action.Singular<Item> singular : actions) {
                    comps.add(Component.literal("").append(singular.getProfessionDisplay()).append(" - ").append(singular.createActionComponent()));
                }
                if (actions.size() == 0) {
                    comps.add(Component.literal("No Actions Present"));
                }
            }
            lines.addAll(comps);
            return;
        }

        if (item instanceof BlockItem blockItem) {
            List<Unlock.Singular<Block>> list = pPlayer.getLockedKnowledge(blockItem.getBlock());
            for (Unlock.Singular<Block> singular : list) {
                if (!singular.canUse(pPlayer)) {
                    comps.add(singular.createUnlockComponent());
                }
            }
            List<Unlock.Singular<Item>> itemList = pPlayer.getLockedKnowledge(blockItem);
            for (Unlock.Singular<Item> singular : itemList) {
                if (!singular.canUse(pPlayer)) {
                    comps.add(singular.createUnlockComponent());
                }
            }
        } else {
            List<Unlock.Singular<Item>> list = pPlayer.getLockedKnowledge(item);
            for (Unlock.Singular<Item> singular : list) {
                if (!singular.canUse(pPlayer)) {
                    comps.add(singular.createUnlockComponent());
                }
            }
        }


        boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyValue);

        if (!isKeyDown && !comps.isEmpty()) {
            // todo; translations
            lines.add(Component.translatable("Hold %s to see Professions info", keyDisplayName));
        } else if (!comps.isEmpty()) {
            lines.addAll(comps);
        }
    }

    public KeyMapping getOccupationMenu() {
        return occupationMenu;
    }

    public KeyMapping getProfessionData() {
        return professionData;
    }

    public KeyMapping getOpenDatapackMenu() {
        return openDatapackMenu;
    }
}
