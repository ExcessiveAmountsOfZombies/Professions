package com.epherical.professions.core.actions;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.rewards.Reward;
import com.epherical.professions.platform.Services;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Predicate;

public interface Action extends Predicate<ProfessionContext> {

    Codec<Action> TYPED_CODEC = Services.PLATFORM.getActionTypeRegistry().byNameCodec().dispatch(
            "action", Action::getType, ActionType::codec);

    ActionType getType();

    default Item getIcon() {
        return Items.STONE;
    }

    List<Reward> getRewards();

    Holder<Profession> getProfession();

    void handleAction(ProfessionContext context);


    @FunctionalInterface
    interface Builder {

        Action build();
    }

}
