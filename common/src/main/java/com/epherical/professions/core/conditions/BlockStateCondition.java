package com.epherical.professions.core.conditions;

import com.epherical.professions.core.context.ProfessionContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

public record BlockStateCondition(LootItemBlockStatePropertyCondition block) implements Condition {

    public static final MapCodec<BlockStateCondition> CODEC = RecordCodecBuilder.<BlockStateCondition>mapCodec(
            i -> i.group(
                    LootItemBlockStatePropertyCondition.CODEC.fieldOf("term").forGetter(BlockStateCondition::block)
            ).apply(i, BlockStateCondition::new))
            .validate(BlockStateCondition::validate);


    private static DataResult<BlockStateCondition> validate(BlockStateCondition condition) {
        return condition.block().properties()
                .flatMap(i -> i.checkState(condition.block().block().value().getStateDefinition()))
                .map(i -> DataResult.<BlockStateCondition>error(() -> "Block " + condition.block() + " has no property" + i))
                .orElse(DataResult.success(condition));
    }

    @Override
    public ConditionType getType() {
        return null;
    }

    @Override
    public boolean test(ProfessionContext context) {
        return false;
    }
}
