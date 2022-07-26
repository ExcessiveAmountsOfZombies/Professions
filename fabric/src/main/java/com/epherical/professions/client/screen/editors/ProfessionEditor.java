package com.epherical.professions.client.screen.editors;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.client.screen.entry.ArrayEntry;
import com.epherical.professions.client.screen.entry.CompoundEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.MultipleTypeEntry;
import com.epherical.professions.client.screen.entry.NumberEntry;
import com.epherical.professions.client.screen.entry.RegistryEntry;
import com.epherical.professions.client.screen.entry.StringEntry;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonElement;

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
    /*private final ArrayEntry<StringEntry> description;
    private final ArrayEntry<CompoundEntry> unlocks;*/


    public ProfessionEditor(int embed, int width) {
        professionType = new RegistryEntry<>(0, 0, 128, RegistryConstants.PROFESSION_SERIALIZER, ProfessionSerializer.DEFAULT_PROFESSION);
        professionColor = new StringEntry(width / 2, 0, 128, "Color:", "#FFFFFF");
        descriptionColor = new StringEntry(width / 2, 0, 128, "Description Color:", "#FFFFFF");
        displayName = new StringEntry(width / 2, 0, 128, "Display Name:", "Occupation Name");
        expScaling = new StringEntry(width / 2, 0, 128, "Exp Scaling:", "1000*1.064^(lvl-1)");
        maxLevel = new NumberEntry<>(width / 2, 0, 128, "Max Level:", 100);
        actions = new ArrayEntry<>(0, 0, 128, "Actions", (x, y) -> {
            MultipleTypeEntry entry = new MultipleTypeEntry(embed + 8, y, 90, new CompoundEntry(embed + 8, y, 90, List.of(
                    new RegistryEntry<>(embed + 14, y, width - 14, RegistryConstants.ACTION_TYPE, Actions.PLACE_BLOCK),
                    new ArrayEntry<>(embed + 14, y, width - 14, "items", (x1, y2) -> {
                        return new StringEntry(embed + 18, y2, width - 22, "items", "#minecraft:crops", Optional.of("items"));
                    }),
                    new ArrayEntry<>(embed + 14, y, width - 14, "rewards", (x1, y2) -> {
                        return new CompoundEntry(0, 0, 0,
                                List.of(new NumberEntry<>(embed + 18, y2, width - 18, "amount", 1.0),
                                        new RegistryEntry<>(embed + 18, y2, width - 18, RegistryConstants.REWARDS, Rewards.EXPERIENCE_REWARD)));
                    }),
                    new ArrayEntry<>(embed + 14, y, width - 14, "conditions", (x1, y2) -> {
                        return new RegistryEntry<>(embed + 18, y2, width - 22, RegistryConstants.ACTION_CONDITION_TYPE, ActionConditions.FULLY_GROWN_CROP_CONDITION);
                    }))));
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
        return List.of(professionType, professionColor, descriptionColor, displayName, expScaling, maxLevel, actions);
    }

    @Override
    public void serialize(JsonElement object) {

    }
}
