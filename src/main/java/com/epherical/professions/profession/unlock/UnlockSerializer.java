package com.epherical.professions.profession.unlock;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.unlock.builtin.AdvancementUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockBreakUnlock;
import com.epherical.professions.profession.unlock.builtin.BlockDropUnlock;
import com.epherical.professions.profession.unlock.builtin.EquipmentUnlock;
import com.epherical.professions.profession.unlock.builtin.InteractionUnlock;
import com.epherical.professions.profession.unlock.builtin.ToolUnlock;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface UnlockSerializer<T extends Unlock<?>> {
    UnlockSerializer<BlockDropUnlock> BLOCK_DROP_UNLOCK = register(Constants.modID("block_drop"), new BlockDropUnlock.NetworkSerializer());
    UnlockSerializer<BlockBreakUnlock> BLOCK_BREAK_UNLOCK = register(Constants.modID("block_break"), new BlockBreakUnlock.NetworkSerializer());
    UnlockSerializer<ToolUnlock> TOOL_UNLOCK = register(Constants.modID("tool"), new ToolUnlock.NetworkSerializer());
    UnlockSerializer<AdvancementUnlock> ADVANCEMENT_UNLOCK = register(Constants.modID("advancement"), new AdvancementUnlock.NetworkSerializer());
    UnlockSerializer<EquipmentUnlock> EQUIPMENT_UNLOCK = register(Constants.modID("equipment"), new EquipmentUnlock.NetworkSerializer());
    UnlockSerializer<InteractionUnlock> INTERACTION_UNLOCK = register(Constants.modID("interaction"), new InteractionUnlock.NetworkSerializer());

    T fromNetwork(FriendlyByteBuf buf);

    void toNetwork(FriendlyByteBuf buf, T unlock);

    static <S extends UnlockSerializer<T>, T extends Unlock<?>> S register(ResourceLocation id, S serializer) {
        return Registry.register(RegistryConstants.UNLOCK_TYPE, id, serializer);

    }

}
