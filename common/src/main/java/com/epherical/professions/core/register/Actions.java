package com.epherical.professions.core.register;

import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.actions.block.BlockBreakAction;


import java.util.List;
import java.util.function.Predicate;

import static com.epherical.professions.CommonClass.ACTION_REGISTRY_KEY;

public class Actions {

    public static final ActionType BLOCK_BREAK = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_break", new ActionType(BlockBreakAction.CODEC));



    public static void register() {

    }


    public static <T> Predicate<T> andAllConditions(List<Predicate<T>> conditions) {
        return switch (conditions.size()) {
            case 0 -> t -> true;
            case 1 -> conditions.getFirst();
            case 2 -> conditions.getFirst().and(conditions.getLast());
            default -> t -> {
                for (Predicate<T> condition : conditions) {
                    if (!condition.test(t)) {
                        return false;
                    }
                }
                return true;
            };
        };
    }

}
