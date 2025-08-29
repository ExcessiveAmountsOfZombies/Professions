package com.epherical.professions.core.register;

import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.actions.block.BlockBreakAction;
import com.epherical.professions.core.actions.block.BlockPlaceAction;
import com.epherical.professions.core.actions.block.TNTDestroyAction;
import com.epherical.professions.core.actions.item.BrewAction;
import com.epherical.professions.core.actions.item.CraftingAction;
import com.epherical.professions.core.actions.item.FishingAction;
import com.epherical.professions.core.actions.item.SmeltItemAction;
import com.epherical.professions.core.actions.item.TakeSmeltAction;
import com.epherical.professions.core.actions.item.TradeAction;


import java.util.List;
import java.util.function.Predicate;

import static com.epherical.professions.CommonClass.ACTION_REGISTRY_KEY;

public class Actions {

    public static final ActionType BLOCK_BREAK = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_break", new ActionType(BlockBreakAction.CODEC));
    public static final ActionType BLOCK_PLACE = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_place", new ActionType(BlockPlaceAction.CODEC));
    public static final ActionType BLOCK_EXPLODE = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_explode", new ActionType(TNTDestroyAction.CODEC));
    public static final ActionType BREW_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "brew_potion", new ActionType(BrewAction.CODEC));
    public static final ActionType CRAFTING_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "craft", new ActionType(CraftingAction.CODEC));
    public static final ActionType FISHING_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "fish", new ActionType(FishingAction.CODEC));
    public static final ActionType SMELT_ITEM_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "smelt_item", new ActionType(SmeltItemAction.CODEC));
    public static final ActionType SMELT_TAKE_ACTION =  PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "take_smelted", new ActionType(TakeSmeltAction.CODEC));
    public static final ActionType TRADE_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "trade", new ActionType(TradeAction.CODEC));




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
