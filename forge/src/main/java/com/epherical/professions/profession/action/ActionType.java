package com.epherical.professions.profession.action;

import com.google.common.reflect.TypeToken;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class ActionType extends SerializerType<Action> implements IForgeRegistryEntry<ActionType> {

    private final String translationKey;

    /**
     * @param serializer The serializer that goes with this action.
     * @param translationKey Either a hardcoded string or a translation key.
     */
    public ActionType(Serializer<? extends Action> serializer, String translationKey) {
        super(serializer);
        this.translationKey = translationKey;
    }

    /**
     * see {@link com.epherical.professions.commands.ProfessionsCommands#info(CommandContext)}
     * @return the display name to be shown in chat when players use /professions info
     */
    public String getTranslationKey() {
        return translationKey;
    }


    private final TypeToken<ActionType> token = new TypeToken<>(getClass()){};
    private ResourceLocation registryName = null;

    @Override
    public ActionType setRegistryName(ResourceLocation name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return registryName != null ? registryName : null;
    }

    @Override
    public Class<ActionType> getRegistryType() {
        return (Class<ActionType>) token.getRawType();
    }
}
