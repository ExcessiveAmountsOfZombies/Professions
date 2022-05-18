package com.epherical.professions.integration.ftb;

import com.epherical.professions.ProfessionsMod;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;

public class FTBIntegration {

    public static TaskType PROFESSION;

    public static void init() {
        PROFESSION = TaskTypes.register(new ResourceLocation(ProfessionsMod.MOD_ID, "profession"), ProfessionTaskType::new, () -> {
            return Icon.getIcon("minecraft:item/netherite_pickaxe");
        });
    }

}
