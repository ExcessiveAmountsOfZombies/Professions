package com.epherical.professions.profession.unlock;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.unlock.builtin.BlockUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface UnlockSerializer<T extends Unlock<?>> {
    UnlockSerializer<BlockUnlock> BLOCK_UNLOCK = register(Constants.modID("block"), new BlockUnlock.NetworkSerializer());
    UnlockSerializer<ToolUnlock> TOOL_UNLOCK = register(Constants.modID("tool"), new ToolUnlock.NetworkSerializer());

    T fromNetwork(FriendlyByteBuf buf);

    void toNetwork(FriendlyByteBuf buf, T unlock);

    static <S extends UnlockSerializer<T>, T extends Unlock<?>> S register(ResourceLocation id, S serializer) {
        return Registry.register(RegistryConstants.UNLOCK_TYPE, id, serializer);

    }

}
