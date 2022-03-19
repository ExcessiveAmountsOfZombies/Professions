package com.epherical.professions.profession.action;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.profession.action.builtin.BreakBlockAction;
import com.epherical.professions.profession.action.builtin.KillAction;
import com.epherical.professions.profession.action.builtin.PlaceBlockAction;
import com.epherical.professions.profession.action.builtin.TntDestroyAction;
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
