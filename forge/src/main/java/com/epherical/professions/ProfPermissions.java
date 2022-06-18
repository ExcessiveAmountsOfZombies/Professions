package com.epherical.professions;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.stringtemplate.v4.ST;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class ProfPermissions {

    private static final Map<String, PermissionNode<?>> NODES = new HashMap<>();

    public static final PermissionDynamicContextKey<String> PROFESSION_CONTEXT = new PermissionDynamicContextKey<>(String.class, "profession", Function.identity());

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

    public static final PermissionNode<Boolean> CREATIVE_PAYMENT = new PermissionNode<>(Constants.MOD_ID, "reward.creative", PermissionTypes.BOOLEAN, ProfPermissions::defaultOp);

    public static final PermissionNode<Boolean> BYPASSS_LEAVE_PREVENTION = new PermissionNode<>(Constants.MOD_ID, "bypass.leave_prevention", PermissionTypes.BOOLEAN, ProfPermissions::defaultOp);

    public static final PermissionNode<Boolean> JOIN_PROFESSION = new PermissionNode<>(Constants.MOD_ID, "join", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue);
    public static final PermissionNode<Boolean> START_PROFESSION = new PermissionNode<>(Constants.MOD_ID, "start", PermissionTypes.BOOLEAN, ProfPermissions::defaultTrue, PROFESSION_CONTEXT);


    @SubscribeEvent
    public void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(HELP, JOIN, LEAVE, LEAVE_ALL, INFO, STATS, BROWSE, TOP,
                        RELOAD, FIRE, FIRE_ALL, EMPLOY, SET_LEVEL, CREATIVE_PAYMENT, BYPASSS_LEAVE_PREVENTION,
                START_PROFESSION, JOIN_PROFESSION);
        for (PermissionNode<?> node : event.getNodes()) {
            NODES.put(node.getNodeName(), node);
        }
    }

    private static Boolean defaultOp(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        return player != null && player.hasPermissions(4);
    }

    private static Boolean defaultTrue(ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {

        return true;
    }

    public static <T> PermissionNode<T> getNode(String key, Class<T> type) {
        return (PermissionNode<T>) NODES.get(key);
    }



}
