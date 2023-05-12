package com.epherical.professions.mixin;

import com.epherical.professions.ProfessionPlatform;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class MixinReloadableServerResources {

    @Inject(method = "method_42095", at = @At("RETURN"), remap = false)
    private static void professionsMaybeCompleted(ReloadableServerResources $$0x, Object $$1x, Throwable $$2x, CallbackInfo ci) {
        // todo; may need to use target or something for forge.
        ProfessionPlatform.platform.datapackFinishedLoadingEvent();
    }
}
