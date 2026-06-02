package com.epherical.professions.listener.action;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.rewards.OccupationExperienceEvent;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import net.minecraft.world.item.ItemStack;

public class ActionGainExperienceListener implements EventListener<OccupationExperienceEvent> {



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


        double baseAmount = event.baseAmount();
        double newAmount = baseAmount;
        ProfessionContext context = event.getContext();
        Action<?> action = event.getAction();
        if (Actions.SMELT_TAKE_ACTION.equals(action.getType())) {
            ItemStack itemStack = context.getPossibleParameter(ProfessionParameter.ITEM_INVOLVED);
            if (itemStack == null) {
                return;
            }
            int count = itemStack.getCount();
            newAmount = baseAmount * count;
        }

        event.setNewAmount(event.getNewAmount() + newAmount - baseAmount);
    }
}
