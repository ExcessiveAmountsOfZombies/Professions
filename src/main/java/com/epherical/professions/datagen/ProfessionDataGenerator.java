package com.epherical.professions.datagen;

import com.epherical.octoecon.api.Economy;
import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.profession.action.Actions;
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
import net.minecraft.world.item.Items;

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
                            .withItem(Items.NETHER_WART)
                            .reward(expReward(2))
                            .reward(moneyReward(0.5))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.REDSTONE, Items.GLOWSTONE_DUST)
                            .reward(expReward(2))
                            .reward(moneyReward(0.65)
                            ).build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.FERMENTED_SPIDER_EYE)
                            .reward(expReward(4))
                            .reward(moneyReward(1))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.GUNPOWDER)
                            .reward(expReward(4))
                            .reward(moneyReward(0.85))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.DRAGON_BREATH)
                            .reward(expReward(6))
                            .reward(moneyReward(1))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.SUGAR)
                            .reward(expReward(0.5))
                            .reward(moneyReward(0.25))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.RABBIT_FOOT, Items.TURTLE_HELMET, Items.PHANTOM_MEMBRANE)
                            .reward(expReward(10))
                            .reward(moneyReward(5))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.GLISTERING_MELON_SLICE)
                            .reward(expReward(3))
                            .reward(moneyReward(1))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.SPIDER_EYE, Items.MAGMA_CREAM, Items.BLAZE_POWDER)
                            .reward(expReward(2))
                            .reward(moneyReward(0.5))
                            .build())
                    .addAction(Actions.BREW_ITEM, BrewAction.brew()
                            .withItem(Items.PUFFERFISH, Items.GOLDEN_CARROT, Items.GHAST_TEAR)
                            .reward(expReward(5))
                            .reward(moneyReward(2))
                            .build());
            Path alchemyProfession = createPath(path, new ResourceLocation("professions:alchemy"));
            DataProvider.save(GSON, cache, ProfessionLoader.serialize(alchemy.build()), alchemyProfession);
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
