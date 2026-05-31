package com.epherical.professions.model.gating;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.actions.GateRequirement;

public record GateReportData(GateRequirement gateRequirement, Occupation occupation, ProfessionContext context) {}
