package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.BlockTags;

import static com.epherical.professions.profession.action.Actions.BREAK_BLOCK;

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
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.LOGS)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build());
    }
}
