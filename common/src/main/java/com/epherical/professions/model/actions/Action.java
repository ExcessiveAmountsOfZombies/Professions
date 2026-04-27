package com.epherical.professions.model.actions;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.bootstrap.platform.Services;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.actions.conditions.Condition;
import com.epherical.professions.model.actions.rewards.Reward;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Action<T> implements Predicate<ProfessionContext> {

    private static final Codec<Holder<Profession>> PROFESSION_CODEC = RegistryFixedCodec.create(ProfessionsCommon.PROFESSION_REGISTRY_KEY)
            .mapResult(new Codec.ResultFunction<>() {
                @Override
                public <E> DataResult<Pair<Holder<Profession>, E>> apply(DynamicOps<E> ops, E input, DataResult<Pair<Holder<Profession>, E>> result) {
                    return result.mapError(Action::explainProfessionDecodeError);
                }

                @Override
                public <E> DataResult<E> coApply(DynamicOps<E> ops, Holder<Profession> input, DataResult<E> result) {
                    return result.mapError(Action::explainProfessionDecodeError);
                }
            });

    public static final Codec<Action<?>> TYPED_CODEC = Services.PLATFORM.getActionTypeRegistry().byNameCodec().dispatch(
            "action", Action::getType, ActionType::codec);

    public static final MapCodec<Common> COMMON = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    PROFESSION_CODEC.fieldOf("profession").forGetter(Common::profession),
                    Condition.CODEC.listOf().fieldOf("conditions").forGetter(Common::conditions),
                    Reward.CODEC.listOf().fieldOf("rewards").forGetter(Common::rewards)
            ).apply(i, Common::new)
    );

    protected List<Either<TagKey<T>, ResourceKey<T>>> values;

    private final Holder<Profession> profession;
    private final List<Condition> conditions;
    private final List<Reward<?>> rewards;
    private final Predicate<ProfessionContext> predicate;

    protected Action(Common common, List<Either<TagKey<T>, ResourceKey<T>>> targets) {
        this(common.profession, common.conditions, common.rewards, targets);
    }

    protected Action(Holder<Profession> profession, List<Condition> conditions, List<Reward<?>> rewards, List<Either<TagKey<T>, ResourceKey<T>>> targets) {
        this.profession = profession;
        this.conditions = conditions;
        this.rewards = rewards;
        this.values = targets;
        this.predicate = Actions.andAllConditions(new ArrayList<>(conditions));
    }

    public boolean isValidAction(ProfessionContext context) {
        return predicate.test(context) && test(context);
    }

    public abstract Common buildCommon();

    public abstract ActionType getType();

    public abstract ResourceKey<? extends Registry<T>> getRegistryKey();

    public Item getIcon() {
        return Items.STONE;
    }

    public Holder<Profession> getProfession() {
        return profession;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Reward<?>> getRewards() {
        return rewards;
    }

    public List<Either<TagKey<T>, ResourceKey<T>>> getValues() {
        return values;
    }

    public record Common(Holder<Profession> profession, List<Condition> conditions, List<Reward<?>> rewards) {
        public static <A extends Action<?>> Common build(A t) {
            return new Common(t.getProfession(), t.getConditions(), t.getRewards());
        }
    }

    public abstract static class Builder<B extends Builder<B, E>, E> {
        private final List<Condition> conditions = new ArrayList<>();
        private final List<Reward<?>> rewards = new ArrayList<>();
        private final List<Either<TagKey<E>, ResourceKey<E>>> targets = new ArrayList<>();
        private final Holder<Profession> profession;

        public Builder(Holder<Profession> profession) {
            this.profession = profession;
        }

        public B condition(Condition.Builder condition) {
            this.conditions.add(condition.build());
            return instance();
        }

        public B reward(Reward.Builder reward) {
            this.rewards.add(reward.build());
            return instance();
        }

        public B target(Either<TagKey<E>, ResourceKey<E>> target) {
            this.targets.add(target);
            return instance();
        }

        public B target(TagKey<E> target) {
            this.targets.add(Either.left(target));
            return instance();
        }

        public B target(ResourceKey<E> target) {
            this.targets.add(Either.right(target));
            return instance();
        }

        protected abstract B instance();

        public List<Condition> getConditions() {
            return conditions;
        }

        public List<Reward<?>> getRewards() {
            return rewards;
        }

        public Holder<Profession> getProfession() {
            return profession;
        }


        public List<Either<TagKey<E>, ResourceKey<E>>> getTargets() {
            return targets;
        }

        public abstract Action<E> build();
    }

    public static <T> Codec<List<Either<TagKey<T>, ResourceKey<T>>>> tagOrElementListCodec(ResourceKey<Registry<T>> registryKey) {
        Codec<Either<TagKey<T>, ResourceKey<T>>> single =
                ExtraCodecs.TAG_OR_ELEMENT_ID.xmap(
                        rl -> rl.tag()
                                ? Either.left(TagKey.create(registryKey, rl.id()))
                                : Either.right(ResourceKey.create(registryKey, rl.id())),
                        e -> e.map(
                                tk -> new ExtraCodecs.TagOrElementLocation(tk.location(), true),
                                rk -> new ExtraCodecs.TagOrElementLocation(rk.location(), false)
                        )
                );
        return single.listOf();
    }

    private static String explainProfessionDecodeError(String error) {
        return error + ". Failed to decode action field 'profession'; the referenced profession is probably missing, disabled, or not loaded yet.";
    }

}
