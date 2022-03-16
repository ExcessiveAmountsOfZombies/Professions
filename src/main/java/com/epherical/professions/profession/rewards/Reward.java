package com.epherical.professions.profession.rewards;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.builtin.OccupationExperience;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Reward {
    RewardType getType();

    void giveReward(ProfessionContext context, Action action, Occupation occupation);

    /**
     * @return always returns a component, even if it has no contents.
     */
    @NotNull Component rewardChatInfo();

    interface Builder {
        Reward build();
    }
}
