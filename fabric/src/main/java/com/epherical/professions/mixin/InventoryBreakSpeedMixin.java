package com.epherical.professions.mixin;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ProfessionUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryBreakSpeedMixin {


    @Shadow @Final public Player player;

    @Shadow @Final public NonNullList<ItemStack> items;

    @Shadow public int selected;

    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    public void professions$modifyDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        ProfessionalPlayer player = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(this.player.getUUID());
        if (player == null) {
            return;
        }
        ItemStack item = this.items.get(this.selected);
        Pair<Unlock.Singular<Item>, Boolean> pair = ProfessionUtil.canUse(player, Unlocks.TOOL_UNLOCK, item.getItem());
        if (!pair.getSecond()) {
            cir.setReturnValue(item.getDestroySpeed(state) / 8);
        }
    }
}
