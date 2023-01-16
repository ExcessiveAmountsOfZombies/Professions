package com.epherical.professions.client.editor;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.client.editors.DatapackEditor;
import com.epherical.professions.client.entry.ArrayEntry;
import com.epherical.professions.client.entry.CompoundAwareEntry;
import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.RegistryEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.epherical.professions.profession.unlock.Unlocks;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProfessionEditor extends DatapackEditor<Profession> {

    private final RegistryEntry<Profession, ProfessionSerializer<?, ?>> professionType;
    private final StringEntry<Profession> professionColor;
    private final StringEntry<Profession> descriptionColor;
    private final StringEntry<Profession> displayName;
    private final StringEntry<Profession> expScaling;
    private final NumberEntry<Integer, Profession> maxLevel;
    private final ArrayEntry<Profession, CompoundAwareEntry<Action, ActionType>> actions;
    private final ArrayEntry<Profession, StringEntry<String>> description;
    private final ArrayEntry<Profession, CompoundAwareEntry<Unlock<?>, UnlockType<?>>> unlocks;


    public ProfessionEditor(int embed, int width) {
        this.width = width;
        professionType = new RegistryEntry<>(0, 0, 128,
                RegistryConstants.PROFESSION_SERIALIZER, ProfessionSerializer.DEFAULT_PROFESSION, Optional.of("type"),
                (profession, entry) -> entry.setValue(profession.getSerializer()));
        professionColor = new StringEntry<>(width / 2, 0, 128, "Color:", "#FFFFFF",
                (profession, entry) -> entry.setValue(profession.getColor().serialize()));
        descriptionColor = new StringEntry<>(width / 2, 0, 128, "Description Color:", "#FFFFFF",
                (profession, entry) -> entry.setValue(profession.getDescriptionColor().serialize()));
        displayName = new StringEntry<>(width / 2, 0, 128, "Display Name:", "Occupation Name",
                (profession, entry) -> entry.setValue(profession.getDisplayName()));
        expScaling = new StringEntry<>(width / 2, 0, 128, "Exp Scaling:", "1000*1.064^(lvl-1)",
                (profession, entry) -> entry.setValue(profession.getExperienceScalingEquation().getExpression()));
        maxLevel = new NumberEntry<>(width / 2, 0, 128, "Max Level:", 100,
                (profession, entry) -> entry.setValue(String.valueOf(profession.getMaxLevel())));
        description = new ArrayEntry<>(0, 0, 128, "Description", (x, y, wid) -> {
            return new StringEntry<>(embed + 8, y, width - 8, "description", "", (s, entry) -> entry.setValue(s), DatapackEntry.Type.REMOVE);
        }, (profession, entry) -> {
            for (String s : profession.getDescription()) {
                StringEntry<String> entry1 = entry.createEntry();
                entry1.deserialize(s);

                entry.addEntry(entry1);
            }
        });


        actions = new ArrayEntry<>(0, 0, 128, "Actions", (x, y, wid) -> {
            return new CompoundAwareEntry<>(embed + 8, y, 90, embed + 14, this.width - 14, RegistryConstants.ACTION_TYPE_KEY,
                    new RegistryEntry<>(embed + 14, y, this.width - 14, RegistryConstants.ACTION_TYPE, Actions.PLACE_BLOCK, Optional.of("action"),
                            (action, entry) -> entry.setValue(action.getType()), DatapackEntry.Type.REMOVE),
                    (action, entry) -> entry.getEntry().deserialize(action));
        }, (profession, entry) -> {
            for (Map.Entry<ActionType, Collection<Action<?>>> entrySet : profession.getActions().entrySet()) {
                for (Action<?> action : entrySet.getValue()) {
                    CompoundAwareEntry<Action, ActionType> entry1 = entry.createEntry();
                    entry1.deserialize(action);
                    entry.addEntry(entry1);
                }
            }
        });

        unlocks = new ArrayEntry<>(0, 0, 128, "Unlocks", (x, y, wid) -> {
            return new CompoundAwareEntry<>(embed + 8, y, 90, embed + 14, this.width - 14, RegistryConstants.UNLOCK_KEY,
                    new RegistryEntry<>(embed + 14, y, this.width - 14, RegistryConstants.UNLOCKS, Unlocks.BLOCK_DROP_UNLOCK, Optional.of("unlock"),
                            (unlock, entry) -> entry.setValue(unlock.getType()), DatapackEntry.Type.REMOVE),
            (unlock, entry) -> entry.getEntry().deserialize(unlock));
        }, (profession, entry) -> {
            for (Map.Entry<UnlockType<?>, Collection<Unlock<?>>> entrySet : profession.getUnlocks().entrySet()) {
                for (Unlock<?> unlock : entrySet.getValue()) {
                    CompoundAwareEntry<Unlock<?>, UnlockType<?>> entry1 = entry.createEntry();
                    entry1.deserialize(unlock);
                    entry.addEntry(entry1);
                }
            }
        });
    }

    @Override
    public List<DatapackEntry<Profession, ?>> entries() {
        return List.of(professionType, professionColor, descriptionColor, description, displayName, expScaling, maxLevel, actions, unlocks);
    }

    @Override
    public Collection<Profession> deserializableObjects() {
        return ProfessionPlatform.platform.getProfessionLoader().getProfessions();
    }

    @Override
    public Collection<Button> deserializableObjectButtons(DatapackEditor<Profession> editor) {
        List<Button> buttons = new ArrayList<>();
        for (Profession profession : ProfessionPlatform.platform.getProfessionLoader().getProfessions()) {
            buttons.add(new Button(0, 0, 0, 20, profession.getDisplayComponent(), button -> {
                editor.deserialize(profession);
                Minecraft.getInstance().setScreen(new DatapackScreen(editor));
            }));
        }
        return buttons;
    }

    @Override
    public void serialize(JsonObject object) {
        object.add("type", professionType.getSerializedValue());//
        object.add("actions", actions.getSerializedValue());
        object.add("color", professionColor.getSerializedValue());//
        object.add("description", description.getSerializedValue());//
        object.add("descriptionColor", descriptionColor.getSerializedValue());//
        object.add("displayName", displayName.getSerializedValue());//
        object.add("experienceSclEquation", expScaling.getSerializedValue());
        object.addProperty("incomeSclEquation", "base");
        object.add("maxLevel", maxLevel.getSerializedValue());
        object.add("unlocks", unlocks.getSerializedValue());
    }

    @Override
    public void deserialize(Profession object) {
        professionType.deserialize(object);
        professionColor.deserialize(object);
        descriptionColor.deserialize(object);
        description.deserialize(object);
        displayName.deserialize(object);
        expScaling.deserialize(object);
        maxLevel.deserialize(object);
        actions.deserialize(object);
        unlocks.deserialize(object);
    }

    @Override
    public String datapackType() {
        return "Occupation";
    }

    @Override
    public String savePath() {
        return "data/%s/professions/occupations/%s";
    }

}
