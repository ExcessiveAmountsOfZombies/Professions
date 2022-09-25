package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.items.TradeAction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;

import static com.epherical.professions.profession.action.Actions.VILLAGER_TRADE;

public class TradingProvider extends NamedProfessionBuilder {

    public TradingProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#2dcf08"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by trading items to villagers."},
                "Trading", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.EMERALD, Items.DIAMOND)
                        .reward(moneyReward(7))
                        .reward(expReward(7))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.PAPER, Items.ROTTEN_FLESH)
                        .reward(moneyReward(0.25))
                        .reward(expReward(0.25))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.GOLD_INGOT, Items.IRON_INGOT)
                        .reward(moneyReward(3))
                        .reward(expReward(3))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.WHEAT, Items.CARROT, Items.POTATO, Items.BEETROOT)
                        .reward(moneyReward(2))
                        .reward(expReward(2))
                        .build())
                .addAction(VILLAGER_TRADE, TradeAction.trade()
                        .item(Items.TROPICAL_FISH, Items.SALMON, Items.COD, Items.PUFFERFISH)
                        .reward(moneyReward(3))
                        .reward(expReward(3))
                        .build());
    }
}
