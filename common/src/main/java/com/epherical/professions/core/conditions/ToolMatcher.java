package com.epherical.professions.core.conditions;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public record ToolMatcher(MatchTool tool) implements Condition {

    public static final MapCodec<ToolMatcher> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            MatchTool.CODEC.fieldOf("matches").forGetter(ToolMatcher::tool))
                    .apply(i, ToolMatcher::new)
    );

    @Override
    public ConditionType getType() {
        return null;
    }

    @Override
    public boolean test(ProfessionContext context) {
        ItemStack stack = context.getPossibleParameter(ProfessionParameter.TOOL);
        return stack != null && (this.tool.predicate().isEmpty() || this.tool.predicate().get().test(stack));
    }
}
