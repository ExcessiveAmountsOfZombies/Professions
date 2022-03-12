package com.epherical.professions.profession;

import com.epherical.professions.profession.action.ActionType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public record ProfessionParameter<T>(ResourceLocation name) {
    public static final ProfessionParameter<Player> THIS_PLAYER = create("this_player");
    public static final ProfessionParameter<BlockState> THIS_BLOCKSTATE = create("this_blockstate");
    public static final ProfessionParameter<BlockPos> BLOCKPOS = create("blockpos");
    public static final ProfessionParameter<ItemStack> TOOL = create("tool_used");
    public static final ProfessionParameter<ActionType> ACTION_TYPE = create("action");
    // tODO: add parameter for a player that has professions


    public static <T> ProfessionParameter<T> create(String id) {
        return new ProfessionParameter<>(new ResourceLocation("professions", id));
    }
}