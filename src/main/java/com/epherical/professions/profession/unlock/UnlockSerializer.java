package com.epherical.professions.profession.unlock;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.unlock.builtin.BlockBreakUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockDropUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface UnlockSerializer<T extends Unlock<?>> {
    UnlockSerializer<BlockDropUnlock> BLOCK_DROP_UNLOCK = register(Constants.modID("block_drop"), new BlockDropUnlock.NetworkSerializer());
    UnlockSerializer<BlockBreakUnlock> BLOCK_BREAK_UNLOCK = register(Constants.modID("block_break"), new BlockBreakUnlock.NetworkSerializer());
    UnlockSerializer<ToolUnlock> TOOL_UNLOCK = register(Constants.modID("tool"), new ToolUnlock.NetworkSerializer());

    T fromNetwork(FriendlyByteBuf buf);

    void toNetwork(FriendlyByteBuf buf, T unlock);

    static <S extends UnlockSerializer<T>, T extends Unlock<?>> S register(ResourceLocation id, S serializer) {
        return Registry.register(RegistryConstants.UNLOCK_TYPE, id, serializer);

    }

}
