package com.epherical.professions.core.register;

import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.actions.block.BlockBreakAction;


import static com.epherical.professions.CommonClass.ACTION_REGISTRY_KEY;

public class Actions {

    public static final ActionType BLOCK_BREAK = PlatformBootstrap.register(
            ACTION_REGISTRY_KEY, "block_break", new ActionType(BlockBreakAction.CODEC));



    public static void register() {

    }

}
