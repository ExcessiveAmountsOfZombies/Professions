package com.epherical.professions.listener.perks;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.rewards.OccupationExperienceEvent;
import com.epherical.professions.bootstrap.Perks;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.perks.Perk;
import com.epherical.professions.model.perks.PerkProfessionGainEXP;

import java.util.Collection;

public class PerkGainExperienceListener implements EventListener<OccupationExperienceEvent> {

    @Override
    public void handle(OccupationExperienceEvent event) {
        IProfessionalPlayer player = event.getContext().getPossibleParameter(ProfessionParameter.THIS_PLAYER);
        if (player == null || player.getCategory() == null) {
            return;
        }

        Occupation occupation = event.getOccupation();
        if (!player.getCategory().hasProfession(occupation.getProfession())) {
            return; // Meh
        }

        double baseAmount = event.getNewAmount();

        Collection<PerkProfessionGainEXP> unlockedPerks = ProfessionsCommon.INSTANCE.getPlayerManager().getUnlockedPerks(Perks.PROFESSION_EXP_GAIN, player, occupation);

        double flat = 0.0D;
        double additive = 0.0D;
        double multiplicative = 1.0D;
        for (PerkProfessionGainEXP unlockedPerk : unlockedPerks) {
            Perk.ModificationStage stage = unlockedPerk.getModificationStage();
            double value = unlockedPerk.getValue();
            if (Perk.ModificationStage.FLAT.equals(stage)) {
                flat += value;
            } else if (Perk.ModificationStage.ADDITIVE.equals(stage)) {
                additive += value;
            } else if (Perk.ModificationStage.MULTIPLICATIVE.equals(stage)) {
                multiplicative *= value;
            }
        }

        double modifiedAmount = baseAmount;
        modifiedAmount = Perk.ModificationStage.FLAT.apply(modifiedAmount, flat);
        modifiedAmount = Perk.ModificationStage.ADDITIVE.apply(modifiedAmount, additive);
        modifiedAmount = Perk.ModificationStage.MULTIPLICATIVE.apply(modifiedAmount, multiplicative);

        event.setNewAmount(modifiedAmount);
    }
}
