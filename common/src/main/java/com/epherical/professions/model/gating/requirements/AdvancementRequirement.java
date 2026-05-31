package com.epherical.professions.model.gating.requirements;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.actions.Gate;
import com.epherical.professions.api.actions.GateRequirement;
import com.epherical.professions.bootstrap.Requirements;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.GateReport;
import com.epherical.professions.util.ClientAdvancementUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record AdvancementRequirement(ResourceLocation advancement) implements GateRequirement {

    public static final MapCodec<AdvancementRequirement> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    ResourceLocation.CODEC.fieldOf("advancement").forGetter(AdvancementRequirement::advancement)
            ).apply(i, AdvancementRequirement::new)
    );

    @Override
    public GateRequirementType getRequirementType() {
        return Requirements.ADVANCEMENT_REQUIREMENT;
    }

    @Override
    public Component failureMessage(Occupation occupation, ProfessionContext context) {
        return Component.translatable("professions.gate.requirement.advancement.failureMessage",
                Component.literal(occupation.getProfession().value().displayNameRaw())
                        .setStyle(Style.EMPTY.withColor(occupation.getProfession().value().professionColor())));
    }

    @Override
    public boolean test(ProfessionContext context, Occupation occupation) {
        if (advancement() == null) {
            return true;
        }

        IProfessionalPlayer professionalPlayer = context.getParameter(ProfessionParameter.THIS_PLAYER);
        Player player = professionalPlayer.getPlayer();

        // If it's just single player we'll do the hasServerAdvancements check because the result would be the same.
        if (context.isClientSide() && !ClientAdvancementUtil.isSingleplayer()) {
            return ClientAdvancementUtil.hasAdvancements(advancement());
        }

        return hasServerAdvancements(player);
    }

    private boolean hasServerAdvancements(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.getServer() == null) {
            return false;
        }
        return hasServerAdvancements(serverPlayer, advancement());
    }

    private boolean hasServerAdvancements(ServerPlayer serverPlayer, ResourceLocation advancementKeys) {
        PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();
        return hasAdvancement(serverPlayer, playerAdvancements, advancementKeys);
    }

    private static boolean hasAdvancement(ServerPlayer player, PlayerAdvancements advancements, ResourceLocation key) {
        AdvancementHolder holder = player.getServer().getAdvancements().get(key); // todo; null check maybe
        if (holder == null) {
            return false;
        }
        return advancements.getOrStartProgress(holder).isDone();
    }

    @Override
    public void test(ProfessionContext context, Occupation occupation, GateReport gateReport, Gate<?> gate) {
        if (!test(context, occupation)) {
            gateReport.failed(this, gate, occupation, context);
        } else {
            gateReport.success(this, gate, occupation, context);
        }
    }

    public static class Builder implements GateRequirement.Builder {
        private ResourceLocation advancements = null;

        public Builder advancement(ResourceLocation advancement) {
            advancements = advancement;
            return this;
        }

        @Override
        public GateRequirement build() {
            return new AdvancementRequirement((advancements));
        }
    }
}
