package com.epherical.professions.core;

import com.epherical.professions.util.ProfessionCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.mbertoli.jfep.Parser;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;


public record Profession(
        Formatting formatting,
        Settings settings,
        ExpScaling expScaling) {

    public static final Codec<Profession> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //ResourceLocation.CODEC.fieldOf("id").forGetter(Profession::key),
            Formatting.CODEC.fieldOf("formatting").forGetter(Profession::formatting),
            Settings.CODEC.fieldOf("settings").forGetter(Profession::settings),
            ExpScaling.CODEC.fieldOf("expScaling").forGetter(Profession::expScaling)
    ).apply(instance, Profession::new));

    public TextColor professionColor() {
        return formatting.nameColor();
    }

    public TextColor descriptionColor() {
        return formatting.descriptionColor();
    }

    public List<String> description() {
        return formatting.description();
    }

    public String displayNameRaw() {
        return formatting.displayName().getString();
    }

    public Component displayName() {
        return formatting.displayName();
    }

    public int maxLevel() {
        return settings.maxLevel();
    }


    public double getExperienceForLevel(int level) {
        if (expScaling.expScalers().isEmpty()) {
            expScaling.defaultExpScale().setVariable("lvl", level);
            return expScaling.defaultExpScale().getValue();
        } else {
            Map.Entry<Integer, Parser> entry = expScaling.expScalers().floorEntry(level);
            if (entry != null && entry.getValue() != null) {
                Parser value = entry.getValue();
                value.setVariable("lvl", level);
                return value.getValue();
            } else {
                entry = expScaling.expScalers().ceilingEntry(level);
                if (entry == null || entry.getValue() == null) {
                    // todo; add logging here
                    return Double.MAX_VALUE;
                } else {
                    Parser value = entry.getValue();
                    value.setVariable("lvl", level);
                    return value.getValue();
                }
            }
        }
    }

    public record Formatting(Component displayName, List<String> description, TextColor nameColor,
                             TextColor descriptionColor, Item icon) {
            public static final Codec<Formatting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ComponentSerialization.CODEC.fieldOf("display").forGetter(Formatting::displayName),
                    Codec.STRING.listOf().fieldOf("description").forGetter(Formatting::description),
                    TextColor.CODEC.fieldOf("nameColor").forGetter(Formatting::nameColor),
                    TextColor.CODEC.fieldOf("descriptionColor").forGetter(Formatting::descriptionColor),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("icon").forGetter(Formatting::icon)
            ).apply(instance, Formatting::new));

        @Override
            public String toString() {
                return "Formatting[" +
                        "displayName=" + displayName + ", " +
                        "description=" + description + ", " +
                        "nameColor=" + nameColor + ", " +
                        "descriptionColor=" + descriptionColor + ", " +
                        "icon=" + icon + ']';
            }

        }

        public record Settings(int maxLevel, ResourceLocation levelUpSound) {
            public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("maxLevel").forGetter(Settings::maxLevel),
                    ResourceLocation.CODEC.fieldOf("levelUpSound").forGetter(Settings::levelUpSound)
            ).apply(instance, Settings::new));
        }

    public record ExpScaling(
            Parser defaultExpScale,
            NavigableMap<Integer, Parser> expScalers
    ) {
        public static final Codec<ExpScaling> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("defaultExpScale")
                        .forGetter(exp -> exp.defaultExpScale().getInputString()),
                ProfessionCodecs.EXP_SCALERS_CODEC.fieldOf("expScalers")
                        .forGetter(ExpScaling::expScalers)
        ).apply(instance, (s, scalers) -> new ExpScaling(new Parser(s), scalers)));
    }
}
