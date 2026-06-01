package com.epherical.professions.util;

import com.epherical.professions.GateManager;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.actions.Gate;
import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.GateReport;

import java.util.Collection;

public final class GateUtil {

    private GateUtil() {}

    public static boolean passesBlockBreakGateChecks(GateManager gateManager, IProfessionalPlayer player, ProfessionContext context) {
        if (!checkGate(gateManager, gateManager.getGatesByType(Gates.BLOCK_BREAK), player, context)) {
            return false;
        }
        return checkGate(gateManager, gateManager.getGatesByType(Gates.TOOL), player, context);
    }

    public static boolean checkGate(GateManager gateManager, Collection<Gate<?>> gates, IProfessionalPlayer player,
                                    ProfessionContext context) {
        if (!gateManager.areGatesEnabled(player) || player.getCategory() == null) {
            return true;
        }

        GateReport gateReport = new GateReport();
        for (Gate<?> gate : gates) {
            Occupation occupation = player.getOccupation(gate.getProfession());
            if (occupation == null || !occupation.isActive() || !player.getCategory().hasProfession(gate.getProfession())) {
                continue;
            }
            gate.meetsRequirements(occupation, context, gateReport);
        }

        if (!gateReport.isAllowed() && player.getPlayer() != null) {
            gateReport.sendFailureMessage(player.getPlayer());
        }

        return gateReport.isAllowed();
    }
}
