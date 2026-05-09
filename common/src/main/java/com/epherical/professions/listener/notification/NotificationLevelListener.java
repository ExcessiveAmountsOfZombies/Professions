package com.epherical.professions.listener.notification;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.rewards.OccupationLevelEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.Optional;

public class NotificationLevelListener implements EventListener<OccupationLevelEvent> {


    @Override
    public void handle(OccupationLevelEvent event) {
        IProfessionalPlayer player = event.getPlayer();

        if (player.getPlayer() instanceof  ServerPlayer serverPlayer) {
            HolderLookup.RegistryLookup<SoundEvent> soundEventRegistryLookup = serverPlayer.serverLevel().registryAccess().lookupOrThrow(Registries.SOUND_EVENT);
            Optional<Holder.Reference<SoundEvent>> soundEventReference = soundEventRegistryLookup.get(ResourceKey.create(Registries.SOUND_EVENT, event.getOccupation().getProfession().value().settings().levelUpSound()));
            soundEventReference.ifPresent(soundEvent -> {
                serverPlayer.playNotifySound(soundEvent.value(), SoundSource.PLAYERS, 0.5f, 1f);
            });
            serverPlayer.sendSystemMessage(Component.literal(String.format("You have leveled %s from %s to %s",
                    event.getOccupation().getProfession().value().displayNameRaw(), event.getOldLevel(), event.getNewLevel())));
        }
    }
}
