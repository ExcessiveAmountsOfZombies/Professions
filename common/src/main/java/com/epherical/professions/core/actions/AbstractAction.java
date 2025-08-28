package com.epherical.professions.core.actions;

import com.epherical.professions.CommonClass;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.conditions.Condition;
import com.epherical.professions.core.rewards.Reward;
import com.epherical.professions.platform.Services;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFixedCodec;
import org.slf4j.Logger;

import java.util.List;

public abstract class AbstractAction implements Action {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Holder<Profession> profession;
    private final List<Condition> conditions;
    private final List<Reward> rewards;


    public AbstractAction(Common common) {
        this(common.profession, common.conditions, common.rewards);
    }


    public AbstractAction(Holder<Profession> profession, List<Condition> conditions, List<Reward> rewards) {
        this.profession = profession;
        this.conditions = conditions;
        this.rewards = rewards;
    }

    public static final MapCodec<Common> COMMON = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    RegistryFixedCodec.create(CommonClass.PROFESSION_REGISTRY_KEY).fieldOf("occupation").forGetter(Common::profession),
                    Condition.CODEC.listOf().fieldOf("conditions").forGetter(Common::conditions),
                    Reward.CODEC.listOf().fieldOf("rewards").forGetter(Common::rewards)
            ).apply(i, Common::new)
    );


    public abstract Common buildCommon();


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
}
