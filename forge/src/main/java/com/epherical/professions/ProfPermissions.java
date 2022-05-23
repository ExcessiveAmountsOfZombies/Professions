package com.epherical.professions;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.stringtemplate.v4.ST;

import java.util.UUID;

public class ProfPermissions {

    public static final PermissionNode<Boolean> HELP = new PermissionNode<>(Constants.MOD_ID, "command.help", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> JOIN = new PermissionNode<>(Constants.MOD_ID, "command.join", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> LEAVE = new PermissionNode<>(Constants.MOD_ID, "command.leave", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> LEAVE_ALL = new PermissionNode<>(Constants.MOD_ID, "command.leaveall", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> INFO = new PermissionNode<>(Constants.MOD_ID, "command.info", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> STATS = new PermissionNode<>(Constants.MOD_ID, "command.stats", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> BROWSE = new PermissionNode<>(Constants.MOD_ID, "command.browse", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> TOP = new PermissionNode<>(Constants.MOD_ID, "command.top", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);

    public static final PermissionNode<Boolean> RELOAD = new PermissionNode<>(Constants.MOD_ID, "command.reload", PermissionTypes.BOOLEAN, ProfPermissions::defaultOp);
    public static final PermissionNode<Boolean> FIRE = new PermissionNode<>(Constants.MOD_ID, "command.fire", PermissionTypes.BOOLEAN, ProfPermissions::defaultOp);
    public static final PermissionNode<Boolean> FIRE_ALL = new PermissionNode<>(Constants.MOD_ID, "command.fireall", PermissionTypes.BOOLEAN, ProfPermissions::defaultOp);
    public static final PermissionNode<Boolean> EMPLOY = new PermissionNode<>(Constants.MOD_ID, "command.employ", PermissionTypes.BOOLEAN, ProfPermissions::defaultOp);
    public static final PermissionNode<Boolean> SET_LEVEL = new PermissionNode<>(Constants.MOD_ID, "command.setlevel", PermissionTypes.BOOLEAN, ProfPermissions::defaultOp);

    @SubscribeEvent
    public void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(HELP, JOIN, LEAVE, LEAVE_ALL, INFO, STATS, BROWSE, TOP,
                        RELOAD, FIRE, FIRE_ALL, EMPLOY, SET_LEVEL);
    }

    private static Boolean defaultOp(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return player != null && player.hasPermissions(4);
    }

    private static Boolean defaultTrue(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return true;
    }

}
