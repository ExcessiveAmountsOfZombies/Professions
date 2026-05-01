package com.epherical.professions;

import com.epherical.professions.registries.ActionLoad3;
import com.epherical.professions.registries.CategoryLoad3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod(Constants.MOD_ID)
public class ProfessionsMod extends CommonClass {

    private final ActionManager actionManager;
    private final ProfessionCategoryManager categoryManager;
    private ActionLoad3 actionLoader;
    private CategoryLoad3 categoryLoader;

    public ProfessionsMod() {
        Constants.LOG.info("Hello Forge world!");
        this.init();
        this.buildConfig();
        this.actionManager = new ActionManager(null);
        this.categoryManager = new ProfessionCategoryManager();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDataReload(AddReloadListenerEvent event) {
        ActionLoad3 loader = new ActionLoad3(actionManager);
        event.addListener(new ForgeActionReloadListener(loader, event.getRegistries()));
        this.actionLoader = loader;
        ACTION_LOAD2 = loader;

        CategoryLoad3 categoryLoader = new CategoryLoad3(categoryManager);
        event.addListener(new ForgeCategoryReloadListener(categoryLoader, event.getRegistries()));
        this.categoryLoader = categoryLoader;
    }

    @Override
    public ActionLoad3 getActionLoader() {
        return actionLoader;
    }

    @Override
    public File getModDir() {
        return FMLPaths.CONFIGDIR.get().toFile();
    }

    @Override
    public boolean isClientEnvironment() {
        return FMLEnvironment.dist.isClient();
    }
}
