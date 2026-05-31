package com.epherical.professions.model.perks;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.perks.IStartupPerk;
import com.epherical.professions.api.perks.Perk;
import com.epherical.professions.bootstrap.Perks;
import com.epherical.professions.model.Occupation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class PerkAttribute extends Perk implements IStartupPerk {

    public static final MapCodec<PerkAttribute> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    COMMON.forGetter(PerkAttribute::buildCommon),
                    Attribute.CODEC.fieldOf("attribute").forGetter(PerkAttribute::getAttribute)
            ).apply(i, PerkAttribute::new)
    );

    private final Holder<Attribute> attribute;


    public PerkAttribute(Common common, Holder<Attribute> attribute) {
        super(common);
        this.attribute = attribute;
    }

    public Holder<Attribute> getAttribute() {
        return attribute;
    }

    @Override
    public PerkType getType() {
        return Perks.ATTRIBUTE_PERK;
    }

    @Override
    public Common buildCommon() {
        return Common.build(this);
    }

    @Override
    public ResourceLocation getGroupId() {
        return ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID,
                attribute.unwrapKey().get().location().toDebugFileName() + "_" + getModificationStage().getSerializedName());
    }

    @Override
    public Applicator applicator(ServerPlayer serverPlayer) {
        return (stage, startingValue,v) -> {
            AttributeInstance attribute = serverPlayer.getAttribute(getAttribute());
            if (attribute == null) {
                return 0.0d;
            }

            ResourceLocation key = getGroupId();

            AttributeModifier modifier = attribute.getModifier(key);
            double newValue = startingValue;
            if (modifier != null) {
                ProfessionsCommon.LOG.debug("Modifier wasn't null for key: {}", key);
                newValue = startingValue + modifier.amount();
            }


            if (Perk.ModificationStage.FLAT.equals(stage)) {
                newValue = stage.apply(0.0d, v);
                ProfessionsCommon.LOG.debug("Applying Flat modification: {}", newValue);
                modifier = new AttributeModifier(key, newValue, AttributeModifier.Operation.ADD_VALUE);
            } else if (Perk.ModificationStage.ADDITIVE.equals(stage)) {
                newValue = stage.apply(attribute.getBaseValue() + startingValue, v);
                ProfessionsCommon.LOG.debug("Applying Additive modification: {}", newValue);
                modifier = new AttributeModifier(key, newValue, AttributeModifier.Operation.ADD_VALUE);
            } else if (Perk.ModificationStage.MULTIPLICATIVE.equals(stage)) {
                newValue = stage.apply(attribute.getBaseValue() + startingValue, v);
                ProfessionsCommon.LOG.debug("Applying Multiplicative modification: {}", newValue);
                modifier = new AttributeModifier(key, newValue, AttributeModifier.Operation.ADD_VALUE);
            }


            attribute.addOrReplacePermanentModifier(modifier);
            return newValue;
        };
    }

    @Override
    public void onDeactivate(Occupation occupation, IProfessionalPlayer player, ServerPlayer serverPlayer) {
        super.onDeactivate(occupation, player, serverPlayer);
        AttributeInstance attribute = serverPlayer.getAttribute(getAttribute());
        if (attribute == null) {
            return;
        }

        attribute.removeModifier(getGroupId());
    }

    @Override
    public PerkStatus onRecalculate(Occupation occupation, IProfessionalPlayer player, ServerPlayer serverPlayer) {
        return super.onRecalculate(occupation, player, serverPlayer);
    }

}
