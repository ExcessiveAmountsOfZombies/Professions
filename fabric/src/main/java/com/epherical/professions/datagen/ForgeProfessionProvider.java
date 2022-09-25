package com.epherical.professions.datagen;

import com.epherical.professions.Constants;
import com.epherical.professions.datagen.defaults.AlchemistProvider;
import com.epherical.professions.datagen.defaults.BuilderProvider;
import com.epherical.professions.datagen.defaults.CraftingProvider;
import com.epherical.professions.datagen.defaults.EnchantingProvider;
import com.epherical.professions.datagen.defaults.FarmingProvider;
import com.epherical.professions.datagen.defaults.FishingProvider;
import com.epherical.professions.datagen.defaults.HuntingProvider;
import com.epherical.professions.datagen.defaults.LoggingProvider;
import com.epherical.professions.datagen.defaults.MiningProvider;
import com.epherical.professions.datagen.defaults.SmithingProvider;
import com.epherical.professions.datagen.defaults.TradingProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;

public class ForgeProfessionProvider extends CommonProvider implements DataProvider, ProviderHelpers {

    private final DataGenerator dataGenerator;

    public ForgeProfessionProvider(FabricDataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        Path path = this.dataGenerator.getOutputFolder();
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

        alchemistProvider.generateNormal(GSON, cache, path, new ResourceLocation("professions:alchemy"), true);
        builderProvider.generateNormal(GSON, cache, path, new ResourceLocation("professions:building"), true);
        crafting.generateNormal(GSON, cache, path, new ResourceLocation("professions:crafting"), true);
        enchanting.generateNormal(GSON, cache, path, new ResourceLocation("professions:enchanting"), true);
        farming.generateNormal(GSON, cache, path, new ResourceLocation("professions:farming"), true);
        fishing.generateNormal(GSON, cache, path, new ResourceLocation("professions:fishing"), true);
        hunting.generateNormal(GSON, cache, path, new ResourceLocation("professions:hunting"), true);
        mining.generateNormal(GSON, cache, path, new ResourceLocation("professions:mining"), true);
        trading.generateNormal(GSON, cache, path, new ResourceLocation("professions:trading"), true);
        smithing.generateNormal(GSON, cache, path, new ResourceLocation("professions:smithing"), true);
        logging.generateNormal(GSON, cache, path, new ResourceLocation("professions:logging"), true);


        generate(GSON, cache, createMiningAppender().build(), createHardcoreAppenders(path, Constants.modID("appenders/mining"), true));
    }

    @Override
    public String getName() {
        return "Professions";
    }

}

