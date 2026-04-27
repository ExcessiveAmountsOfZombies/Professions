package com.epherical.professions.bootstrap;

import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.block.BlockBreakAction;
import com.epherical.professions.model.actions.block.BlockPlaceAction;
import com.epherical.professions.model.actions.block.TNTDestroyAction;
import com.epherical.professions.model.actions.item.BrewAction;
import com.epherical.professions.model.actions.item.CraftingAction;
import com.epherical.professions.model.actions.item.FishingAction;
import com.epherical.professions.model.actions.item.SmeltItemAction;
import com.epherical.professions.model.actions.item.TakeSmeltAction;
import com.epherical.professions.model.actions.item.TradeAction;


import java.util.List;
import java.util.function.Predicate;

import static com.epherical.professions.ProfessionsCommon.ACTION_REGISTRY_KEY;

public class Actions {

    public static final ActionType BLOCK_BREAK = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_break", new ActionType(BlockBreakAction.CODEC, "professions.action.type.break_block"));
    public static final ActionType BLOCK_PLACE = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_place", new ActionType(BlockPlaceAction.CODEC, "professions.action.type.place_block"));
    public static final ActionType BLOCK_EXPLODE = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_explode", new ActionType(TNTDestroyAction.CODEC, "professions.action.type.tnt_destroy"));
    public static final ActionType BREW_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "brew_potion", new ActionType(BrewAction.CODEC, "professions.action.type.brew"));
    public static final ActionType CRAFTING_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "craft", new ActionType(CraftingAction.CODEC, "professions.action.type.craft_item"));
    public static final ActionType FISHING_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "fish", new ActionType(FishingAction.CODEC, "professions.action.type.catch_fish"));
    public static final ActionType SMELT_ITEM_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "smelt_item", new ActionType(SmeltItemAction.CODEC, "professions.action.type.on_item_smelted"));
    public static final ActionType SMELT_TAKE_ACTION =  PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "take_smelted", new ActionType(TakeSmeltAction.CODEC, "professions.action.type.take_smelted_item"));
    public static final ActionType TRADE_ACTION = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "trade", new ActionType(TradeAction.CODEC, "professions.action.type.villager_trade"));




    public static void bootstrap() {}


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
