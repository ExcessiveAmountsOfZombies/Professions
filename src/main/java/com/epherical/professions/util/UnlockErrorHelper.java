package com.epherical.professions.util;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.unlock.Unlock;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class UnlockErrorHelper {

    private final MutableComponent component;


    public UnlockErrorHelper(MutableComponent header) {
        this.component = header;
    }

    public void newLine() {
        component.append("\n");
    }

    public <T> void levelRequirementNotMet(Unlock.Singular<T> singular) {
        component.append(Component.translatable("%s %s",
                singular.getProfessionDisplay(),
                Component.literal(String.valueOf(singular.getUnlockLevel())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables))));
    }

    public MutableComponent getComponent() {
        return component;
    }
}
