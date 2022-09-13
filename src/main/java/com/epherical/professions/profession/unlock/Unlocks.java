package com.epherical.professions.profession.unlock;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.unlock.builtin.AdvancementUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockBreakUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockDropUnlock;
import com.epherical.professions.profession.unlock.builtin.EquipmentUnlock;
import com.epherical.professions.profession.unlock.builtin.InteractionUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.Constants.modID;

public class Unlocks {
    public static final UnlockType<Block> BLOCK_DROP_UNLOCK = register(modID("block_drop"), new BlockDropUnlock.JsonSerializer(), Block.class, "professions.tooltip.unlock.drop_req");
    public static final UnlockType<Block> BLOCK_BREAK_UNLOCK = register(modID("block_break"), new BlockBreakUnlock.JsonSerializer(), Block.class, "professions.tooltip.unlock.break_req");
    public static final UnlockType<Item> TOOL_UNLOCK = register(modID("tool_unlock"), new ToolUnlock.JsonSerializer(), Item.class, "professions.tooltip.unlock.tool_use_req");
    public static final UnlockType<Item> ADVANCEMENT_UNLOCK = register(modID("need_advancement"), new AdvancementUnlock.JsonSerializer(), Item.class, "professions.tooltip.unlock.advancement_req");
    public static final UnlockType<Item> EQUIPMENT_UNLOCK = register(modID("equip_unlock"), new EquipmentUnlock.JsonDeserializer(), Item.class, "professions.tooltip.unlock.equipment_req");
    public static final UnlockType<Item> INTERACTION_UNLOCK = register(modID("interaction_unlock"), new InteractionUnlock.JsonDeserializer(), Item.class, "professions.tooltip.unlock.interaction_req");


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.UNLOCKS, "unlock", "unlock", Unlock::getType).build();
    }

    public static <T> UnlockType<T> register(ResourceLocation id, Serializer<? extends Unlock<T>> serializer, Class<T> clazz, String translation) {
        return Registry.register(RegistryConstants.UNLOCKS, id, new UnlockType<>(serializer, clazz, translation));
    }
}
