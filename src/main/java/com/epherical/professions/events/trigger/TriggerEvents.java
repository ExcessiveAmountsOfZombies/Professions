package com.epherical.professions.events.trigger;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;

public final class TriggerEvents {

    public static final Event<PlaceBlock> PLACE_BLOCK_EVENT = EventFactory.createArrayBacked(PlaceBlock.class, calls -> (player, state) -> {
        for (PlaceBlock call : calls) {
            call.onBlockPlace(player, state);
        }
    });

    public static final Event<TNTDestroy> TNT_DESTROY_EVENT = EventFactory.createArrayBacked(TNTDestroy.class, calls -> (source, state) -> {
        for (TNTDestroy call : calls) {
            call.onTNTDestroy(source, state);
        }
    });

    public static final Event<CatchFish> CATCH_FISH_EVENT = EventFactory.createArrayBacked(CatchFish.class, calls -> (player, stack) -> {
        for (CatchFish call : calls) {
            call.onCatchFish(player, stack);
        }
    });

    public static final Event<CraftItem> CRAFT_ITEM_EVENT = EventFactory.createArrayBacked(CraftItem.class, calls -> (player, stack, recipe) -> {
        for (CraftItem call : calls) {
            call.onCraftItem(player, stack, recipe);
        }
    });

    public static final Event<TakeSmeltedItem> TAKE_SMELTED_ITEM_EVENT = EventFactory.createArrayBacked(TakeSmeltedItem.class, calls -> (player, stack) -> {
        for (TakeSmeltedItem call : calls) {
            call.onItemTake(player, stack);
        }
    });



    public interface PlaceBlock {
        /**
         * This event is already assumed to take place on the server, no side checks are needed.
         */
        void onBlockPlace(ServerPlayer player, BlockState state);
    }

    public interface TNTDestroy {
        void onTNTDestroy(ServerPlayer source, BlockState state);
    }

    public interface CatchFish {
        void onCatchFish(ServerPlayer player, ItemStack stack);
    }

    public interface CraftItem {
        /**
         * This event is only called on the remote side.
         * <br>
         * This event is called for every 'craft' the player does. If a player crafts 64 emerald blocks,
         * this event will be called 64 times.
         * @param player The player who crafted the item.
         * @param stack The item that was crafted
         */
        void onCraftItem(ServerPlayer player, ItemStack stack, Recipe<?> recipe);
    }

    public interface TakeSmeltedItem {
        void onItemTake(ServerPlayer player, ItemStack stack);
    }

}
