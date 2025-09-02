package com.epherical.professions;

import com.epherical.professions.registries.ActionLoad2;
import net.fabricmc.api.ModInitializer;

import java.io.File;

public class ProfessionsMod extends CommonClass implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        this.init();
        //CommonClass.init();
    }

    @Override
    public ActionLoad2 getActionLoader() {
        return null;
    }

    @Override
    public File getModDir() {
        return null;
    }

    @Override
    public boolean isClientEnvironment() {
        return false;
    }
}
