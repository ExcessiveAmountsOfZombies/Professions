package com.epherical.professions.model.actions.conditions;

import com.epherical.professions.api.actions.Condition;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.bootstrap.Conditions;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
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
        return Conditions.BLOCK_STATE_MATCHES;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState state = context.getPossibleParameter(ProfessionParameter.THIS_BLOCK_STATE);
        return state != null && state.is(this.block.block()) && (this.block.properties().isEmpty() || this.block.properties().get().matches(state));
    }
}
