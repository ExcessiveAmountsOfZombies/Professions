package com.epherical.professions.util;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class XpRateTimerTask extends TimerTask {

    private ServerPlayer player;

    private Map<Profession, Double> expRewards = new HashMap<>();

    public XpRateTimerTask(ServerPlayer player) {
        this.player = player;

    }

    @Override
    public void run() {
        if (this.player != null && this.player.isAlive()) {
            this.player.sendSystemMessage(Component.translatable("Experience Gains for the last 60s:"));
            for (Map.Entry<Profession, Double> entry : expRewards.entrySet()) {
                MutableComponent component = Component.translatable("%s, %s",
                        entry.getKey().getDisplayComponent(), Component.literal(String.valueOf(entry.getValue())));
                this.player.sendSystemMessage(component);
            }
            this.expRewards.clear();
        } else {
            cancel();
        }
    }

    public void addExperience(Occupation occupation, double value) {
        if (expRewards.containsKey(occupation.getProfession())) {
            expRewards.put(occupation.getProfession(), expRewards.get(occupation.getProfession()) + value);
        } else {
            expRewards.put(occupation.getProfession(), value);
        }
    }
}
