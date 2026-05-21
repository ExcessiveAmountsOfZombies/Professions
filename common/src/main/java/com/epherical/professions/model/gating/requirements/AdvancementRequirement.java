package com.epherical.professions.model.gating.requirements;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Requirements;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record AdvancementRequirement(List<ResourceLocation> advancements) implements GateRequirement {

    public static final MapCodec<AdvancementRequirement> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    ResourceLocation.CODEC.listOf().fieldOf("advancements").forGetter(AdvancementRequirement::advancements)
            ).apply(i, AdvancementRequirement::new)
    );

    @Override
    public GateRequirementType getRequirementType() {
        return Requirements.ADVANCEMENT_REQUIREMENT;
    }

    @Override
    public boolean test(ProfessionContext context, Occupation occupation) {
        if (advancements.isEmpty()) {
            return true;
        }


        IProfessionalPlayer professionalPlayer = context.getParameter(ProfessionParameter.THIS_PLAYER);

        Player player = professionalPlayer.getPlayer();
        // todo; ehhh not sure here, we might be able to check both sides?
        if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.getServer() == null) {
            return false;
        }

        for (ResourceLocation advancement : advancements) {
            if (!hasAdvancement(serverPlayer, serverPlayer.getAdvancements(), advancement)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasAdvancement(ServerPlayer player, PlayerAdvancements advancements, ResourceLocation key) {
        AdvancementHolder holder = player.getServer().getAdvancements().get(key);
        if (holder == null) {
            return false;
        }
        return advancements.getOrStartProgress(holder).isDone();
    }

    public static class Builder implements GateRequirement.Builder {
        private final List<ResourceLocation> advancements = new ArrayList<>();

        public Builder advancement(ResourceLocation advancement) {
            advancements.add(advancement);
            return this;
        }

        public Builder advancements(Collection<ResourceLocation> advancements) {
            this.advancements.addAll(advancements);
            return this;
        }

        @Override
        public GateRequirement build() {
            return new AdvancementRequirement(List.copyOf(advancements));
        }
    }
}
