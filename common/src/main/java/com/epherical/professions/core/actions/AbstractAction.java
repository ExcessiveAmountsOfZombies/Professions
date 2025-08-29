package com.epherical.professions.core.actions;

import com.epherical.professions.CommonClass;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.conditions.Condition;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.core.register.Actions;
import com.epherical.professions.core.rewards.Reward;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractAction implements Action {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Holder<Profession> profession;
    private final List<Condition> conditions;
    private final List<Reward> rewards;
    private final Predicate<ProfessionContext> predicate;


    public AbstractAction(Common common) {
        this(common.profession, common.conditions, common.rewards);
    }


    public AbstractAction(Holder<Profession> profession, List<Condition> conditions, List<Reward> rewards) {
        this.profession = profession;
        this.conditions = conditions;
        this.rewards = rewards;
        this.predicate = Actions.andAllConditions(new ArrayList<>(conditions));
    }

    public static final MapCodec<Common> COMMON = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    RegistryFixedCodec.create(CommonClass.PROFESSION_REGISTRY_KEY).fieldOf("occupation").forGetter(Common::profession),
                    Condition.CODEC.listOf().fieldOf("conditions").forGetter(Common::conditions),
                    Reward.CODEC.listOf().fieldOf("rewards").forGetter(Common::rewards)
            ).apply(i, Common::new)
    );

    @Override
    public void handleAction(ProfessionContext context) {
        // At this point we have already established that this is something we want to apply an action to.
        IProfessionalPlayer parameter = context.getParameter(ProfessionParameter.THIS_PLAYER);
        Occupation occupation = parameter.getOccupation(profession);
        if (occupation != null && test(context) && predicate.test(context)) {
            handleAction(context, occupation);
            giveRewards(context, occupation);
        }
    }

    public abstract void handleAction(ProfessionContext context, Occupation occupation);

    public abstract Common buildCommon();


    private void giveRewards(ProfessionContext context, Occupation occupation) {
        for (Reward reward : rewards) {
            reward.giveReward(context, occupation, this);
        }
    }


    public Holder<Profession> getProfession() {
        return profession;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public record Common(Holder<Profession> profession, List<Condition> conditions, List<Reward>   rewards) {
        public static <T extends AbstractAction> Common build(T t) {
            return new Common(t.getProfession(), t.getConditions(), t.getRewards());
        }
    }

    public abstract static class Builder<T extends Builder<T>> implements Action.Builder {
        private final List<Condition> conditions = new ArrayList<>();
        private final List<Reward> rewards = new ArrayList<>();
        private final Holder<Profession> profession;


        public Builder(Holder<Profession> profession) {
            this.profession = profession;
        }

        public T condition(Condition.Builder condition) {
            this.conditions.add(condition.build());
            return instance();
        }

        public T reward(Reward.Builder reward) {
            this.rewards.add(reward.build());
            return instance();
        }

        protected abstract T instance();

        public List<Condition> getConditions() {
            return conditions;
        }

        public List<Reward> getRewards() {
            return rewards;
        }

        public Holder<Profession> getProfession() {
            return profession;
        }
    }

}
