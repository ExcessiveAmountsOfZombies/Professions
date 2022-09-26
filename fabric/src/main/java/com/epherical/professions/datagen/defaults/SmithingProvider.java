package com.epherical.professions.datagen.defaults;

import com.epherical.professions.Constants;
import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.items.CraftingAction;
import com.epherical.professions.profession.action.builtin.items.SmeltItemAction;
import com.epherical.professions.profession.action.builtin.items.TakeSmeltAction;
import com.epherical.professions.profession.editor.Append;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.modifiers.perks.builtin.ScalingAttributePerk;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.profession.unlock.builtin.EquipmentUnlock;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import static com.epherical.professions.profession.action.Actions.*;

public class SmithingProvider extends NamedProfessionBuilder {

    public SmithingProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#84abad"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by smithing items."},
                "Smithing", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.WOODEN_SWORD, Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE)
                        .reward(moneyReward(1.0))
                        .reward(expReward(1.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS)
                        .reward(moneyReward(6.0))
                        .reward(expReward(6.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.STONE_SWORD, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_SHOVEL, Items.STONE_PICKAXE)
                        .reward(moneyReward(3))
                        .reward(expReward(3))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS)
                        .reward(moneyReward(12.0))
                        .reward(expReward(12.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.IRON_SWORD, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SHOVEL, Items.IRON_PICKAXE)
                        .reward(moneyReward(5))
                        .reward(expReward(5))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS)
                        .reward(moneyReward(18.0))
                        .reward(expReward(18.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.GOLDEN_SWORD, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE)
                        .reward(moneyReward(7))
                        .reward(expReward(7))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)
                        .reward(moneyReward(24.0))
                        .reward(expReward(24.0))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.DIAMOND_SWORD, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE)
                        .reward(moneyReward(12))
                        .reward(expReward(12))
                        .build())
                .addAction(ON_ITEM_COOK, SmeltItemAction.smelt()
                        .item(Items.GOLD_INGOT)
                        .reward(moneyReward(2))
                        .reward(expReward(2))
                        .build())
                .addAction(TAKE_COOKED_ITEM, TakeSmeltAction.takeSmelt()
                        .item(Items.IRON_INGOT)
                        .reward(moneyReward(1.3))
                        .reward(expReward(1.3))
                        .build());
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(1).attribute(Attributes.MAX_HEALTH).increaseBy(0.20));
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(10).attribute(Attributes.ATTACK_DAMAGE).increaseBy(0.05));
    }

    public Append.Builder createSmithingAppender() {
        Append.Builder builder = Append.Builder.appender(Constants.modID("smithing"));
        builder.addUnlock(Unlocks.EQUIPMENT_UNLOCK, EquipmentUnlock.builder()
                .level(5)
                .item(Items.IRON_HELMET, Items.IRON_CHESTPLATE,
                        Items.IRON_LEGGINGS, Items.IRON_BOOTS));
        builder.addUnlock(Unlocks.EQUIPMENT_UNLOCK, EquipmentUnlock.builder()
                .level(10)
                .item(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE,
                        Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS));
        builder.addUnlock(Unlocks.EQUIPMENT_UNLOCK, EquipmentUnlock.builder()
                .level(15)
                .item(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE,
                        Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS));
        builder.addUnlock(Unlocks.EQUIPMENT_UNLOCK, EquipmentUnlock.builder()
                .level(25)
                .item(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE,
                        Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS));

        return builder;
    }
}
