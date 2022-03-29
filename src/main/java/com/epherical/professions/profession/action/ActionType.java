package com.epherical.professions.profession.action;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

public class ActionType extends SerializerType<Action> {

    private final String displayName;

    /**
     * @param serializer The serializer that goes with this action.
     * @param displayName Either a hardcoded string or a translation key.
     */
    public ActionType(Serializer<? extends Action> serializer, String displayName) {
        super(serializer);
        this.displayName = displayName;
    }

    /**
     * see {@link com.epherical.professions.commands.ProfessionsCommands#info(CommandContext)}
     * @return the display name to be shown in chat when players use /professions info
     */
    public String getDisplayName() {
        return displayName;
    }
}
