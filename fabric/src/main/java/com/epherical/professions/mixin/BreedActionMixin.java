package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
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

        IProfessionalPlayer professionalPlayer = FabricProfessionsMod.mod.ensureProfessionalPlayer(player);
        if (professionalPlayer == null) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(player.serverLevel(), Actions.BREED_ACTION, professionalPlayer)
                .addParameter(ProfessionParameter.ENTITY, child);
        ProfessionsCommon.INSTANCE.getPlayerManager().processAction(player, builder.build());
    }
}
