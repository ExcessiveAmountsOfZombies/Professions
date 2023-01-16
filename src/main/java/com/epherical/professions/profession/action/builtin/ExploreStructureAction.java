package com.epherical.professions.profession.action.builtin;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExploreStructureAction extends AbstractAction<ConfiguredStructureFeature<?, ?>> {
    protected final List<ActionEntry<ConfiguredStructureFeature<?, ?>>> entries;
    @Nullable
    protected Set<ConfiguredStructureFeature<?, ?>> realEntries;

    protected ExploreStructureAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<ConfiguredStructureFeature<?, ?>>> entries) {
        super(conditions, rewards);
        this.entries = entries;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        ConfiguredStructureFeature<?, ?> struct = professionContext.getParameter(ProfessionParameter.CONFIGURED_STRUCTURE);
        RegistryAccess access = professionContext.level().registryAccess();
        Registry<ConfiguredStructureFeature<?, ?>> registry = access.ownedRegistryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        String key = registry.getKey(struct).toString();
        logAction(professionContext, Component.nullToEmpty(key));
        return getRealFeatures(registry).contains(struct);
    }

    @Override
    public boolean internalCondition(ProfessionContext context) {
        return true;
    }

    @Override
    public ActionType getType() {
        return Actions.EXPLORE_STRUCT;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        Registry<ConfiguredStructureFeature<?, ?>> registry = ProfessionPlatform.platform.server().registryAccess().ownedRegistryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        for (ConfiguredStructureFeature<?, ?> feature : getRealFeatures(registry)) {
            ResourceLocation key = registry.getKey(feature);
            if (key != null) {
                components.add(new TextComponent(key.toString()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))
                        .append(ProfessionPlatform.platform.displayInformation(this, map)));
            }
        }
        return components;
    }

    @Override
    public List<ActionDisplay.Icon> clientFriendlyInformation(Component actionType) {
        List<ActionDisplay.Icon> comps = new ArrayList<>();
        Registry<ConfiguredStructureFeature<?, ?>> registry = ProfessionPlatform.platform.server().registryAccess().ownedRegistryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        for (ConfiguredStructureFeature<?, ?> feature : getRealFeatures(registry)) {
            ResourceLocation key = registry.getKey(feature);
            if (key != null) {
                ActionDisplay.Icon icon = new ActionDisplay.Icon(Items.CRACKED_STONE_BRICKS, new TextComponent(key.toString())
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)), allRewardInformation(), actionType);
                comps.add(icon);
            }
        }
        return comps;
    }

    @Override
    public List<Singular<ConfiguredStructureFeature<?, ?>>> convertToSingle(Profession profession) {
        List<Action.Singular<ConfiguredStructureFeature<?, ?>>> list = new ArrayList<>();
        Registry<ConfiguredStructureFeature<?, ?>> registry = ProfessionPlatform.platform.server().registryAccess().ownedRegistryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        for (ConfiguredStructureFeature<?, ?> items : getRealFeatures(registry)) {
            list.add(new ExploreStructureAction.Single(items, profession));
        }
        return list;
    }

    protected Set<ConfiguredStructureFeature<?, ?>> getRealFeatures(Registry<ConfiguredStructureFeature<?, ?>> features) {
        if (realEntries == null) {
            realEntries = new HashSet<>();
            for (ActionEntry<ConfiguredStructureFeature<?, ?>> entry : entries) {
                realEntries.addAll(entry.getActionValues(features));
            }
        }
        return realEntries;
    }

    public static Builder explore() {
        return new Builder();
    }

    public static class Builder extends AbstractAction.Builder<Builder> {
        protected final List<ActionEntry<ConfiguredStructureFeature<?, ?>>> features = new ArrayList<>();

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new ExploreStructureAction(this.getConditions(), this.getRewards(), this.features);
        }

        @SafeVarargs
        public final Builder feature(ResourceKey<ConfiguredStructureFeature<?, ?>>... biome) {
            this.features.add(ActionEntry.of(biome));
            return this;
        }

        public Builder feature(ConfiguredStructureFeature<?, ?>... biome) {
            this.features.add(ActionEntry.of(biome));
            return this;
        }

    }

    public static class Serializer extends ActionSerializer<ExploreStructureAction> {

        @Override
        public void serialize(@NotNull JsonObject json, ExploreStructureAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (ActionEntry<ConfiguredStructureFeature<?, ?>> entry : value.entries) {
                // I'm not under the impression that this works, but structures aren't generally loaded until later anyways,
                // only ever leaving us with potential 'keys' so our ActionEntries are unlikely to ever be of the "SingleEntry" type.
                array.addAll(entry.serialize(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE));
            }
            json.add("structures", array);
        }

        @Override
        public ExploreStructureAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "structures");
            List<ActionEntry<ConfiguredStructureFeature<?, ?>>> structs = new ArrayList<>();
            for (JsonElement element : array) {
                String id = element.getAsString();
                // i'm not actually sure that anyone would tag these, or if they can even be tagged, but might as well.
                if (id.startsWith("#")) {
                    TagKey<ConfiguredStructureFeature<?, ?>> key = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(id.substring(1)));
                    structs.add(ActionEntry.of(key));
                } else {
                    structs.add(ActionEntry.of(ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(id))));
                }
            }
            return new ExploreStructureAction(conditions, rewards, structs);
        }
    }

    public class Single extends AbstractSingle<ConfiguredStructureFeature<?, ?>> {

        public Single(ConfiguredStructureFeature<?, ?> value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return ExploreStructureAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return new TranslatableComponent(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return ExploreStructureAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            ExploreStructureAction.this.giveRewards(context, occupation);
        }
    }
}
