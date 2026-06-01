package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BredAnimalsTrigger.class)
public class BreedActionMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    private void onBredAnimals(ServerPlayer player, Animal parent, Animal partner, @Nullable AgeableMob child, CallbackInfo ci) {
        if (child == null) {
            return;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = FabricProfessionsMod.ensureProfessionalPlayer(mod, player);
        if (professionalPlayer == null) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(player.level(), Actions.BREED_ACTION, professionalPlayer)
                .addParameter(ProfessionParameter.ENTITY, child);
        mod.getPlayerManager().processAction(player, builder.build());
    }
}
