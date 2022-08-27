package com.epherical.professions.profession.modifiers.milestones;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.modifiers.milestones.builtin.CommandMilestone;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.Constants.modID;

public class Milestones {

    public static final MilestoneType ADMIN_RAN_COMMAND = register(modID("admin_ran_command"), new CommandMilestone.MilestoneSerializer());


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.MILESTONE_TYPE, "milestone", "milestone", Milestone::getType).build();
    }

    public static MilestoneType register(ResourceLocation location, Serializer<? extends Milestone> serializer) {
        return Registry.register(RegistryConstants.MILESTONE_TYPE, location, new MilestoneType(serializer));
    }
}
