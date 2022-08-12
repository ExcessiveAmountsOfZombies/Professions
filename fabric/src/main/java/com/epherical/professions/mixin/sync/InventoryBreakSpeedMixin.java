package com.epherical.professions.mixin.sync;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
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

import java.util.List;
import java.util.Set;

@Mixin(Inventory.class)
public abstract class InventoryBreakSpeedMixin {


    @Shadow
    @Final
    public Player player;

    @Shadow
    @Final
    public NonNullList<ItemStack> items;

    @Shadow
    public int selected;

    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    public void professions$modifyDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        ProfessionalPlayer player = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(this.player.getUUID());
        if (player == null) {
            return;
        }
        ItemStack item = this.items.get(this.selected);
        List<Unlock.Singular<Item>> lockedKnowledge = player.getLockedKnowledge(item.getItem(), Set.of(Unlocks.TOOL_UNLOCK, Unlocks.ADVANCEMENT_UNLOCK));
        for (Unlock.Singular<Item> singular : lockedKnowledge) {
            if (!singular.canUse(player)) {
                cir.setReturnValue(item.getDestroySpeed(state) * 0.0027F);
            }
        }
    }
}
