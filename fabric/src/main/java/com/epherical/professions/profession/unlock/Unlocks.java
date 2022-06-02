package com.epherical.professions.profession.unlock;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.unlock.builtin.BlockUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.Constants.modID;

public class Unlocks {
    public static final UnlockType<Block> BLOCK_UNLOCK = register(modID("block"), new BlockUnlock.JsonSerializer());
    public static final UnlockType<Item> TOOL_UNLOCK = register(modID("tool"), new ToolUnlock.JsonSerializer());


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.UNLOCKS, "unlock", "unlock", Unlock::getType).build();
    }

    public static <T> UnlockType<T> register(ResourceLocation id, Serializer<? extends Unlock<T>> serializer) {
        return Registry.register(RegistryConstants.UNLOCKS, id, new UnlockType<>(serializer));
    }
}
