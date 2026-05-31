package com.epherical.professions.api.actions;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.bootstrap.Gates;
import com.epherical.professions.bootstrap.platform.Services;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.gating.GateReport;
import com.epherical.professions.model.gating.GateType;
import com.epherical.professions.presentation.model.GateDisplay;
import com.epherical.professions.util.GateReportPredicate;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Gate<T> implements Predicate<ProfessionContext> {

    private static final Codec<Holder<Profession>> PROFESSION_CODEC = RegistryFixedCodec.create(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
            .mapResult(new Codec.ResultFunction<>() {
                @Override
                public <E> DataResult<Pair<Holder<Profession>, E>> apply(DynamicOps<E> ops, E input, DataResult<Pair<Holder<Profession>, E>> result) {
                    return result.mapError(Gate::explainProfessionDecodeError);
                }

                @Override
                public <E> DataResult<E> coApply(DynamicOps<E> ops, Holder<Profession> input, DataResult<E> result) {
                    return result.mapError(Gate::explainProfessionDecodeError);
                }
            });

    public static final Codec<Gate<?>> TYPED_CODEC = Services.PLATFORM.getGateTypeRegistry().byNameCodec().dispatch(
            "gate", Gate::getGateType, GateType::codec);

    public static final MapCodec<Common> COMMON = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    PROFESSION_CODEC.fieldOf("profession").forGetter(Common::profession),
                    GateRequirement.CODEC.listOf().optionalFieldOf("requirements", List.of()).forGetter(Common::requirements)
            ).apply(i, Common::new)
    );

    protected final List<Either<TagKey<T>, ResourceKey<T>>> values;
    private final Holder<Profession> profession;
    private final List<GateRequirement> requirements;
    private @Nullable ResourceLocation fileId;
    @Deprecated(since = "eh, probably don't use this for anything anymore, i'll leave it in case i find a use for it though.")
    private final BiPredicate<ProfessionContext, Occupation> predicate;
    private final GateReportPredicate<ProfessionContext, Occupation> gatePredicate;

    protected Gate(Common common, List<Either<TagKey<T>, ResourceKey<T>>> values) {
        this(common.profession, common.requirements, values);
    }

    protected Gate(Holder<Profession> profession, List<GateRequirement> requirements, List<Either<TagKey<T>, ResourceKey<T>>> values) {
        this.profession = profession;
        this.requirements = requirements;
        this.values = values;
        this.predicate = Gates.andAllConditions(new ArrayList<>(requirements));
        this.gatePredicate = Gates.andAllGateConditions(new ArrayList<>(requirements));
    }

    public abstract Common buildCommon();

    public abstract GateType getGateType();

    public abstract ResourceKey<? extends Registry<T>> getRegistryKey();

    public abstract List<GateDisplay<T>> getDisplays(@Nullable RegistryAccess registryAccess);

    public Holder<Profession> getProfession() {
        return profession;
    }

    public List<GateRequirement> getRequirements() {
        return requirements;
    }


    public GateReportPredicate<ProfessionContext, Occupation> getGatePredicate() {
        return gatePredicate;
    }

    public List<Either<TagKey<T>, ResourceKey<T>>> getValues() {
        return values;
    }

    public boolean meetsRequirements(Occupation occupation, ProfessionContext context) {
        if (!test(context)) return true; // I think this is correct. If the thing being tested is not in the map, it's not locked.
        return predicate.test(context, occupation); // if it is, then we need to see if we've met the requirements to use it.
    }

    /**
     * This method is for building a report of failures that can be sent to the player that will contain all the requirements that they
     *  did not meet.
     * @param occupation The occupation involved in the requirement check
     * @param context the context involved
     * @param gateReport A way to build out a report to let the player know which requirements they failed for this gate.
     */
    public void meetsRequirements(Occupation occupation, ProfessionContext context, GateReport gateReport) {
        if (!test(context)) return; // It's in the map, so we'll continue to evaluate.
        gatePredicate.test(context, occupation, gateReport, this);
    }

    public void getExtraValues(Multimap<Holder<?>, Gate<?>> valueToGatesMap, Multimap<Gate<?>, Holder<?>> gateToValueMap, HolderLookup.Provider provider) {}

    public @Nullable ResourceLocation getId() {
        return fileId;
    }

    public void setId(ResourceLocation fileId) {
        if (this.fileId != null && !this.fileId.equals(fileId)) {
            throw new IllegalStateException("Gate file id already set to " + this.fileId + ", cannot reset to " + fileId);
        }
        this.fileId = fileId;
    }

    public record Common(Holder<Profession> profession, List<GateRequirement> requirements) {
        public static Common build(Gate<?> gate) {
            return new Common(gate.getProfession(), gate.getRequirements());
        }
    }

    public abstract static class Builder<B extends Builder<B, E>, E> {
        private final Holder<Profession> profession;
        private final List<GateRequirement> requirements = new ArrayList<>();
        private final List<Either<TagKey<E>, ResourceKey<E>>> targets = new ArrayList<>();

        protected Builder(Holder<Profession> profession) {
            this.profession = profession;
        }

        public B target(Either<TagKey<E>, ResourceKey<E>> target) {
            targets.add(target);
            return instance();
        }

        public B target(TagKey<E> target) {
            targets.add(Either.left(target));
            return instance();
        }

        public B target(ResourceKey<E> target) {
            targets.add(Either.right(target));
            return instance();
        }

        public B requirement(GateRequirement requirement) {
            requirements.add(requirement);
            return instance();
        }

        public B requirement(GateRequirement.Builder requirement) {
            requirements.add(requirement.build());
            return instance();
        }

        public Holder<Profession> getProfession() {
            return profession;
        }

        public List<GateRequirement> getRequirements() {
            return requirements;
        }

        public List<Either<TagKey<E>, ResourceKey<E>>> getTargets() {
            return targets;
        }

        protected abstract B instance();

        public abstract Gate<E> build();
    }

    public static <T> Codec<List<Either<TagKey<T>, ResourceKey<T>>>> tagOrElementListCodec(ResourceKey<Registry<T>> registryKey) {
        Codec<Either<TagKey<T>, ResourceKey<T>>> single = ExtraCodecs.TAG_OR_ELEMENT_ID.xmap(
                rl -> rl.tag()
                        ? Either.left(TagKey.create(registryKey, rl.id()))
                        : Either.right(ResourceKey.create(registryKey, rl.id())),
                either -> either.map(
                        tagKey -> new ExtraCodecs.TagOrElementLocation(tagKey.location(), true),
                        resourceKey -> new ExtraCodecs.TagOrElementLocation(resourceKey.location(), false)
                )
        );
        return single.listOf();
    }

    private static String explainProfessionDecodeError(String error) {
        return error + ". Failed to decode gate field 'profession'; the referenced profession is probably missing, disabled, or not loaded yet.";
    }
}
