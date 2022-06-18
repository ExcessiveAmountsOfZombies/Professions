package com.epherical.professions.util;

import com.epherical.professions.profession.action.Action;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActionDisplay {

    private final Component header;
    private final Collection<Component> actionInformation;

    public ActionDisplay(Component header, Collection<Action> actions) {
        this.header = header;
        actionInformation = new ArrayList<>();
        for (Action action : actions) {
            actionInformation.addAll(action.clientFriendlyInformation());
        }
    }

    public ActionDisplay(Component header, List<Component> actions) {
        // dumb way to avoid erasure
        this.header = header;
        this.actionInformation = actions;
    }

    public Component getHeader() {
        return header;
    }

    public Collection<Component> getActionInformation() {
        return actionInformation;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(actionInformation.size() + 1);
        buf.writeComponent(header);
        actionInformation.forEach(buf::writeComponent);
    }

    public static ActionDisplay fromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt() - 1;
        Component header = buf.readComponent();
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            components.add(buf.readComponent());
        }
        return new ActionDisplay(header, components);
    }

}