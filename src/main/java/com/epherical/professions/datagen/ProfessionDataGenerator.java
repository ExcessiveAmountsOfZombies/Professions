package com.epherical.professions.datagen;

import com.epherical.octoecon.api.Economy;
import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.action.builtin.blocks.PlaceBlockAction;
import com.epherical.professions.profession.action.builtin.items.BrewAction;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.builtin.OccupationExperience;
import com.epherical.professions.profession.rewards.builtin.PaymentReward;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.IOException;
import java.nio.file.Path;

public class ProfessionDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(ProfessionProvider::new);
    }


    private static class ProfessionProvider implements DataProvider {

        private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

        private final DataGenerator dataGenerator;

        public ProfessionProvider(FabricDataGenerator dataGenerator) {
            this.dataGenerator = dataGenerator;
        }

        @Override
        public void run(HashCache cache) throws IOException {
            Path path = this.dataGenerator.getOutputFolder();
            ProfessionBuilder alchemy = ProfessionBuilder.profession(
                    TextColor.parseColor("#6816ba"),
                    TextColor.parseColor("#FFFFFF"),
                    new String[]{"Earn money by brewing potions."},
                    "Alchemy", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.NETHER_WART)
                            .reward(expReward(2))
                            .reward(moneyReward(0.5))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.REDSTONE, Items.GLOWSTONE_DUST)
                            .reward(expReward(2))
                            .reward(moneyReward(0.65)
                            ).build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.FERMENTED_SPIDER_EYE)
                            .reward(expReward(4))
                            .reward(moneyReward(1))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.GUNPOWDER)
                            .reward(expReward(4))
                            .reward(moneyReward(0.85))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.DRAGON_BREATH)
                            .reward(expReward(6))
                            .reward(moneyReward(1))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.SUGAR)
                            .reward(expReward(0.5))
                            .reward(moneyReward(0.25))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.RABBIT_FOOT, Items.TURTLE_HELMET, Items.PHANTOM_MEMBRANE)
                            .reward(expReward(10))
                            .reward(moneyReward(5))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.GLISTERING_MELON_SLICE)
                            .reward(expReward(3))
                            .reward(moneyReward(1))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.SPIDER_EYE, Items.MAGMA_CREAM, Items.BLAZE_POWDER)
                            .reward(expReward(2))
                            .reward(moneyReward(0.5))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .item(Items.PUFFERFISH, Items.GOLDEN_CARROT, Items.GHAST_TEAR)
                            .reward(expReward(5))
                            .reward(moneyReward(2))
                            .build());
            ProfessionBuilder builder = ProfessionBuilder.profession(
                            TextColor.parseColor("#03ad31"),
                            TextColor.parseColor("#FFFFFF"),
                            new String[]{"Earn money by placing blocks"},
                            "Building", 100)
                    .addExperienceScaling(defaultLevelParser())
                    .incomeScaling(defaultIncomeParser())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
                                    Blocks.CRACKED_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS,
                                    Blocks.DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLESTONE,
                                    Blocks.POLISHED_BASALT, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.CHISELED_POLISHED_BLACKSTONE)
                            .reward(expReward(1))
                            .reward(moneyReward(1))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS,
                                    Blocks.DARK_OAK_PLANKS, Blocks.WARPED_PLANKS, Blocks.CRIMSON_PLANKS)
                            .reward(expReward(1.3))
                            .reward(moneyReward(1.3))
                            .build())
                    /*.addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place() // todo: figure out how to do block tags so we can do the wool tag
                            .block(WOOL))*/
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.LAPIS_BLOCK, Blocks.NETHERITE_BLOCK,
                                    Blocks.BRICKS, Blocks.BOOKSHELF, Blocks.MOSSY_COBBLESTONE, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN)
                            .reward(expReward(1.5))
                            .reward(moneyReward(1.5))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.PRISMARINE, Blocks.DARK_PRISMARINE, Blocks.SEA_LANTERN, Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)
                            .reward(expReward(1.25))
                            .reward(moneyReward(1.25))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.ANVIL, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.CAMPFIRE,
                                    Blocks.SOUL_CAMPFIRE, Blocks.CRAFTING_TABLE, Blocks.ENCHANTING_TABLE, Blocks.JUKEBOX, Blocks.HOPPER)
                            .reward(expReward(1.5))
                            .reward(moneyReward(1.5))
                            .build())
                    .addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block(Blocks.GLASS, Blocks.GLASS_PANE, Blocks.IRON_BARS, Blocks.BLACK_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS,
                                    Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS,
                                    Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS,
                                    Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS,
                                    Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS,
                                    Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE,
                                    Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE,
                                    Blocks.PINK_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                                    Blocks.CYAN_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE,
                                    Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE,
                                    Blocks.BLACK_STAINED_GLASS_PANE)
                            .reward(expReward(1.3))
                            .reward(moneyReward(1.3))
                            .build());
                    /*.addAction(Actions.PLACE_BLOCK, PlaceBlockAction.place()
                            .block())*/



            Path alchemyProfession = createPath(path, new ResourceLocation("professions:alchemy"));
            Path builderProfession = createPath(path, new ResourceLocation("professions:builder"));
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(alchemy.build()), alchemyProfession);
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(builder.build()), builderProfession);
        }

        private static Path createPath(Path path, ResourceLocation id) {
            String namespace = id.getNamespace();
            return path.resolve("data/" + namespace + "/professions/" + id.getPath() + ".json");
        }

        @Override
        public String getName() {
            return "Professions";
        }

        public static Parser defaultLevelParser() {
            return new Parser("(91+1000)*((1.1)^(lvl-1))");
        }

        public static Parser defaultIncomeParser() {
            return new Parser("base");
        }

        public static Reward.Builder expReward(double reward) {
            return OccupationExperience.builder().exp(reward);
        }

        private static Reward.Builder moneyReward(double amount) {
            Economy economy = ProfessionsMod.getEconomy();
            if (economy == null) {
                return PaymentReward.builder().money(amount, null);
            } else {
                return PaymentReward.builder().money(amount, economy.getDefaultCurrency());
            }
        }
    }


}
