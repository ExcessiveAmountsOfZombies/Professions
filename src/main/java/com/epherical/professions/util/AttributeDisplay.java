package com.epherical.professions.util;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttributeDisplay {

    private static final Map<Attribute, String> attributeCharacter;

    private final Multimap<Attribute, AttributeValue> occupationMultimap = HashMultimap.create();


    public AttributeDisplay() {

    }

    public void addData(Occupation occupation, double variable, Attribute attribute) {
        this.occupationMultimap.put(attribute, new AttributeValue(occupation, variable));
    }

    public Map<Attribute, MutableComponent> getValues() {
        Map<Attribute, MutableComponent> componentMap = new HashMap<>();
        for (Map.Entry<Attribute, Collection<AttributeValue>> entry : occupationMultimap.asMap().entrySet()) {
            MutableComponent component = Component.literal(attributeCharacter.getOrDefault(entry.getKey(), "⚠"));
            Map<Profession, Double> values = new HashMap<>();
            for (AttributeValue attributeValue : entry.getValue()) {
                Occupation occupation = attributeValue.occupation;
                if (values.containsKey(occupation.getProfession())) {
                    values.put(occupation.getProfession(), attributeValue.var + values.get(occupation.getProfession()));
                } else {
                    values.put(occupation.getProfession(), attributeValue.var);
                }
            }
            double totalValue = 0.0d;
            MutableComponent attributeName = Component.translatable(entry.getKey().getDescriptionId());
            MutableComponent hoverComponent = Component.literal("").append(attributeName).append(" By Occupation.\n");
            int size = values.entrySet().size();
            int increment = 0;
            for (Map.Entry<Profession, Double> profEntry : values.entrySet()) {
                increment++;
                hoverComponent.append(profEntry.getKey().getDisplayComponent())
                        .append(" ")
                        .append(net.minecraft.network.chat.Component.literal("+" + String.format("%.2f", profEntry.getValue())).setStyle(Style.EMPTY.withColor(ProfessionConfig.money)));
                if (increment != size) {
                    hoverComponent.append("\n");
                }

                totalValue += profEntry.getValue();
            }
            component.setStyle(Style.EMPTY
                    .withColor(ProfessionConfig.descriptors)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent)));
            component.append(Component.literal(" " + String.format("%.2f", totalValue)).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)));

            componentMap.put(entry.getKey(), component);
        }

        return componentMap;
    }


    static {
        attributeCharacter = new HashMap<>();
        attributeCharacter.put(Attributes.MAX_HEALTH, "♥");
        attributeCharacter.put(Attributes.KNOCKBACK_RESISTANCE, "⛨");
        attributeCharacter.put(Attributes.MOVEMENT_SPEED, "☁");
        attributeCharacter.put(Attributes.ARMOR, "⛨");
        attributeCharacter.put(Attributes.ARMOR_TOUGHNESS, "⛨");
        attributeCharacter.put(Attributes.ATTACK_DAMAGE, "⚔");
        attributeCharacter.put(Attributes.ATTACK_KNOCKBACK, "⚔");
        attributeCharacter.put(Attributes.ATTACK_SPEED, "⚔");
        attributeCharacter.put(Attributes.LUCK, "☘");
    }

    private record AttributeValue(Occupation occupation, double var) {
    }


}
