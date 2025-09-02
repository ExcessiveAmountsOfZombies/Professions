package com.epherical.professions;

import com.epherical.professions.registries.ActionLoad2;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod(Constants.MOD_ID)
public class ProfessionsMod extends CommonClass {

    public ProfessionsMod() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        //CommonClass.init();
        this.init();

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
