package com.epherical.professions.runtime.event;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.core.context.ProfessionContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public class DamagePipelineEvent extends AbstractCancellableProfessionEvent {

    public static final EventKey<DamagePipelineEvent> KEY =
            new EventKey<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "damage_pipeline"), DamagePipelineEvent.class);

    private final DamageSource source;
    private final LivingEntity attacker;
    private final LivingEntity target;
    private final String damageType;
    private final double originalAmount;
    private double workingAmount;
    private final double preAbsorptionAmount;
    private final double finalHealthLoss;
    private final double blockedAmount;
    private final Map<String, Double> reductionBreakdown;
    private final ProfessionContext context;

    public DamagePipelineEvent(DamageSource source, LivingEntity attacker, LivingEntity target, String damageType, double originalAmount, double workingAmount, double preAbsorptionAmount, double finalHealthLoss, double blockedAmount, Map<String, Double> reductionBreakdown, ProfessionContext context) {
        super(KEY);
        this.source = source;
        this.attacker = attacker;
        this.target = target;
        this.damageType = damageType;
        this.originalAmount = originalAmount;
        this.workingAmount = workingAmount;
        this.preAbsorptionAmount = preAbsorptionAmount;
        this.finalHealthLoss = finalHealthLoss;
        this.blockedAmount = blockedAmount;
        this.reductionBreakdown = reductionBreakdown;
        this.context = context;
    }

    public DamageSource source() { return this.source; }
    public LivingEntity attacker() { return this.attacker; }
    public LivingEntity target() { return this.target; }
    public String damageType() { return this.damageType; }
    public double originalAmount() { return this.originalAmount; }
    public double workingAmount() { return this.workingAmount; }
    public void setWorkingAmount(double workingAmount) { this.workingAmount = workingAmount; }
    public double preAbsorptionAmount() { return this.preAbsorptionAmount; }
    public double finalHealthLoss() { return this.finalHealthLoss; }
    public double blockedAmount() { return this.blockedAmount; }
    public Map<String, Double> reductionBreakdown() { return this.reductionBreakdown; }
    public ProfessionContext context() { return this.context; }
}
