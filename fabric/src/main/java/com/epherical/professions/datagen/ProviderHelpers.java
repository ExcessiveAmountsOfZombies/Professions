package com.epherical.professions.datagen;

import com.epherical.octoecon.api.Economy;
import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.datapack.FabricProfLoader;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.editor.Editor;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.builtin.OccupationExperience;
import com.epherical.professions.profession.rewards.builtin.PaymentReward;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;

public interface ProviderHelpers {

    default Path createNormalPath(Path path, ResourceLocation id, boolean forge) {
        String namespace = id.getNamespace();
        if (forge) {
            return path.resolve("resourcepacks/forge/normal/data/" + namespace + "/professions/occupations/" + id.getPath() + ".json");
        }
        return path.resolve("resourcepacks/fabric/normal/data/" + namespace + "/professions/occupations/" + id.getPath() + ".json");
    }

    default Path createHardcoreAppenders(Path path, ResourceLocation id, boolean forge) {
        String namespace = id.getNamespace();
        if (forge) {
            return path.resolve("resourcepacks/forge/hardcore/data/" + namespace + "/professions/occupations/" + id.getPath() + ".json");
        }
        return path.resolve("resourcepacks/fabric/hardcore/data/" + namespace + "/professions/occupations/" + id.getPath() + ".json");
    }

    default void generate(CachedOutput cache, Profession profession, Path id) throws IOException {
        DataProvider.saveStable(cache, FabricProfLoader.serialize(profession), id);
    }

    default void generate(CachedOutput cache, Editor editor, Path id) throws IOException {
        DataProvider.saveStable(cache, FabricProfLoader.serialize(editor), id);
    }

    default Parser defaultLevelParser() {
        return new Parser("(1000)*((1.064)^(lvl-1))");
    }

    default Parser defaultIncomeParser() {
        return new Parser("base");
    }

    default Reward.Builder expReward(double reward) {
        return OccupationExperience.builder().exp(reward);
    }

    default Reward.Builder moneyReward(double amount) {
        Economy economy = ProfessionsFabric.getEconomy();
        if (economy == null) {
            return PaymentReward.builder().money(amount, null);
        } else {
            return PaymentReward.builder().money(amount, economy.getDefaultCurrency());
        }
    }
}
