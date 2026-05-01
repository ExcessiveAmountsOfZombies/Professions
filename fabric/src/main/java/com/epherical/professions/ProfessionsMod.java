package com.epherical.professions;

import com.epherical.professions.registries.ActionLoad3;
import com.epherical.professions.registries.CategoryLoad3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;

import java.io.File;

public class ProfessionsMod extends CommonClass implements ModInitializer {

    private final ActionManager actionManager = new ActionManager(null);
    private final ProfessionCategoryManager categoryManager = new ProfessionCategoryManager();
    private ActionLoad3 actionLoader;
    private CategoryLoad3 categoryLoader;

    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");
        this.init();
        this.buildConfig();

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {

        });

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(FabricActionReloadListener.ID, provider -> {
            ActionLoad3 loader = new ActionLoad3(actionManager);
            this.actionLoader = loader;
            ACTION_LOAD2 = loader;
            return new FabricActionReloadListener(loader, provider);
        });
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(FabricCategoryReloadListener.ID, provider -> {
            CategoryLoad3 loader = new CategoryLoad3(categoryManager);
            this.categoryLoader = loader;
            return new FabricCategoryReloadListener(loader, provider);
        });
    }

    @Override
    public ActionLoad3 getActionLoader() {
        return actionLoader;
    }

    @Override
    public File getModDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    @Override
    public boolean isClientEnvironment() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
