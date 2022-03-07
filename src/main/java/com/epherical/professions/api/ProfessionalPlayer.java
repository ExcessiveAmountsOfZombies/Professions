package com.epherical.professions.api;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.rewards.Reward;

import java.util.List;

public interface ProfessionalPlayer {

    List<Reward> handleAction(ProfessionContext context);

}
