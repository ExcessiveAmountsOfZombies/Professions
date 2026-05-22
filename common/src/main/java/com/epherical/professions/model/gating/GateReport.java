package com.epherical.professions.model.gating;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.data.config.ProfessionConfig;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.requirements.GateRequirement;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class GateReport {

    private static final long MESSAGE_COOLDOWN_MILLIS = 3_000L;
    private static final Map<UUID, Long> LAST_FAILURE_MESSAGE_TIMESTAMPS = new ConcurrentHashMap<>();

    private boolean allowed;
    private final Multimap<GateType, GateData> failuresMap = ArrayListMultimap.create();

    public GateReport() {
        this.allowed = true;
    }


    public void failed(GateRequirement gateRequirement, Gate<?> gate, Occupation occupation, ProfessionContext context) {
        allowed = false;
        failuresMap.put(gate.getGateType(), new GateData(gateRequirement, occupation, context));
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void sendFailureMessage(Player player) {
        long now = System.currentTimeMillis();
        Long lastSentAt = LAST_FAILURE_MESSAGE_TIMESTAMPS.get(player.getUUID());
        if (lastSentAt != null && now - lastSentAt < MESSAGE_COOLDOWN_MILLIS) {
            return;
        }
        LAST_FAILURE_MESSAGE_TIMESTAMPS.put(player.getUUID(), now);

        MutableComponent start = Component.empty().setStyle(Style.EMPTY.withColor(ProfessionConfig.errors));
        for (GateType gateType : failuresMap.keySet()) {
            Collection<GateData> gateRequirements = failuresMap.get(gateType);

            start.append(Component.translatable("professions.gate.requirement.start.failureMessage",
                    Component.translatable(gateType.translationKey()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))
            ));
            for (GateData gateRequirement : gateRequirements) {
                start.append(gateRequirement.gateRequirement().failureMessage(gateRequirement.occupation, gateRequirement.context))
                        .append("\n");
            }
        }
        player.sendSystemMessage(start);
    }

    private record GateData(GateRequirement gateRequirement, Occupation occupation, ProfessionContext context) {}
}
