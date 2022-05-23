package com.epherical.professions.profession.action;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.PlaceBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.TntDestroyAction;
import com.epherical.professions.profession.action.builtin.entity.BreedAction;
import com.epherical.professions.profession.action.builtin.entity.KillAction;
import com.epherical.professions.profession.action.builtin.entity.TameAction;
import com.epherical.professions.profession.action.builtin.items.BrewAction;
import com.epherical.professions.profession.action.builtin.items.CraftingAction;
import com.epherical.professions.profession.action.builtin.items.EnchantAction;
import com.epherical.professions.profession.action.builtin.items.FishingAction;
import com.epherical.professions.profession.action.builtin.items.SmeltItemAction;
import com.epherical.professions.profession.action.builtin.items.TakeSmeltAction;
import com.epherical.professions.profession.action.builtin.items.TradeAction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Predicate;

import static com.epherical.professions.Constants.modID;

public class Actions {
    public static final DeferredRegister<ActionType> ACTIONS = DeferredRegister.create(RegistryConstants.ACTION_TYPE, Constants.MOD_ID);
    public static final RegistryObject<ActionType> BREAK_BLOCK = ACTIONS.register("break_block", () -> new ActionType(new BreakBlockAction.Serializer(), "professions.action.type.break_block"));
    public static final RegistryObject<ActionType> PLACE_BLOCK = ACTIONS.register("place_block", () -> new ActionType(new PlaceBlockAction.Serializer(), "professions.action.type.place_block"));
    public static final RegistryObject<ActionType> TNT_DESTROY = ACTIONS.register("tnt_destroy", () -> new ActionType(new TntDestroyAction.Serializer(), "professions.action.type.tnt_destroy"));
    public static final RegistryObject<ActionType> KILL_ENTITY = ACTIONS.register("kill_entity", () -> new ActionType(new KillAction.Serializer(), "professions.action.type.kill_entity"));
    public static final RegistryObject<ActionType> FISH_ACTION = ACTIONS.register("catch_fish", () -> new ActionType(new FishingAction.Serializer(), "professions.action.type.catch_fish"));
    public static final RegistryObject<ActionType> CRAFTS_ITEM = ACTIONS.register("craft_item", () -> new ActionType(new CraftingAction.Serializer(), "professions.action.type.craft_item"));
    public static final RegistryObject<ActionType> TAKE_COOKED_ITEM = ACTIONS.register("take_smelted_item", () -> new ActionType(new TakeSmeltAction.Serializer(), "professions.action.type.take_smelted_item"));
    public static final RegistryObject<ActionType> ON_ITEM_COOK = ACTIONS.register("on_item_smelted", () -> new ActionType(new SmeltItemAction.Serializer(), "professions.action.type.on_item_smelted"));
    public static final RegistryObject<ActionType> BREW_ITEM = ACTIONS.register("brew", () -> new ActionType(new BrewAction.Serializer(), "professions.action.type.brew"));
    public static final RegistryObject<ActionType> ENCHANT_ITEM = ACTIONS.register("enchant", () -> new ActionType(new EnchantAction.Serializer(), "professions.action.type.enchant"));
    // repair action
    public static final RegistryObject<ActionType> BREED_ENTITY = ACTIONS.register("breed", () -> new ActionType(new BreedAction.Serializer(), "professions.action.type.breed"));
    public static final RegistryObject<ActionType> TAME_ENTITY = ACTIONS.register("tame", () -> new ActionType(new TameAction.Serializer(), "professions.action.type.tame"));
    public static final RegistryObject<ActionType> VILLAGER_TRADE = ACTIONS.register("villager_trade", () -> new ActionType(new TradeAction.Serializer(), "professions.action.type.villager_trade"));


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.ACTION_TYPE.getSlaveMap(modID("actions"), MappedRegistry.class), "action", "action", Action::getType).build();
    }

    /*public static ActionType register(ResourceLocation id, Serializer<? extends Action> serializer, String translationKey) {
        return Registry.register(RegistryConstants.ACTION_TYPE, id, new ActionType(serializer, translationKey));
    }*/

    public static <T> Predicate<T> andAllConditions(Predicate<T>[] conditions) {
        return switch (conditions.length) {
            case 0 -> t -> true;
            case 1 -> conditions[0];
            case 2 -> conditions[0].and(conditions[1]);
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
