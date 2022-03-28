package com.epherical.professions.profession.action;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.profession.action.builtin.entity.BreedAction;
import com.epherical.professions.profession.action.builtin.blocks.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.entity.TameAction;
import com.epherical.professions.profession.action.builtin.items.BrewAction;
import com.epherical.professions.profession.action.builtin.items.CraftingAction;
import com.epherical.professions.profession.action.builtin.items.EnchantAction;
import com.epherical.professions.profession.action.builtin.items.FishingAction;
import com.epherical.professions.profession.action.builtin.entity.KillAction;
import com.epherical.professions.profession.action.builtin.blocks.PlaceBlockAction;
import com.epherical.professions.profession.action.builtin.blocks.TntDestroyAction;
import com.epherical.professions.profession.action.builtin.items.SmeltItemAction;
import com.epherical.professions.profession.action.builtin.items.TakeSmeltAction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import java.util.function.Predicate;

import static com.epherical.professions.ProfessionsMod.modID;

public class Actions {
    public static final ActionType BREAK_BLOCK = register(modID("break_block"), new BreakBlockAction.Serializer(), "Break Block");
    public static final ActionType PLACE_BLOCK = register(modID("place_block"), new PlaceBlockAction.Serializer(), "Place Block");
    public static final ActionType TNT_DESTROY = register(modID("tnt_destroy"), new TntDestroyAction.Serializer(), "TNT Destroy");
    public static final ActionType KILL_ENTITY = register(modID("kill_entity"), new KillAction.Serializer(), "Kill");
    public static final ActionType FISH_ACTION = register(modID("catch_fish"), new FishingAction.Serializer(), "Fishing");
    public static final ActionType CRAFTS_ITEM = register(modID("craft_item"), new CraftingAction.Serializer(), "Craft");
    public static final ActionType TAKE_COOKED_ITEM = register(modID("take_smelted_item"), new TakeSmeltAction.Serializer(), "Take Smelted Item");
    public static final ActionType ON_ITEM_COOK = register(modID("on_item_smelted"), new SmeltItemAction.Serializer(), "Smelt Item");
    public static final ActionType BREW_ITEM = register(modID("brew"), new BrewAction.Serializer(), "Brew Potion");
    public static final ActionType ENCHANT_ITEM = register(modID("enchant"), new EnchantAction.Serializer(), "Enchant");
    // repair action
    public static final ActionType BREED_ENTITY = register(modID("breed"), new BreedAction.Serializer(), "Breed");
    public static final ActionType TAME_ENTITY = register(modID("tame"), new TameAction.Serializer(), "Tame");
    // villager trading action


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(ProfessionConstants.ACTION_TYPE, "action", "action", Action::getType).build();
    }

    public static ActionType register(ResourceLocation id, Serializer<? extends Action> serializer, String displayName) {
        return Registry.register(ProfessionConstants.ACTION_TYPE, id, new ActionType(serializer, displayName));
    }

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
