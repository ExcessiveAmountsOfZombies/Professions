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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExploreBiomeAction extends AbstractAction<Biome> {
    protected final List<ActionEntry<Biome>> entries;
    @Nullable
    protected Set<Biome> realBiomes;


    protected ExploreBiomeAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Biome>> biomes) {
        super(conditions, rewards);
        this.entries = biomes;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        Holder<Biome> biome = professionContext.getPossibleParameter(ProfessionParameter.BIOME);
        logAction(professionContext, biome != null ? Component.nullToEmpty(biome.unwrapKey().get().location().toString()) : Component.nullToEmpty(""));
        return biome != null && getRealBiomes(professionContext.level().registryAccess().registryOrThrow(Registries.BIOME)).contains(biome.value());
    }

    @Override
    public boolean internalCondition(ProfessionContext context) {
        return true;
    }

    @Override
    public ActionType getType() {
        return Actions.EXPLORE_BIOME;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        Registry<Biome> registry = ProfessionPlatform.platform.server().registryAccess().registryOrThrow(Registries.BIOME);
        for (Biome realBiome : getRealBiomes(registry)) {
            ResourceLocation key = registry.getKey(realBiome);
            if (key != null) {
                components.add(Component.literal(key.toString()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))
                        .append(ProfessionPlatform.platform.displayInformation(this, map)));
            }
        }
        return components;
    }

    @Override
    public List<ActionDisplay.Icon> clientFriendlyInformation(Component actionType) {
        List<ActionDisplay.Icon> comps = new ArrayList<>();
        Registry<Biome> biomes = ProfessionPlatform.platform.server().registryAccess().registryOrThrow(Registries.BIOME);
        for (Biome realBiome : getRealBiomes(biomes)) {
            ResourceLocation key = biomes.getKey(realBiome);
            if (key != null) {
                ActionDisplay.Icon icon = new ActionDisplay.Icon(Items.GRASS_BLOCK, Component.literal(key.toString())
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)), allRewardInformation(), actionType);
                comps.add(icon);
            }
        }
        return comps;
    }

    @Override
    public List<Action.Singular<Biome>> convertToSingle(Profession profession) {
        List<Action.Singular<Biome>> list = new ArrayList<>();
        Registry<Biome> biomes = ProfessionPlatform.platform.server().registryAccess().registryOrThrow(Registries.BIOME);
        for (Biome items : getRealBiomes(biomes)) {
            list.add(new ExploreBiomeAction.Single(items, profession));
        }
        return list;
    }

    protected Set<Biome> getRealBiomes(Registry<Biome> biomes) {
        if (realBiomes == null) {
            realBiomes = new HashSet<>();
            for (ActionEntry<Biome> item : entries) {
                realBiomes.addAll(item.getActionValues(biomes));
            }
        }
        return realBiomes;
    }

    @Override
    public void addActionEntry(ActionEntry<Biome> entry) {
        entries.add(entry);
    }

    public List<ActionEntry<Biome>> getEntries() {
        return entries;
    }

    @Override
    public Registry<Biome> getRegistry(MinecraftServer server) {
        return server.registryAccess().registryOrThrow(Registries.BIOME);
    }

    public static Builder explore() {
        return new Builder();
    }

    public static class Builder extends AbstractAction.Builder<Builder> {
        protected final List<ActionEntry<Biome>> biomes = new ArrayList<>();

        @Override
        protected Builder instance() {
            return this;
        }

        @SafeVarargs
        public final Builder biome(ResourceKey<Biome>... biome) {
            this.biomes.add(ActionEntry.of(biome));
            return this;
        }

        public Builder biome(Biome... biome) {
            this.biomes.add(ActionEntry.of(biome));
            return this;
        }

        public Builder biome(TagKey<Biome> biome) {
            this.biomes.add(ActionEntry.of(biome));
            return this;
        }

        @Override
        public Action build() {
            return new ExploreBiomeAction(this.getConditions(), this.getRewards(), this.biomes);
        }
    }


    public static class Serializer extends ActionSerializer<ExploreBiomeAction> {

        @Override
        public void serialize(@NotNull JsonObject json, ExploreBiomeAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (ActionEntry<Biome> entry : value.entries) {

                array.addAll(entry.serialize(ProfessionPlatform.platform.server().registryAccess().registryOrThrow(Registries.BIOME)));
            }
            if (array.size() > 0) {
                json.add("biomes", array);
            }
        }

        @Override
        public ExploreBiomeAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "biomes", new JsonArray());
            List<ActionEntry<Biome>> biomes = new ArrayList<>();
            for (JsonElement element : array) {
                String id = element.getAsString();
                if (id.startsWith("#")) {
                    TagKey<Biome> key = TagKey.create(Registries.BIOME, new ResourceLocation(id.substring(1)));
                    biomes.add(ActionEntry.of(key));
                } else {
                    biomes.add(ActionEntry.of(ResourceKey.create(Registries.BIOME, new ResourceLocation(id))));
                }
            }
            return new ExploreBiomeAction(conditions, rewards, biomes);
        }
    }

    public class Single extends AbstractSingle<Biome> {

        public Single(Biome value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return ExploreBiomeAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return Component.translatable(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return ExploreBiomeAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            ExploreBiomeAction.this.giveRewards(context, occupation);
        }
    }
}
