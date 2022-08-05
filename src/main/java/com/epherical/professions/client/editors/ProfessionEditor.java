package com.epherical.professions.client.editors;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.client.entry.ArrayEntry;
import com.epherical.professions.client.entry.CompoundAwareEntry;
import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.client.entry.MultipleTypeEntry;
import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.RegistryEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.unlock.Unlocks;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Optional;

public class ProfessionEditor extends DatapackEditor {

    private final RegistryEntry<ProfessionSerializer<?, ?>> professionType;
    private final StringEntry professionColor;
    private final StringEntry descriptionColor;
    private final StringEntry displayName;
    private final StringEntry expScaling;
    private final NumberEntry<Integer> maxLevel;
    private final ArrayEntry<MultipleTypeEntry> actions;
    private final ArrayEntry<StringEntry> description;
    private final ArrayEntry<MultipleTypeEntry> unlocks;


    public ProfessionEditor(int embed, int width) {
        this.width = width;
        professionType = new RegistryEntry<>(0, 0, 128, RegistryConstants.PROFESSION_SERIALIZER, ProfessionSerializer.DEFAULT_PROFESSION, Optional.of("type"));
        professionColor = new StringEntry(width / 2, 0, 128, "Color:", "#FFFFFF");
        descriptionColor = new StringEntry(width / 2, 0, 128, "Description Color:", "#FFFFFF");
        displayName = new StringEntry(width / 2, 0, 128, "Display Name:", "Occupation Name");
        expScaling = new StringEntry(width / 2, 0, 128, "Exp Scaling:", "1000*1.064^(lvl-1)");
        maxLevel = new NumberEntry<>(width / 2, 0, 128, "Max Level:", 100);
        description = new ArrayEntry<>(0, 0, 128, "Description", (x, y, wid) -> {
            return new StringEntry(embed + 8, y, width - 8, "description", "", DatapackEntry.Type.REMOVE);
        });
        actions = new ArrayEntry<>(0, 0, 128, "Actions", (x, y, wid) -> {
            return new MultipleTypeEntry(embed + 8, y, 90, new DatapackEntry[]{
                    new CompoundAwareEntry<>(embed + 8, y, 90, embed + 14, this.width - 14, RegistryConstants.ACTION_TYPE_KEY,
                            new RegistryEntry<>(embed + 14, y, this.width - 14, RegistryConstants.ACTION_TYPE, Actions.PLACE_BLOCK, Optional.of("action")))},
                    DatapackEntry.Type.REMOVE);
        });
        unlocks = new ArrayEntry<>(0, 0, 128, "Unlocks", (x, y, wid) -> {
           return new MultipleTypeEntry(embed + 8, y, 90, new DatapackEntry[]{
                   new CompoundAwareEntry<>(embed + 8, y, 90, embed + 14, this.width - 14, RegistryConstants.UNLOCK_KEY,
                           new RegistryEntry<>(embed + 14, y, this.width - 14, RegistryConstants.UNLOCKS, Unlocks.TOOL_UNLOCK, Optional.of("unlock")))},
                   DatapackEntry.Type.REMOVE);
        });

    }

    @Override
    public List<DatapackEntry> entries() {
        return List.of(professionType, professionColor, descriptionColor, description, displayName, expScaling, maxLevel, actions, unlocks);
    }

    @Override
    public void serialize(JsonObject object) {
        object.add("type", professionType.getSerializedValue());
        object.add("actions", actions.getSerializedValue());
        object.add("color", professionColor.getSerializedValue());
        object.add("description", description.getSerializedValue());
        object.add("descriptionColor", descriptionColor.getSerializedValue());
        object.add("displayName", displayName.getSerializedValue());
        object.add("experienceSclEquation", expScaling.getSerializedValue());
        object.addProperty("incomeSclEquation", "base");
        object.add("maxLevel", maxLevel.getSerializedValue());
        object.add("unlocks", unlocks.getSerializedValue());
    }
}
