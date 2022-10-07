package com.epherical.professions.util;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.progression.Occupation;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class ActionLogger {

    private static Style BORDER = Style.EMPTY.withColor(ProfessionConfig.headerBorders);
    private static Style VARIABLE = Style.EMPTY.withColor(ProfessionConfig.variables);
    private static Style DESCRIPTOR = Style.EMPTY.withColor(ProfessionConfig.descriptors);
    private static Style MONEY = Style.EMPTY.withColor(ProfessionConfig.money);
    private static Style EXP = Style.EMPTY.withColor(ProfessionConfig.experience);

    private MutableComponent message;
    private MutableComponent actionPortion;

    private Component money = new TextComponent("$0.0").setStyle(MONEY);
    private Component exp = new TextComponent("0.0xp").setStyle(EXP);

    private boolean actionAdded = false;
    private boolean msgAdded = false;


    public void startMessage(Occupation occupation) {
        if (!msgAdded) {
            message = new TranslatableComponent("[%s] %s", new TextComponent("PR").setStyle(VARIABLE), occupation.getProfession().getDisplayComponent()).setStyle(BORDER);
            msgAdded = true;
        }
    }

    public void addAction(Action action, Component component) {
        if (!actionAdded) {
            MutableComponent type = new TranslatableComponent(action.getType().getTranslationKey()).setStyle(DESCRIPTOR);
            actionPortion = new TextComponent(" ").append(component.copy().withStyle(VARIABLE).withStyle(ChatFormatting.UNDERLINE).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, type))));
            actionAdded = true;
        }
    }

    public void addMoneyReward(Component component) {
        money = component;
    }

    public void addExpReward(Component reward) {
        exp = reward;
    }


    public void sendMessage(ServerPlayer player) {
        if (message != null && player != null) {
            message.append(actionPortion).append(" ").append(money).append(" | ").append(exp);
            if (ProfessionConfig.logInChat) {
                player.sendMessage(message, Util.NIL_UUID);
            } else if (ProfessionConfig.displayOutput) {
                player.sendMessage(message, ChatType.GAME_INFO, Util.NIL_UUID);
            }
        }
    }

    public static void reloadStyles() {
        BORDER = Style.EMPTY.withColor(ProfessionConfig.headerBorders);
        VARIABLE = Style.EMPTY.withColor(ProfessionConfig.variables);
        DESCRIPTOR = Style.EMPTY.withColor(ProfessionConfig.descriptors);
        MONEY = Style.EMPTY.withColor(ProfessionConfig.money);
        EXP = Style.EMPTY.withColor(ProfessionConfig.experience);
    }


}
