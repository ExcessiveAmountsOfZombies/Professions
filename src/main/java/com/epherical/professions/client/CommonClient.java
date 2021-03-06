package com.epherical.professions.client;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.unlock.Unlock;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
        if (getOpenDatapackMenu().isDown()) {
            client.setScreen(CommonPlatform.platform.createScreen());
        }

        if (occupationMenu.isDown()) {
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
        if (item instanceof BlockItem blockItem) {
            List<Unlock.Singular<Block>> list = pPlayer.getLockedKnowledge(blockItem.getBlock());
            for (Unlock.Singular<Block> singular : list) {
                if (!singular.canUse(pPlayer)) {
                    comps.add(new TranslatableComponent(singular.getType().getTranslationKey(),
                                    singular.getProfessionDisplay(),
                                    new TextComponent(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)));
                }
            }
        } else {
            List<Unlock.Singular<Item>> list = pPlayer.getLockedKnowledge(item);
            for (Unlock.Singular<Item> singular : list) {
                if (!singular.canUse(pPlayer)) {
                    comps.add(new TranslatableComponent(singular.getType().getTranslationKey(),
                                    singular.getProfessionDisplay(),
                                    new TextComponent(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                            .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)));
                }
            }
        }


        boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyValue);

        if (!isKeyDown && !comps.isEmpty()) {
            lines.add(new TranslatableComponent("Hold %s to see Professions info", keyDisplayName));
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
