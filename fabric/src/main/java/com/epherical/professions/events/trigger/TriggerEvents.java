package com.epherical.professions.events.trigger;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public final class TriggerEvents {

    public static final Event<PlaceBlock> PLACE_BLOCK_EVENT = EventFactory.createArrayBacked(PlaceBlock.class, calls -> (player, state, pos) -> {
        for (PlaceBlock call : calls) {
            call.onBlockPlace(player, state, pos);
        }
    });

    public static final Event<TNTDestroy> TNT_DESTROY_EVENT = EventFactory.createArrayBacked(TNTDestroy.class, calls -> (source, state, blockPos) -> {
        for (TNTDestroy call : calls) {
            call.onTNTDestroy(source, state, blockPos);
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

    public static final Event<SmeltItem> SMELT_ITEM_EVENT = EventFactory.createArrayBacked(SmeltItem.class, calls -> (owner, smeltedItem, recipe, blockEntity) -> {
        for (SmeltItem call : calls) {
            call.onItemSmelt(owner, smeltedItem, recipe, blockEntity);
        }
    });

    public static final Event<BrewPotion> BREW_POTION_EVENT = EventFactory.createArrayBacked(BrewPotion.class, calls -> (owner, brewingIngredient, blockEntity) -> {
        for (BrewPotion call : calls) {
            call.onPotionBrew(owner, brewingIngredient, blockEntity);
        }
    });

    public static final Event<EnchantItem> ENCHANT_ITEM_EVENT = EventFactory.createArrayBacked(EnchantItem.class, calls -> (player, itemEnchanted, spentLevels) -> {
        for (EnchantItem call : calls) {
            call.onItemEnchant(player, itemEnchanted, spentLevels);
        }
    });

    public static final Event<BreedAnimal> BREED_ANIMAL_EVENT = EventFactory.createArrayBacked(BreedAnimal.class, calls -> (player, parent, partner, child) -> {
        for (BreedAnimal call : calls) {
            call.onBreed(player, parent, partner, child);
        }
    });

    public static final Event<TameAnimal> TAME_ANIMAL_EVENT = EventFactory.createArrayBacked(TameAnimal.class, calls -> (player, animal) -> {
        for (TameAnimal call : calls) {
            call.onTame(player, animal);
        }
    });

    public static final Event<VillagerTrade> VILLAGER_TRADE_EVENT = EventFactory.createArrayBacked(VillagerTrade.class, calls -> (player, villager, offer) -> {
        for (VillagerTrade call : calls) {
            call.onTradeWithVillager(player, villager, offer);
        }
    });


    public interface PlaceBlock {
        /**
         * This event is already assumed to take place on the server, no side checks are needed.
         */
        void onBlockPlace(ServerPlayer player, BlockState state, BlockPos pos);
    }

    public interface TNTDestroy {
        void onTNTDestroy(ServerPlayer source, BlockState state, BlockPos pos);
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
         *
         * @param player The player who crafted the item.
         * @param stack  The item that was crafted
         */
        void onCraftItem(ServerPlayer player, ItemStack stack, Recipe<?> recipe);
    }

    public interface TakeSmeltedItem {
        void onItemTake(ServerPlayer player, ItemStack stack);
    }

    public interface SmeltItem {
        void onItemSmelt(UUID owner, ItemStack smeltedItem, Recipe<?> recipe, AbstractFurnaceBlockEntity blockEntity);
    }

    public interface BrewPotion {
        /**
         * This event is not great.
         */
        void onPotionBrew(UUID owner, ItemStack brewingIngredient, BrewingStandBlockEntity blockEntity);
    }

    public interface EnchantItem {
        /**
         * Event for after the player has enchanted an item.
         */
        void onItemEnchant(ServerPlayer player, ItemStack itemEnchanted, int spentLevels);
    }

    public interface BreedAnimal {
        void onBreed(ServerPlayer player, Animal parent, Animal partner, AgeableMob child);
    }

    public interface TameAnimal {
        void onTame(ServerPlayer player, Animal animal);
    }

    public interface VillagerTrade {
        void onTradeWithVillager(ServerPlayer player, AbstractVillager villager, MerchantOffer offer);
    }

}
