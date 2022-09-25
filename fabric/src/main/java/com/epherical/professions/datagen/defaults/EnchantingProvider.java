package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.items.EnchantAction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import static com.epherical.professions.profession.action.Actions.ENCHANT_ITEM;

public class EnchantingProvider extends NamedProfessionBuilder {

    public EnchantingProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#c9008d"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{"Earn money and experience",
                        "by enchanting."},
                "Enchanting", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_HELMET)
                        .reward(moneyReward(10))
                        .reward(expReward(10))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_CHESTPLATE)
                        .reward(moneyReward(16))
                        .reward(expReward(16))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_LEGGINGS)
                        .reward(moneyReward(14))
                        .reward(expReward(14))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_BOOTS)
                        .reward(moneyReward(8))
                        .reward(expReward(8))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_SWORD)
                        .reward(moneyReward(4))
                        .reward(expReward(4))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_SHOVEL)
                        .reward(moneyReward(2))
                        .reward(expReward(2))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_PICKAXE)
                        .reward(moneyReward(6))
                        .reward(expReward(6))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_AXE)
                        .reward(moneyReward(6))
                        .reward(expReward(6))
                        .build())
                .addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .item(Items.DIAMOND_CHESTPLATE)
                        .reward(moneyReward(16))
                        .reward(expReward(16))
                        .build());
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isCurse() || enchantment.isTreasureOnly() || !enchantment.isDiscoverable()) {
                continue;
            }
            for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
                builder.addAction(ENCHANT_ITEM, EnchantAction.enchant()
                        .enchant(enchantment, i)
                        .reward(moneyReward(2 * i))
                        .reward(expReward(2 * i))
                        .build());
            }
        }
    }
}
