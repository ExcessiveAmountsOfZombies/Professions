package com.epherical.professions.api.event.runtime.rewards;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.api.actions.Action;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.api.event.runtime.AbstractProfessionEvent;
import net.minecraft.resources.ResourceLocation;

public class CurrencyAwardEvent extends AbstractProfessionEvent {

    public static final EventKey<CurrencyAwardEvent> KEY =
            new EventKey<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "reward_currency"), CurrencyAwardEvent.class);

    private final ProfessionContext context;
    private final IProfessionalPlayer player;
    private final String currencyId;
    private final double baseAmount;
    private final double resolvedAmount;
    private final Action<?> sourceAction;

    public CurrencyAwardEvent(ProfessionContext context, IProfessionalPlayer player, String currencyId, double baseAmount, double resolvedAmount, Action<?> sourceAction) {
        super(KEY);
        this.context = context;
        this.player = player;
        this.currencyId = currencyId;
        this.baseAmount = baseAmount;
        this.resolvedAmount = resolvedAmount;
        this.sourceAction = sourceAction;
    }

    public ProfessionContext context() { return this.context; }
    public IProfessionalPlayer player() { return this.player; }
    public String currencyId() { return this.currencyId; }
    public double baseAmount() { return this.baseAmount; }
    public double resolvedAmount() { return this.resolvedAmount; }
    public Action<?> sourceAction() { return this.sourceAction; }
}
