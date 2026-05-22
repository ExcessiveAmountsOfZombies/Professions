package com.epherical.professions.model.gating.requirements;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Requirements;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.util.ClientAdvancementUtil;
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
        if (advancements().isEmpty()) {
            return true;
        }

        IProfessionalPlayer professionalPlayer = context.getParameter(ProfessionParameter.THIS_PLAYER);
        Player player = professionalPlayer.getPlayer();

        // If it's just single player we'll do the hasServerAdvancements check because the result would be the same.
        if (context.isClientSide() && !ClientAdvancementUtil.isSingleplayer()) {
            return ClientAdvancementUtil.hasAdvancements(advancements());
        }

        return hasServerAdvancements(player);
    }

    private boolean hasServerAdvancements(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.getServer() == null) {
            return false;
        }
        return hasServerAdvancements(serverPlayer, advancements());
    }

    private boolean hasServerAdvancements(ServerPlayer serverPlayer, List<ResourceLocation> advancementKeys) {
        PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();
        for (ResourceLocation advancementKey : advancementKeys) {
            if (!hasAdvancement(serverPlayer, playerAdvancements, advancementKey)) {
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
