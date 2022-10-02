package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.PlaceBlockAction;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.modifiers.perks.builtin.ScalingAttributePerk;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static com.epherical.professions.profession.action.Actions.BREAK_BLOCK;
import static com.epherical.professions.profession.action.Actions.PLACE_BLOCK;

public class LoggingProvider extends NamedProfessionBuilder {

    public LoggingProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#9e3011"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by farming trees."},
                "Logging", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(BlockTags.SAPLINGS)
                        .reward(expReward(0.5))
                        .reward(moneyReward(0.25)))
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.LOGS)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build());
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(1).attribute(Attributes.MAX_HEALTH).increaseBy(0.20));
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(10).attribute(Attributes.ATTACK_DAMAGE).increaseBy(0.05));
    }
}
