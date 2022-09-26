package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.PlaceBlockAction;
import com.epherical.professions.profession.action.builtin.entity.BreedAction;
import com.epherical.professions.profession.conditions.builtin.BlockStatePropertyAnyCondition;
import com.epherical.professions.profession.conditions.builtin.FullyGrownCropCondition;
import com.epherical.professions.profession.modifiers.perks.Perks;
import com.epherical.professions.profession.modifiers.perks.builtin.ScalingAttributePerk;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.epherical.professions.profession.action.Actions.*;

public class FarmingProvider extends NamedProfessionBuilder {

    public FarmingProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#107d0e"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by farming."},
                "Farming", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(BlockTags.CROPS)
                        .condition(FullyGrownCropCondition::new)
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.COCOA)
                        .condition(BlockStatePropertyAnyCondition.checkProperties()
                                .properties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(BlockStateProperties.AGE_2, 2)))
                        .reward(expReward(1))
                        .reward(moneyReward(1))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.SUGAR_CANE)
                        .reward(expReward(0.75))
                        .reward(moneyReward(0.75))
                        .build())
                .addAction(BREAK_BLOCK, BreakBlockAction.breakBlock()
                        .block(Blocks.MELON, Blocks.PUMPKIN)
                        .reward(expReward(1.3))
                        .reward(moneyReward(1.3))
                        .build())
                .addAction(PLACE_BLOCK, PlaceBlockAction.place()
                        .block(BlockTags.CROPS)
                        .block(Blocks.COCOA)
                        .reward(moneyReward(0.85))
                        .reward(expReward(0.85))
                        .build())
                .addAction(BREED_ENTITY, BreedAction.breed()
                        .entity(EntityType.PIG, EntityType.COW, EntityType.MOOSHROOM,
                                EntityType.SHEEP, EntityType.RABBIT, EntityType.LLAMA,
                                EntityType.TURTLE, EntityType.OCELOT, EntityType.WOLF, EntityType.CHICKEN,
                                EntityType.HORSE)
                        .reward(expReward(5.0))
                        .reward(moneyReward(5.0))
                        .build());
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(1).attribute(Attributes.MAX_HEALTH).increaseBy(0.20));
        builder.addPerk(Perks.SCALING_ATTRIBUTE_PERK, ScalingAttributePerk.scaling()
                .level(10).attribute(Attributes.MOVEMENT_SPEED).increaseBy(0.001));
    }
}
