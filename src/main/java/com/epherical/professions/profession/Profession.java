package com.epherical.professions.profession;

import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockSerializer;
import com.epherical.professions.profession.unlock.UnlockType;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    protected final Map<UnlockType<?>, Collection<Unlock<?>>> unlocks;
    protected final Parser experienceScalingEquation;
    protected final Parser incomeScalingEquation;

    protected ResourceLocation key;

    protected final Component displayComponent;

    public Profession(TextColor color, TextColor descriptionColor, String[] description, String displayName, int maxLevel, Map<ActionType, Collection<Action>> actions,
                      Map<UnlockType<?>, Collection<Unlock<?>>> unlocks, Parser experienceScalingEquation, Parser incomeScalingEquation) {
        this.color = color;
        this.description = description;
        this.descriptionColor = descriptionColor;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.actions = actions;
        this.unlocks = unlocks;
        this.experienceScalingEquation = experienceScalingEquation;
        this.incomeScalingEquation = incomeScalingEquation;
        this.displayComponent = new TextComponent(displayName).setStyle(Style.EMPTY.withColor(color));
    }

    public Profession(TextColor color, TextColor descriptionColor, String[] description, String displayName, int maxLevel, Map<ActionType, Collection<Action>> actions,
                      Map<UnlockType<?>, Collection<Unlock<?>>> unlocks, Parser experienceScalingEquation, Parser incomeScalingEquation, ResourceLocation key) {
        this(color, descriptionColor, description, displayName, maxLevel, actions, unlocks, experienceScalingEquation, incomeScalingEquation);
        this.key = key;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Component getDisplayComponent() {
        return displayComponent;
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

    public Map<UnlockType<?>, Collection<Unlock<?>>> getUnlocks() {
        return unlocks;
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
        MutableComponent level = new TextComponent("" + maxLevel).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
        MutableComponent hoverText = new TextComponent("").setStyle(Style.EMPTY.withColor(descriptionColor));
        for (String s : getDescription()) {
            hoverText.append(s);
        }

        // todo: translation
        return new TranslatableComponent("%s. Max Level: %s", name, level)
                .setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
    }


    @NotNull
    public ProfessionSerializer<Profession, ProfessionBuilder> getSerializer() {
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

    public static void toNetwork(FriendlyByteBuf buf, Profession profession, boolean sendUnlocks) {
        buf.writeResourceLocation(RegistryConstants.PROFESSION_SERIALIZER.getKey(profession.getSerializer()));
        profession.getSerializer().toClient(buf, profession, sendUnlocks);
    }

    public static void toNetwork(FriendlyByteBuf buf, Collection<Profession> professions) {
        buf.writeVarInt(professions.size());
        for (Profession profession : professions) {
            Profession.toNetwork(buf, profession, false);
        }
    }

    /**
     * Only call on the CLIENT
     *
     * @return
     */
    public static List<Profession> fromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Profession> professions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation serializer = buf.readResourceLocation();
            Profession profession = RegistryConstants.PROFESSION_SERIALIZER.getOptional(serializer).orElseThrow(()
                    -> new IllegalArgumentException("Unknown profession serializer " + serializer)).fromServer(buf);
            professions.add(profession);
        }
        return professions;
    }

    public static class Serializer implements ProfessionSerializer<Profession, ProfessionBuilder> {


        @Override
        public ProfessionBuilder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = GsonHelper.convertToJsonObject(json, "profession object");
            TextColor professionColor = TextColor.parseColor(GsonHelper.getAsString(object, "color", "#FF0000"));
            TextColor descriptionColor = TextColor.parseColor(GsonHelper.getAsString(object, "descriptionColor", "#FFFFFF"));
            String[] description = GsonHelper.getAsObject(object, "description", context, String[].class);
            String displayName = GsonHelper.getAsString(object, "displayName");
            int maxLevel = GsonHelper.getAsInt(object, "maxLevel");
            ProfessionBuilder builder = ProfessionBuilder.profession(
                    professionColor, descriptionColor, description, displayName, maxLevel);
            Action[] actions = GsonHelper.getAsObject(object, "actions", new Action[0], context, Action[].class);
            for (Action action : actions) {
                builder.addAction(action.getType(), action);
            }
            Unlock<?>[] unlocks = GsonHelper.getAsObject(object, "unlocks", new Unlock[0], context, Unlock[].class);
            for (Unlock<?> unlock : unlocks) {
                builder.addUnlock(unlock.getType(), unlock);
            }
            Parser experienceScaling = new Parser(GsonHelper.getAsString(object, "experienceSclEquation"));
            Parser incomeScaling = new Parser(GsonHelper.getAsString(object, "incomeSclEquation"));
            builder.setExperienceScalingEquation(experienceScaling);
            builder.setIncomeScalingEquation(incomeScaling);
            return builder;
        }

        @Override
        public JsonElement serialize(Profession src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", RegistryConstants.PROFESSION_SERIALIZER.getKey(src.getSerializer()).toString());
            object.addProperty("color", src.color.serialize());
            object.addProperty("descriptionColor", src.descriptionColor.serialize());
            JsonArray array = new JsonArray();
            for (String s : src.description) {
                array.add(s);
            }
            object.add("description", array);
            object.addProperty("displayName", src.displayName);
            object.addProperty("maxLevel", src.maxLevel);
            object.addProperty("experienceSclEquation", src.experienceScalingEquation.getExpression());
            object.addProperty("incomeSclEquation", src.incomeScalingEquation.getExpression());
            JsonArray actionArray = new JsonArray();
            for (Collection<Action> value : src.actions.values()) {
                for (Action action : value) {
                    actionArray.add(context.serialize(action));
                }
            }
            object.add("actions", actionArray);
            JsonArray unlockArray = new JsonArray();
            for (Collection<Unlock<?>> value : src.unlocks.values()) {
                for (Unlock<?> unlock : value) {
                    unlockArray.add(context.serialize(unlock));
                }
            }
            object.add("unlocks", unlockArray);
            return object;
        }

        @Override
        public Profession fromServer(FriendlyByteBuf buffer) {
            ResourceLocation location = buffer.readResourceLocation();
            TextColor color = TextColor.parseColor(buffer.readUtf());
            TextColor descColor = TextColor.parseColor(buffer.readUtf());
            String displayName = buffer.readUtf();
            String[] description = new String[buffer.readVarInt()];
            for (int i = 0; i < description.length; i++) {
                description[i] = buffer.readUtf();
            }
            Map<UnlockType<?>, Collection<Unlock<?>>> map = new HashMap<>();
            if (buffer.readBoolean()) {
                map = buffer.readMap(buf -> {
                    return RegistryConstants.UNLOCKS.get(buf.readResourceLocation());
                }, buf -> {
                    int size = buf.readVarInt();
                    Collection<Unlock<?>> unlocks = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        UnlockSerializer<?> serializer = RegistryConstants.UNLOCK_TYPE.get(buf.readResourceLocation());
                        if (serializer != null) {
                            unlocks.add(serializer.fromNetwork(buf));
                        }
                    }
                    return unlocks;
                });
            }
            Profession profession = new Profession(color, descColor, description, displayName, -1, ImmutableMap.of(), ImmutableMap.copyOf(map), null, null);
            profession.setKey(location);
            return profession;
        }

        @Override
        public void toClient(FriendlyByteBuf buf, Profession profession, boolean sendUnlocks) {
            buf.writeResourceLocation(profession.key);
            buf.writeUtf(profession.color.serialize());
            buf.writeUtf(profession.descriptionColor.serialize());
            buf.writeUtf(profession.displayName);
            buf.writeVarInt(profession.description.length);
            for (String s : profession.description) {
                buf.writeUtf(s);
            }
            buf.writeBoolean(sendUnlocks);
            if (sendUnlocks) {
                buf.writeMap(profession.getUnlocks(), (buf1, unlockType) -> {
                    buf.writeResourceLocation(RegistryConstants.UNLOCKS.getKey(unlockType));
                }, (buf1, unlocks1) -> {
                    buf.writeVarInt(unlocks1.size());
                    for (Unlock unlock : unlocks1) {
                        buf.writeResourceLocation(RegistryConstants.UNLOCK_TYPE.getKey(unlock.getSerializer()));
                        unlock.getSerializer().toNetwork(buf, unlock); // todo: figure out generics
                    }
                });
            }
        }

        @Override
        public Class<ProfessionBuilder> getBuilderType() {
            return ProfessionBuilder.class;
        }

        @Override
        public Class<Profession> getType() {
            return Profession.class;
        }
    }
}
