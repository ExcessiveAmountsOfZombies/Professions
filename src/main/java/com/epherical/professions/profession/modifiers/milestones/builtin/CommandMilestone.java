package com.epherical.professions.profession.modifiers.milestones.builtin;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.modifiers.milestones.Milestone;
import com.epherical.professions.profession.modifiers.milestones.MilestoneType;
import com.epherical.professions.profession.modifiers.milestones.Milestones;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class CommandMilestone extends AbstractLevelMilestone {

    private final String command;

    public CommandMilestone(int level, String command) {
        super(level);
        this.command = command;
    }

    @Override
    public MilestoneType getType() {
        return Milestones.ADMIN_RAN_COMMAND;
    }

    @Override
    public boolean giveMilestoneReward(ProfessionalPlayer context, Occupation occupation) {
        if (context.getPlayer() != null) {
            ServerPlayer player = context.getPlayer();
            String replacement = command.formatted(player.getDisplayName().getContents());
            player.getServer().getCommands().performPrefixedCommand(player.getServer().createCommandSourceStack(), replacement);
            return true;
        }

        return false;
    }

    public static Builder commandMilestone() {
        return new Builder();
    }

    public static class Builder extends AbstractLevelMilestone.Builder<Builder> {
        private String command = "";

        @Override
        public Milestone build() {
            return new CommandMilestone(getLevel(), command);
        }

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        @Override
        protected Builder instance() {
            return this;
        }
    }

    public static class MilestoneSerializer extends AbstractLevelMilestoneSerializer<CommandMilestone> {

        @Override
        public void serialize(JsonObject jsonObject, CommandMilestone commandMilestone, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("command", commandMilestone.command);
        }

        @Override
        public CommandMilestone deserialize(JsonObject object, JsonDeserializationContext context, int level) {
            String command = GsonHelper.getAsString(object, "command");
            return new CommandMilestone(level, command);
        }
    }
}
