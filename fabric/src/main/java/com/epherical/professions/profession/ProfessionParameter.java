package com.epherical.professions.profession;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.util.ActionLogger;
import com.epherical.professions.util.EnchantmentContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;

public record ProfessionParameter<T>(ResourceLocation name) {
    public static final ProfessionParameter<ProfessionalPlayer> THIS_PLAYER = create("this_player");
    public static final ProfessionParameter<BlockState> THIS_BLOCKSTATE = create("this_blockstate");
    public static final ProfessionParameter<BlockPos> BLOCKPOS = create("blockpos");
    public static final ProfessionParameter<ItemStack> TOOL = create("tool_used");
    public static final ProfessionParameter<ActionType> ACTION_TYPE = create("action");
    public static final ProfessionParameter<Entity> ENTITY = create("entity");
    public static final ProfessionParameter<ItemStack> ITEM_INVOLVED = create("item_involved");
    public static final ProfessionParameter<Recipe<?>> RECIPE_CRAFTED = create("recipe");
    public static final ProfessionParameter<EnchantmentContainer> ENCHANTMENT = create("enchantment");
    public static final ProfessionParameter<ActionLogger> ACTION_LOGGER = create("action_logger");


    public static <T> ProfessionParameter<T> create(String id) {
        return new ProfessionParameter<>(new ResourceLocation("professions", id));
    }
}
