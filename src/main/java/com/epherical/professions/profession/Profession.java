package com.epherical.professions.profession;

import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.progression.Occupation;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Profession {
    protected final TextColor color;
    protected final TextColor descriptionColor;
    protected final String[] description;
    protected final String displayName;
    protected final int maxLevel;
    protected final Map<ActionType, Collection<Action>> actions;
    protected final Parser experienceScalingEquation;
    protected final Parser incomeScalingEquation;

    protected ResourceLocation key;

    public Profession(TextColor color, TextColor descriptionColor, String[] description, String displayName, int maxLevel, Map<ActionType, Collection<Action>> actions,
                      Parser experienceScalingEquation, Parser incomeScalingEquation) {
        this.color = color;
        this.description = description;
        this.descriptionColor = descriptionColor;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.actions = actions;
        this.experienceScalingEquation = experienceScalingEquation;
        this.incomeScalingEquation = incomeScalingEquation;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getDescription() {
        return description;
    }

    public TextColor getDescriptionColor() {
        return descriptionColor;
    }

    public TextColor getColor() {
        return color;
    }

    public Map<ActionType, Collection<Action>> getActions() {
        return actions;
    }

    @Nullable
    public Collection<Action> getActions(ActionType type) {
        return actions.get(type);
    }

    public double getExperienceForLevel(int level) {
        experienceScalingEquation.setVariable("lvl", level);
        return experienceScalingEquation.getValue();
    }

    public double getIncomeForLevel(double income) {
        incomeScalingEquation.setVariable("base", income);
        return incomeScalingEquation.getValue();
    }

    public boolean isSameProfession(Profession profession) {
        return Objects.equals(this, profession);
    }

    public void setKey(ResourceLocation key) {
        this.key = key;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public Component createBrowseMessage() {
        MutableComponent name = new TextComponent(displayName).setStyle(Style.EMPTY.withColor(color));
        MutableComponent level = new TextComponent("" + maxLevel).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        MutableComponent hoverText = new TextComponent("").setStyle(Style.EMPTY.withColor(descriptionColor));
        for (String s : getDescription()) {
            hoverText.append(s);
        }

        return new TranslatableComponent("%s. Max Level: %s", name, level)
                .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
    }


    @NotNull
    public ProfessionSerializer<Profession> getSerializer() {
        return ProfessionSerializer.DEFAULT_PROFESSION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profession that = (Profession) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public static void toNetwork(FriendlyByteBuf buf, List<Occupation> occupations) {
        buf.writeResourceLocation(ProfessionConstants.OPEN_UI_RESPONSE);
        buf.writeVarInt(occupations.size());
        for (Occupation occupation : occupations) {
            Profession profession = occupation.getProfession();
            buf.writeResourceLocation(com.epherical.professions.ProfessionConstants.PROFESSION_SERIALIZER.getKey(profession.getSerializer()));
            profession.getSerializer().toClient(buf, profession);
            buf.writeVarInt(occupation.getLevel());
            buf.writeDouble(occupation.getExp());
            buf.writeVarInt(occupation.getMaxExp());
        }
    }

    public static class Serializer implements ProfessionSerializer<Profession> {


        @Override
        public Profession deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = GsonHelper.convertToJsonObject(json, "profession object");
            TextColor professionColor = TextColor.parseColor(GsonHelper.getAsString(object, "color", "#FF0000"));
            TextColor descriptionColor = TextColor.parseColor(GsonHelper.getAsString(object, "descriptionColor", "#FFFFFF"));
            String[] description = GsonHelper.getAsObject(object, "description", context, String[].class);
            String displayName = GsonHelper.getAsString(object, "displayName");
            int maxLevel = GsonHelper.getAsInt(object, "maxLevel");
            Action[] actions = GsonHelper.getAsObject(object, "actions", new Action[0], context, Action[].class);
            Multimap<ActionType, Action> actionMap = HashMultimap.create();
            // TODO: order isn't kept anymore, need to fix
            for (Action action : actions) {
                actionMap.put(action.getType(), action);
            }
            Parser experienceScaling = new Parser(GsonHelper.getAsString(object, "experienceSclEquation"));
            Parser incomeScaling = new Parser(GsonHelper.getAsString(object, "incomeSclEquation"));
            return new Profession(professionColor, descriptionColor, description, displayName, maxLevel, actionMap.asMap(), experienceScaling, incomeScaling);
        }

        @Override
        public JsonElement serialize(Profession src, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }

        @Override
        public Profession fromServer(FriendlyByteBuf buf) {
            ResourceLocation location = buf.readResourceLocation();
            TextColor color = TextColor.parseColor(buf.readUtf());
            TextColor descColor = TextColor.parseColor(buf.readUtf());
            String displayName = buf.readUtf();
            String[] description = new String[buf.readVarInt()];
            for (int i = 0; i < description.length; i++) {
                description[i] = buf.readUtf();
            }
            Profession profession = new Profession(color, descColor, description, displayName, -1, ImmutableMap.of(), null, null);
            profession.setKey(location);
            return profession;
        }

        @Override
        public void toClient(FriendlyByteBuf buf, Profession profession) {
            buf.writeResourceLocation(profession.key);
            buf.writeUtf(profession.color.serialize());
            buf.writeUtf(profession.descriptionColor.serialize());
            buf.writeUtf(profession.displayName);
            buf.writeVarInt(profession.description.length);
            for (String s : profession.description) {
                buf.writeUtf(s);
            }
        }

        @Override
        public Class<Profession> getType() {
            return Profession.class;
        }
    }
}
