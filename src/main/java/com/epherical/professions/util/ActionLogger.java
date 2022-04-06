package com.epherical.professions.util;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.progression.Occupation;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
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
    private MutableComponent subject;

    private Component money = new TextComponent("$0.0").setStyle(MONEY);
    private Component exp = new TextComponent("0.0xp").setStyle(EXP);


    public void startMessage(Occupation occupation) {
        message = new TranslatableComponent("[%s] %s", new TextComponent("PR").setStyle(VARIABLE), occupation.getProfession().getDisplayComponent()).setStyle(BORDER);
    }

    public boolean addSubjectOfAction(Component component) {
        subject = component.copy().setStyle(VARIABLE);
        return true;
    }

    public void addAction(Action action) {
        MutableComponent component = new TranslatableComponent(action.getType().getTranslationKey()).setStyle(DESCRIPTOR);
        message.append(" ").append(subject.withStyle(ChatFormatting.UNDERLINE).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component))))
                        .append(" ->");
    }

    public void addMoneyReward(Component component) {
        money = component;
    }

    public void addExpReward(Component reward) {
        exp = reward;
    }


    public void sendMessage(ServerPlayer player) {
        if (message != null && player != null && ProfessionConfig.logAllActionsInChat) {
            // todo: add a multiplier to the msg at some point
            message.append(" ").append(money).append(" | ").append(exp);
            player.sendMessage(message, Util.NIL_UUID);
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
