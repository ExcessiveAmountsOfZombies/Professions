package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.items.BrewAction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;

import static com.epherical.professions.profession.action.Actions.BREW_ITEM;

public class AlchemistProvider extends NamedProfessionBuilder {

    public AlchemistProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#a100e0"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by brewing potions."},
                "Alchemy", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser());
        builder.addAction(BREW_ITEM, BrewAction.brew()
                .item(Items.NETHER_WART)
                .reward(expReward(300))
                .reward(moneyReward(200))
                .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.NETHER_WART)
                        .reward(expReward(2))
                        .reward(moneyReward(0.5))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.REDSTONE, Items.GLOWSTONE_DUST)
                        .reward(expReward(2))
                        .reward(moneyReward(0.65))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.FERMENTED_SPIDER_EYE)
                        .reward(expReward(4))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.GUNPOWDER)
                        .reward(expReward(4))
                        .reward(moneyReward(0.85))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.DRAGON_BREATH)
                        .reward(expReward(6))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.SUGAR)
                        .reward(expReward(0.5))
                        .reward(moneyReward(0.25))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.RABBIT_FOOT, Items.TURTLE_HELMET, Items.PHANTOM_MEMBRANE)
                        .reward(expReward(10))
                        .reward(moneyReward(5))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.GLISTERING_MELON_SLICE)
                        .reward(expReward(3))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.SPIDER_EYE, Items.MAGMA_CREAM, Items.BLAZE_POWDER)
                        .reward(expReward(2))
                        .reward(moneyReward(0.5))
                        .build())
                .addAction(BREW_ITEM, BrewAction.brew()
                        .item(Items.PUFFERFISH, Items.GOLDEN_CARROT, Items.GHAST_TEAR)
                        .reward(expReward(5))
                        .reward(moneyReward(2))
                        .build());
    }
}
