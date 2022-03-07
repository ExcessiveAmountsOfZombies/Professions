package com.epherical.professions.profession.progression;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.events.OccupationEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.rewards.Reward;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfessionalPlayerImpl implements ProfessionalPlayer {
    private final String userName;
    private final UUID uuid;
    private List<Occupation> occupations = new ArrayList<>();

    public ProfessionalPlayerImpl(String userName, UUID uuid) {
        this.uuid = uuid;
        this.userName = userName;

    }

    public String getUserName() {
        return userName;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public List<Reward> handleAction(ProfessionContext context) {
        OccupationEvents.BEFORE_ACTION_HANDLED_EVENT.invoker().onHandleAction(context, this);
        List<Reward> rewards = new ArrayList<>();
        for (Occupation occupation : occupations) {
            if (occupation.isActive()) {
                for (Action action : occupation.getProfession().getActions()) {
                    if (action.getType() == context.getParameter(ProfessionParameter.ACTION_TYPE)) {
                        action.handleRewards(context, occupation);
                    }
                }
            }
        }
        return rewards;
    }
}
