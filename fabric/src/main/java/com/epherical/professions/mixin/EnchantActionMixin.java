package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantedItemTrigger.class)
public class EnchantActionMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    private void onEnchantedItem(ServerPlayer player, ItemStack item, int levelsSpent, CallbackInfo ci) {
        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = FabricProfessionsMod.ensureProfessionalPlayer(mod, player);
        if (professionalPlayer == null) {
            return;
        }

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(item).entrySet()) {
            EnchantmentInstance enchantmentInstance = new EnchantmentInstance(entry.getKey(), entry.getIntValue());
            ProfessionContext.Builder enchantBuilder = ProfessionContext.builder(player.serverLevel(), Actions.ENCHANT_ACTION, professionalPlayer)
                    .addParameter(ProfessionParameter.ENCHANTMENT_INSTANCE, enchantmentInstance);
            mod.getPlayerManager().processAction(player, enchantBuilder.build());
        }

        ProfessionContext.Builder itemBuilder = ProfessionContext.builder(player.serverLevel(), Actions.ENCHANT_ACTION, professionalPlayer)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, item);
        mod.getPlayerManager().processAction(player, itemBuilder.build());
    }
}
