package com.epherical.professions.util;

import com.epherical.professions.profession.action.Action;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActionDisplay {

    private final Collection<Icon> actionInformation;

    public ActionDisplay(Component header, Collection<Action<?>> actions) {
        actionInformation = new ArrayList<>();
        for (Action<?> action : actions) {
            actionInformation.addAll(action.clientFriendlyInformation(header));
        }
    }

    public ActionDisplay(Collection<Icon> actions) {
        // dumb way to avoid erasure
        this.actionInformation = actions;
    }

    public Collection<Icon> getActionInformation() {
        return actionInformation;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(actionInformation.size() + 1);
        for (Icon icon : actionInformation) {
            icon.write(buf);
        }
    }

    public static ActionDisplay fromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt() - 1;
        List<Icon> icons = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            icons.add(Icon.read(buf));
        }
        return new ActionDisplay(icons);
    }

    public static class Icon {
        protected final ItemStack representation;
        protected final Component name;
        protected final Component actionInformation;
        protected final Component actionType;

        public Icon(Item representation, Component name, Component actionInformation, Component actionType) {
            this(new ItemStack(representation), name, actionInformation, actionType);
        }

        public Icon(ItemStack representation, Component name, Component actionInformation, Component actionType) {
            this.representation = representation;
            this.name = name;
            this.actionInformation = actionInformation;
            this.actionType = actionType;
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeItem(representation);
            buf.writeComponent(name);
            buf.writeComponent(actionInformation);
            buf.writeComponent(actionType);
        }

        public static Icon read(FriendlyByteBuf buf) {
            ItemStack item = buf.readItem();
            if (item.getItem().equals(Items.AIR)) {
                item = new ItemStack(Items.BARRIER);
            }
            Component name = buf.readComponent();
            Component actionInformation = buf.readComponent();
            Component actionType = buf.readComponent();
            return new Icon(item, name, actionInformation, actionType);
        }

        public Component getName() {
            return name;
        }

        public Component getActionInformation() {
            return actionInformation;
        }

        public Component getActionType() {
            return actionType;
        }

        public ItemStack getRepresentation() {
            return representation;
        }
    }

}
