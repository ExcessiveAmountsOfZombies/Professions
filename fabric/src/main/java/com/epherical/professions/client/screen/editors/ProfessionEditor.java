package com.epherical.professions.client.screen.editors;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.client.screen.entry.ArrayEntry;
import com.epherical.professions.client.screen.entry.CompoundAwareEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.MultipleTypeEntry;
import com.epherical.professions.client.screen.entry.NumberEntry;
import com.epherical.professions.client.screen.entry.RegistryEntry;
import com.epherical.professions.client.screen.entry.StringEntry;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Actions;
import com.google.gson.JsonArray;
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
    /*private final ArrayEntry<CompoundEntry> unlocks;*/


    public ProfessionEditor(int embed, int width) {
        professionType = new RegistryEntry<>(0, 0, 128, RegistryConstants.PROFESSION_SERIALIZER, ProfessionSerializer.DEFAULT_PROFESSION, Optional.of("type"));
        professionColor = new StringEntry(width / 2, 0, 128, "Color:", "#FFFFFF");
        descriptionColor = new StringEntry(width / 2, 0, 128, "Description Color:", "#FFFFFF");
        displayName = new StringEntry(width / 2, 0, 128, "Display Name:", "Occupation Name");
        expScaling = new StringEntry(width / 2, 0, 128, "Exp Scaling:", "1000*1.064^(lvl-1)");
        maxLevel = new NumberEntry<>(width / 2, 0, 128, "Max Level:", 100);
        description = new ArrayEntry<>(0, 0, 128, "Description", (x, y) -> {
            return new StringEntry(embed + 8, y, width - 8, "description", "", DatapackEntry.Type.REMOVE);
        });
        /*actions = new ArrayEntry<>(0, 0, 128, "Actions", (x, y) -> {
            MultipleTypeEntry entry = new MultipleTypeEntry(embed + 8, y, 90,
                    new CompoundEntry(embed + 8, y, 90, List.of(
                            new RegistryEntry<>(embed + 14, y, width - 14, RegistryConstants.ACTION_TYPE, Actions.PLACE_BLOCK, Optional.of("action")),
                            new ArrayEntry<>(embed + 14, y, width - 14, "items", (x1, y2) -> {
                                return new StringEntry(embed + 18, y2, width - 18, "items", "#minecraft:crops", Optional.of("items"));
                            }),
                            new ArrayEntry<>(embed + 14, y, width - 14, "rewards", (x1, y2) -> {
                                return new CompoundEntry(0, 0, 0,
                                        List.of(new NumberEntry<>(embed + 18, y2, width - 18, "amount", 1.0),
                                                new RegistryEntry<>(embed + 18, y2, width - 18, RegistryConstants.REWARDS, Rewards.EXPERIENCE_REWARD, Optional.of("reward"))));
                            }),
                            new ArrayEntry<>(embed + 14, y, width - 14, "conditions", (x1, y2) -> {
                                return new RegistryEntry<>(embed + 18, y2, width - 18, RegistryConstants.ACTION_CONDITION_TYPE, ActionConditions.FULLY_GROWN_CROP_CONDITION);
                            }))));
            return entry;
        });*/
        actions = new ArrayEntry<>(0, 0, 128, "Actions", (x, y) -> {
            MultipleTypeEntry entry = new MultipleTypeEntry(embed + 8, y, 90, new DatapackEntry[]{
                    new CompoundAwareEntry<>(embed + 8, y, 90, embed, width, RegistryConstants.ACTION_TYPE_KEY,
                            new RegistryEntry<>(embed + 14, y, width - 14, RegistryConstants.ACTION_TYPE, Actions.PLACE_BLOCK, Optional.of("action")))},
                    DatapackEntry.Type.REMOVE);
            return entry;
        });

        /*this.addDatapackWidget(new RegistryEntry<>(ofx, ofy, width, RegistryConstants.PROFESSION_SERIALIZER, ProfessionSerializer.DEFAULT_PROFESSION));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance, width, "Color:", "#FFFFFF"));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance + height, width, "Desc Color:", "#FFFFFF"));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance + (height * 2), width, "Display Name:", "Occupation"));
        this.addDatapackWidget(new StringEntry(ofx, ofy + distance + (height * 3), width, "Exp Scaling:", "1000*1.064^(lvl-1)"));
        //this.addRenderableWidget(new StringEntry(ofx, ofy + distance + (height * 4), width, "Income Scale", "base"));
        this.addDatapackWidget(new NumberEntry<>(ofx, ofy + distance + (height * 4), width, "Max Level:", 100));*/

    }

    @Override
    public List<DatapackEntry> entries() {
        return List.of(professionType, professionColor, descriptionColor, description, displayName, expScaling, maxLevel, actions);
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
        object.add("unlocks", new JsonArray());
    }
}
