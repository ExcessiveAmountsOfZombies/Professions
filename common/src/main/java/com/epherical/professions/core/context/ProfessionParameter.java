package com.epherical.professions.core.context;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.gating.GateType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;

public record ProfessionParameter<T>(ResourceLocation name) {
    public static final ProfessionParameter<IProfessionalPlayer> THIS_PLAYER = of("player");
    public static final ProfessionParameter<Holder<?>> THIS_HOLDER = of("holder");
    public static final ProfessionParameter<BlockState> THIS_BLOCK_STATE = of("block_state");
    public static final ProfessionParameter<BlockPos> BLOCKPOS = of("blockpos");
    public static final ProfessionParameter<ItemStack> TOOL = of("tool_used");
    public static final ProfessionParameter<ActionType> ACTION_TYPE = of("action");
    public static final ProfessionParameter<GateType> GATE_TYPE = of("gate");
    public static final ProfessionParameter<Entity> ENTITY = of("entity");
    public static final ProfessionParameter<ItemStack> ITEM_INVOLVED = of("item_involved");
    public static final ProfessionParameter<Recipe<?>> RECIPE_CRAFTED = of("recipe");
    public static final ProfessionParameter<Holder<Biome>> BIOME = of("biome");
    public static final ProfessionParameter<Structure> CONFIGURED_STRUCTURE = of("configured_structure");
    public static final ProfessionParameter<EnchantmentInstance> ENCHANTMENT_INSTANCE = of("enchantment_instance");



    public static <T> ProfessionParameter<T> of(String name) {
        return new ProfessionParameter<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, name));
    }
}
