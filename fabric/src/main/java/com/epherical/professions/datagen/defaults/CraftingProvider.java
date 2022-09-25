package com.epherical.professions.datagen.defaults;

import com.epherical.professions.datagen.NamedProfessionBuilder;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.action.builtin.items.CraftingAction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import static com.epherical.professions.profession.action.Actions.CRAFTS_ITEM;

public class CraftingProvider extends NamedProfessionBuilder {

    public CraftingProvider() {
        super(ProfessionBuilder.profession(
                TextColor.parseColor("#f2a100"),
                TextColor.parseColor("#FFFFFF"),
                new String[]{
                        "Earn money and experience",
                        "by crafting."},
                "Crafting", 100));
    }


    @Override
    public void addData(ProfessionBuilder builder) {
        builder.addExperienceScaling(defaultLevelParser())
                .incomeScaling(defaultIncomeParser())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .reward(expReward(2))
                        .reward(moneyReward(2))
                        .item(ItemTags.WOODEN_PRESSURE_PLATES)
                        .item(ItemTags.BANNERS)
                        .item(ItemTags.WALLS)
                        .item(ItemTags.STAIRS)
                        .item(ItemTags.FENCES)
                        .item(ItemTags.BOATS)
                        .item(ItemTags.WOODEN_DOORS)
                        .item(ItemTags.WOODEN_TRAPDOORS)
                        .item(ItemTags.WOODEN_BUTTONS)
                        .item(ItemTags.CANDLES)
                        .item(ItemTags.SIGNS)
                        .item(Items.LADDER)
                        .item(Items.TINTED_GLASS)
                        .item(Items.STONE_PRESSURE_PLATE)
                        .item(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE)
                        .item(Items.LIGHT_WEIGHTED_PRESSURE_PLATE)
                        .item(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                        .item(Items.PAINTING)
                        .item(Items.FLOWER_POT)
                        .item(Items.LEAD)
                        .item(Items.ARMOR_STAND)
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .reward(expReward(4))
                        .reward(moneyReward(4))
                        .item(Items.CHEST, Items.BARREL)
                        .item(Items.FURNACE, Items.MINECART)
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.ANVIL, Items.JUKEBOX, Items.TNT)
                        .reward(expReward(6))
                        .reward(moneyReward(5))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.DISPENSER, Items.BLAST_FURNACE, Items.PISTON, Items.CHAIN, Items.STICKY_PISTON, Items.OBSERVER,
                                Items.FISHING_ROD, Items.SPYGLASS)
                        .reward(expReward(3))
                        .reward(moneyReward(3))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.BEACON)
                        .reward(expReward(200))
                        .reward(moneyReward(200))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.IRON_DOOR, Items.IRON_BARS, Items.HOPPER, Items.CAULDRON, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.RAIL,
                                Items.ACTIVATOR_RAIL)
                        .reward(expReward(10))
                        .reward(moneyReward(10))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.LODESTONE)
                        .reward(expReward(30))
                        .reward(moneyReward(30))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.COMPARATOR, Items.COMPASS, Items.CLOCK, Items.BREWING_STAND, Items.REPEATER, Items.DAYLIGHT_DETECTOR)
                        .item(ItemTags.BEDS)
                        .reward(expReward(6))
                        .reward(moneyReward(6))
                        .build())
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(Items.WHITE_CONCRETE_POWDER, Items.ORANGE_CONCRETE_POWDER, Items.MAGENTA_CONCRETE_POWDER,
                                Items.LIGHT_BLUE_CONCRETE_POWDER, Items.YELLOW_CONCRETE_POWDER, Items.LIME_CONCRETE_POWDER,
                                Items.PINK_CONCRETE_POWDER, Items.GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_CONCRETE_POWDER, Items.CYAN_CONCRETE_POWDER,
                                Items.BLUE_CONCRETE_POWDER, Items.BROWN_CONCRETE_POWDER, Items.GREEN_CONCRETE_POWDER, Items.RED_CONCRETE_POWDER,
                                Items.BLACK_CONCRETE_POWDER, Items.LECTERN, Items.REDSTONE_LAMP, Items.LIGHTNING_ROD)
                        .reward(expReward(2))
                        .reward(moneyReward(2)))
                .addAction(CRAFTS_ITEM, CraftingAction.craft()
                        .item(ItemTags.CARPETS)
                        .item(Items.DROPPER, Items.TRIPWIRE_HOOK, Items.TRAPPED_CHEST, Items.REDSTONE_TORCH, Items.TORCH)
                        .reward(expReward(1))
                        .reward(moneyReward(1)));
    }
}
