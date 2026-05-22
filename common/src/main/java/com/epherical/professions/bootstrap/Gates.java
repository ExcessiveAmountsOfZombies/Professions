package com.epherical.professions.bootstrap;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.model.gating.BlockBreakGate;
import com.epherical.professions.model.gating.GateType;
import com.epherical.professions.model.gating.LootDropGate;
import com.epherical.professions.model.gating.PlaceGate;
import com.epherical.professions.model.gating.ToolGate;
import com.epherical.professions.util.GateReportPredicate;

import java.util.List;
import java.util.function.BiPredicate;

public class Gates {

    public static final GateType BLOCK_BREAK = PlatformBootstrap.register(
            ProfessionsCommon.GATE_REGISTRY_KEY, "block_break", new GateType(BlockBreakGate.CODEC, "professions.gate.type.block_break"));
    public static final GateType PLACE = PlatformBootstrap.register(
            ProfessionsCommon.GATE_REGISTRY_KEY, "place", new GateType(PlaceGate.CODEC, "professions.gate.type.place"));
    public static final GateType TOOL = PlatformBootstrap.register(
            ProfessionsCommon.GATE_REGISTRY_KEY, "tool", new GateType(ToolGate.CODEC, "professions.gate.type.tool"));
    public static final GateType LOOT_DROP = PlatformBootstrap.register(
            ProfessionsCommon.GATE_REGISTRY_KEY, "loot_drop", new GateType(LootDropGate.CODEC, "professions.gate.type.loot_drop"));

    public static void bootstrap() {}


    public static <U, T> BiPredicate<U, T> andAllConditions(List<BiPredicate<U, T>> conditions) {
        return switch (conditions.size()) {
            case 0 -> (t, u) -> true;
            case 1 -> conditions.getFirst();
            case 2 -> conditions.getFirst().and(conditions.getLast());
            default -> (t, u) -> {
                for (BiPredicate<U, T> condition : conditions) {
                    if (!condition.test(t, u)) {
                        return false;
                    }
                }
                return true;
            };
        };
    }


    public static <U, T> GateReportPredicate<U, T> andAllGateConditions(List<GateReportPredicate<U, T>> conditions) {
        return switch (conditions.size()) {
            case 0 -> (t, u, gr, g) -> {};
            case 1 -> conditions.getFirst();
            case 2 -> conditions.getFirst().and(conditions.getLast());
            default -> (t, u, gr, g) -> {
                for (GateReportPredicate<U, T> condition : conditions) {
                    condition.test(t, u, gr, g);
                }
            };
        };
    }
}
