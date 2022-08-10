package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CompoundEntry<OBJ> extends AbstractCompoundEntry<OBJ, CompoundEntry<OBJ>> {


    private final Deserializer<OBJ, CompoundEntry<OBJ>> deserializer;

    /**
     * @param entries a list of entries that will comprise the object. Each entry should use the Optional serialization key.
     */
    public CompoundEntry(int x, int y, int width, Optional<String> key, List<DatapackEntry<OBJ, ?>> entries, Deserializer<OBJ, CompoundEntry<OBJ>> deserializer, Type... types) {
        super(x, y, width, key, entries, types);
        this.deserializer = deserializer;
    }

    @Nullable // todo; it may be wise to do something else with this.
    protected EditBox box;

    public CompoundEntry(int x, int y, int width, int height, Optional<String> key, List<DatapackEntry<OBJ, ?>> entries,
                         Deserializer<OBJ, CompoundEntry<OBJ>> deserializer, Type... types) {
        super(x, y, width, height, key, entries, types);
        this.deserializer = deserializer;
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        this.box = new EditBox(font, this.x + 30, y + 8, 250, 22, Component.nullToEmpty(""));
        this.box.setVisible(true);
        this.box.setMaxLength(256);
        this.box.setBordered(false);
        this.box.setValue("");
        this.box.setTextColor(TEXT_COLOR);
        children.add(this.box);
    }

    @Override
    public String getType() {
        return "Compound";
    }

    @Override
    public JsonElement getSerializedValue() {
        return super.getSerializedValue();
    }

    @Override
    public void deserialize(OBJ object) {
        deserializer.deserialize(object, this);
    }

    @Override
    public void tick(CommonDataScreen screen) {
        super.tick(screen);
        if (box != null) {
            box.tick();
            box.setMessage(Component.nullToEmpty(box.getValue()));
        }
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        rebuildTinyButtons(screen);
        if (box != null) {
            screen.addChild(box);
        }
        screen.addChild(this);
        for (AbstractWidget child : children) {
            if (child instanceof DatapackEntry entry) {
                entry.onRebuild(screen);
            } else {
                // compound entries shouldn't have anything in them, they're just containers.
                screen.addChild(child);
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        if (box != null) {
            this.box.x = (this.x + 30);
            this.box.y = y + 8 + getYScroll();
            drawString(poseStack, minecraft.font, ">>>", x + 13 + xScroll, y + 8 + yScroll, 0xFFFFFF);
        }
    }

    @Override
    public Optional<String> getSerializationKey() {
        if (box != null) {
            return Optional.of(box.getValue());
        } else {
            return super.getSerializationKey();
        }
    }

    @Override
    public void setSerializationKey(Optional<String> serializationKey) {
        super.setSerializationKey(serializationKey);
        if (box != null && serializationKey.isPresent()) {
            box.setValue(serializationKey.get());
        }
    }

    public void setValue(String value) {
        if (box != null) {
            box.setValue(value);
        }
    }
}
