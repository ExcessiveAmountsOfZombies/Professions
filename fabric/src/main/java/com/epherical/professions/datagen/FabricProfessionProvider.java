package com.epherical.professions.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.util.concurrent.CompletableFuture;

public class FabricProfessionProvider extends CommonProvider implements DataProvider, ProviderHelpers {

    private final DataGenerator dataGenerator;

    public FabricProfessionProvider(FabricDataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }


    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return null;
        /*Path path = this.dataGenerator.getOutputFolder();
        AlchemistProvider alchemistProvider = new AlchemistProvider();
        BuilderProvider builderProvider = new BuilderProvider();
        CraftingProvider crafting = new CraftingProvider();
        EnchantingProvider enchanting = new EnchantingProvider();
        FarmingProvider farming = new FarmingProvider();
        FishingProvider fishing = new FishingProvider();
        HuntingProvider hunting = new HuntingProvider();
        MiningProvider mining = new MiningProvider();
        TradingProvider trading = new TradingProvider();
        SmithingProvider smithing = new SmithingProvider();
        LoggingProvider logging = new LoggingProvider();

        alchemistProvider.generateNormal(cache, path, new ResourceLocation("professions:alchemy"), false);
        builderProvider.generateNormal(cache, path, new ResourceLocation("professions:building"), false);
        crafting.generateNormal(cache, path, new ResourceLocation("professions:crafting"), false);
        enchanting.generateNormal(cache, path, new ResourceLocation("professions:enchanting"), false);
        farming.generateNormal(cache, path, new ResourceLocation("professions:farming"), false);
        fishing.generateNormal(cache, path, new ResourceLocation("professions:fishing"), false);
        hunting.generateNormal(cache, path, new ResourceLocation("professions:hunting"), false);
        mining.generateNormal(cache, path, new ResourceLocation("professions:mining"), false);
        trading.generateNormal(cache, path, new ResourceLocation("professions:trading"), false);
        smithing.generateNormal(cache, path, new ResourceLocation("professions:smithing"), false);
        logging.generateNormal(cache, path, new ResourceLocation("professions:logging"), false);


        generate(cache, mining.createMiningAppender().build(), createHardcoreAppenders(path, Constants.modID("appenders/mining"), false));
        generate(cache, smithing.createSmithingAppender().build(), createHardcoreAppenders(path, Constants.modID("appenders/smithing"), false));*/
    }

    @Override
    public String getName() {
        return "Professions";
    }

}
