package com.epherical.professions;

import com.epherical.professions.networking.CommandButtons;
import com.epherical.professions.networking.NetworkHandler;
import net.minecraft.world.entity.player.Player;

public class ForgePlatform extends CommonPlatform<ForgePlatform> {


    @Override
    public ForgePlatform getPlatform() {
        return this;
    }

    @Override
    public boolean isClientEnvironment() {
        return false;
    }

    @Override
    public boolean isServerEnvironment() {
        return false;
    }

    @Override
    public boolean checkPermission(Player player, String perm, int defIntPerm) {
        return false;
    }

    @Override
    public void sendButtonPacket(CommandButtons buttons) {
        NetworkHandler.Client.sendButtonPacket(buttons);
    }
}
