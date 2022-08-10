package com.epherical.professions.client.entry;

import com.epherical.professions.client.button.SmallIconButton;
import com.epherical.professions.client.screen.CommonDataScreen;
import com.epherical.professions.client.widgets.CommandButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DynamicCompoundEntry<OBJ, T extends DatapackEntry<?, ?>> extends AbstractCompoundEntry<OBJ, DynamicCompoundEntry<OBJ, T>> {

    private final Deserializer<OBJ, DynamicCompoundEntry<OBJ, T>> deserializer;
    private final TriFunction<Integer, Integer, Integer, T> addObject;
    protected List<T> objects = new ArrayList<>();


    protected SmallIconButton addButton;

    protected boolean needsRefresh;

    public DynamicCompoundEntry(int x, int y, int width, Optional<String> key, List<DatapackEntry<OBJ, ?>> entries, Deserializer<OBJ, DynamicCompoundEntry<OBJ, T>> deserializer,
                                TriFunction<Integer, Integer, Integer, T> addObject, Type... types) {
        super(x, y, width, 23, key, entries, types);
        this.deserializer = deserializer;
        this.addObject = addObject;
        this.addButton = new SmallIconButton(x, y + 2, 16, 16, Component.nullToEmpty(""), CommandButton.SmallIcon.ADD, button -> {
            addEntryToBeginning(createEntry());
        });
        children.add(addButton);
    }

    @Override
    public void tick(CommonDataScreen screen) {
        super.tick(screen);
        if (needsRefresh) {
            screen.markScreenDirty();
            needsRefresh = false;
        }
        for (T object : objects) {
            object.tick(screen);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        setButtonPositions(getXScroll(), getYScroll());
    }

    public T createEntry() {
        int indent = 20;
        return addObject.apply(x + indent, this.y + 2, width - (indent * 2));
    }

    public void addEntryToBeginning(T object) {
        objects.add(0, object);
        initEntry(object);
    }

    public void addEntry(T object) {
        objects.add(object);
        initEntry(object);
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        rebuildTinyButtons(screen);
        screen.addChild(addButton);
        screen.addChild(this);
        for (T object : objects) {
            object.onRebuild(screen);
        }
    }

    private void initEntry(T object) {
        object.initPosition(this.x, this.y);
        object.addListener(button1 -> {
            if (button1.getType() == Type.REMOVE) {
                objects.remove(object);
                needsRefresh = true;
            }
        });
        for (AbstractWidget child : object.children) {
            if (child instanceof DatapackEntry entry) {
                entry.addListener(button1 -> {
                    if (button1.getType() == Type.REMOVE) {
                        objects.remove(object);
                        needsRefresh = true;
                    }
                });
            }
        }
        needsRefresh = true;
    }

    @Override
    public void initPosition(int initialX, int initialY) {
        super.initPosition(initialX, initialY);
        setButtonPositions(0, 0);
    }

    @Override
    public void deserialize(OBJ object) {
        deserializer.deserialize(object, this);
    }

    @Override
    public List<? extends AbstractWidget> children() {
        List<AbstractWidget> copy = new ArrayList<>(super.children());
        copy.addAll(objects);
        return copy;
    }

    @Override
    public JsonElement getSerializedValue() {
        JsonObject object = new JsonObject();
        for (T t : objects) {
            Optional<String> serializationKey = t.getSerializationKey();
            if (serializationKey.isPresent()) {
                object.add(serializationKey.get(), t.getSerializedValue());
            } else {
                LOGGER.warn("Unable to serialize value for: {} in DynamicCompoundEntry", t);
            }
        }
        return object;
    }

    @Override
    public String getType() {
        return "Dynamic Compound";
    }

    private void setButtonPositions(int xScroll, int yScroll) {
        int start = width - 25;
        addButton.x = x + start + xScroll;
        addButton.y = y + 2 + yScroll;
    }
}
