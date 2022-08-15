package com.epherical.professions.util;

import com.epherical.professions.profession.unlock.Unlock;
import net.minecraft.network.chat.MutableComponent;

public class UnlockErrorHelper {

    private final MutableComponent component;


    public UnlockErrorHelper(MutableComponent header) {
        this.component = header;
    }

    public void newLine() {
        component.append("\n");
    }

    public <T> void levelRequirementNotMet(Unlock.Singular<T> singular) {
        // todo; weird code, is this class important?
        component.append(singular.createUnlockComponent());
        /*component.append(Component.translatable("%s %s",
                singular.getProfessionDisplay(),
                Component.literal(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))));*/
    }

    public MutableComponent getComponent() {
        return component;
    }
}
