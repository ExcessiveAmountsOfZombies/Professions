package com.epherical.professions.client.editors;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.client.entry.ArrayEntry;
import com.epherical.professions.client.entry.CompoundAwareEntry;
import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.client.entry.MultipleTypeEntry;
import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.RegistryEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.unlock.Unlock;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProfessionEditor<T extends Profession> extends DatapackEditor<T> {

    private final RegistryEntry<Profession, ProfessionSerializer<?, ?>> professionType;
    private final StringEntry<Profession> professionColor;
    private final StringEntry<Profession> descriptionColor;
    private final StringEntry<Profession> displayName;
    private final StringEntry<Profession> expScaling;
    private final NumberEntry<Integer, Profession> maxLevel;
    private final ArrayEntry<Profession, CompoundAwareEntry<Action, ActionType>> actions;
    private final ArrayEntry<Profession, StringEntry<String>> description;
    private final ArrayEntry<Unlock<?>, MultipleTypeEntry<Unlock<?>>> unlocks;


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
            for (Map.Entry<ActionType, Collection<Action>> entrySet : profession.getActions().entrySet()) {
                for (Action action : entrySet.getValue()) {
                    CompoundAwareEntry<Action, ActionType> entry1 = entry.createEntry();
                    entry1.deserialize(action);
                    entry.addEntry(entry1);
                }
            }
        });

        unlocks = null;

        deserialize(CommonPlatform.platform.getProfessionLoader().getProfession(new ResourceLocation("professions:building")));
    }

    @Override
    public List<DatapackEntry<T, ?>> entries() {
        // silly
        return (List<DatapackEntry<T, ?>>) (List<?>)
                List.of(professionType, professionColor, descriptionColor, description, displayName, expScaling, maxLevel, actions/*, unlocks*/);
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
        //object.add("unlocks", unlocks.getSerializedValue());
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


        /*for (Map.Entry<UnlockType<?>, Collection<Unlock<?>>> entry : object.getUnlocks().entrySet()) {
            for (Unlock<?> unlock : entry.getValue()) {
                unlocks.deserialize(unlock);
            }
        }*/
    }

}
