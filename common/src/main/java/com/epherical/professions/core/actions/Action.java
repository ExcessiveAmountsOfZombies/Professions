package com.epherical.professions.core.actions;

import com.epherical.professions.platform.Services;
import com.mojang.serialization.Codec;

public interface Action {

    Codec<Action> TYPED_CODEC = Services.PLATFORM.getActionTypeRegistry().byNameCodec().dispatch(
            "action", Action::getType, ActionType::codec);

    ActionType getType();


    @FunctionalInterface
    interface Builder {

        Action build();
    }

}
