package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.items.FishingAction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;

import static com.epherical.professions.profession.action.Actions.FISH_ACTION;

public class FishingProvider extends NamedProfessionBuilder {

    public FishingProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#0a91c7"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by fishing."},
                "Fishing", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(FISH_ACTION, FishingAction.fish()
                        .item(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH)
                        .reward(moneyReward(25))
                        .reward(expReward(25))
                        .build());
    }
}
