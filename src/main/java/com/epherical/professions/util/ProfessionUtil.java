package com.epherical.professions.util;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.profession.unlock.Unlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ProfessionUtil {

    /**
     * Checks all potential unlocks on the player, and then determines if any of them are false.
     * Don't use if you want to know specifically which one is keeping you from doing an action.
     * @return true if it can be used, false if not.
     */
    public static <T> boolean canUse(ProfessionalPlayer player, UnlockType<T> type, T object) {
        List<Unlock.Singular<T>> unlocks = player.getLockedKnowledge(type, object);
        for (Unlock.Singular<T> unlock : unlocks) {
            if (!unlock.canUse(player)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if the player CAN break the block, and false if they can't.
     */
    public static boolean canBreak(ProfessionalPlayer player, Player onlinePlayer, Block block) {
        boolean canBreak = true;
        UnlockErrorHelper helper = new UnlockErrorHelper(new TextComponent("=-=-=-= Level Requirements =-=-=-="));
        List<Unlock.Singular<Block>> unlocks = player.getLockedKnowledge(Unlocks.BLOCK_BREAK_UNLOCK, block);
        for (Unlock.Singular<Block> singular : unlocks) {
            if (!singular.canUse(player)) {
                helper.newLine();
                helper.levelRequirementNotMet(singular);
                canBreak = false;
            }
        }

        if (!canBreak) {
            Component hover = new TextComponent("Hover to see which occupations prevented the block break.")
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)
                            .withUnderlined(true)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, helper.getComponent())));
            onlinePlayer.sendMessage(new TranslatableComponent("%s", hover)
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
        }
        return canBreak;
    }
}
