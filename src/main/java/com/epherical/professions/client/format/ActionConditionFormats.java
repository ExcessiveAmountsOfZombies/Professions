package com.epherical.professions.client.format;

import com.epherical.professions.client.entry.CompoundAwareEntry;
import com.epherical.professions.client.entry.CompoundEntry;
import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.client.entry.MultipleTypeEntry;
import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.RegistryEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.mixin.accessor.ItemPredicateAccess;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.profession.conditions.builtin.InvertedCondition;
import com.epherical.professions.profession.conditions.builtin.ToolMatcher;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Optional;
import java.util.Set;

import static com.epherical.professions.RegistryConstants.ACTION_CONDITION_KEY;
import static com.epherical.professions.RegistryConstants.ACTION_CONDITION_TYPE;
import static com.epherical.professions.client.format.FormatRegistry.formatID;
import static com.epherical.professions.client.format.FormatRegistry.register;

public class ActionConditionFormats {

    public static final FormatBuilder<ActionCondition> INVERTED_CONDITION = register(formatID(ACTION_CONDITION_KEY, "inverted"), condition ->
            new RegularFormat<>((embed, y, width) -> {
                return Lists.newArrayList(
                        new CompoundAwareEntry<ActionCondition, ActionConditionType>(embed, y, width, embed + 8, width - 8, ACTION_CONDITION_KEY,
                                new RegistryEntry<>(embed + 8, y, width - 8, ACTION_CONDITION_TYPE, ActionConditions.TOOL_MATCHES, Optional.of("condition"),
                                        (con, entry) -> {
                                            entry.setValue(con.getType());
                                        }, DatapackEntry.Type.REMOVE),
                                (con, entry) -> {
                                    if (con instanceof InvertedCondition inverted) {
                                        for (DatapackEntry<ActionCondition, ?> entryEntry : entry.getEntries()) {
                                            entryEntry.deserialize(inverted.condition());
                                        }
                                        entry.setNewObj(inverted.condition());
                                    }

                                }, Optional.of("term")));
            })
    );

    public static final FormatBuilder<ToolMatcher> TOOL_MATCHER_FORMAT = register(formatID(ACTION_CONDITION_KEY, "tool_matches"), toolMatcher ->
            new RegularFormat<>((embed, y, width) -> {
                int indent = 8;
                int countIndent = 16;
                return Lists.newArrayList(
                        new CompoundEntry<ToolMatcher>(embed, y, width, Optional.of("predicate"), Lists.newArrayList(
                                FormatRegistry.arrayItemString(embed + indent, y, width - indent, "items", (o, entry) -> {
                                    ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                    Set<Item> items = access.getItems();
                                    if (items != null) {
                                        for (Item item : items) {
                                            StringEntry<String> entry1 = entry.createEntry();
                                            entry1.setValue(Registry.ITEM.getKey(item).toString());
                                            entry.addEntry(entry1);
                                        }
                                    }
                                }, ToolMatcher.class),
                                new StringEntry<>(embed + indent, y, width - indent, "Item TagKey", "", Optional.of("tag"), (o, entry) -> {
                                    ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                    TagKey<Item> tag = access.getTag();
                                    if (tag != null) {
                                        entry.setValue("#" + tag.location());
                                    }
                                }),
                                new MultipleTypeEntry<ToolMatcher>(embed + indent, y, width - indent, new DatapackEntry[]{
                                        new NumberEntry<Integer, ToolMatcher>(embed + countIndent, y, width - countIndent, "count", 0, (o, entry) -> {
                                            ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                            // doesn't matter if we use min or max.
                                            Integer max = access.getCount().getMax();
                                            if (max != null) {
                                                entry.setValue(String.valueOf(max));
                                            } else {
                                                entry.setValue("");
                                            }
                                        }),
                                        new CompoundEntry<ToolMatcher>(embed + countIndent, y, width - countIndent, Optional.of("count"),
                                                Lists.newArrayList(
                                                        new NumberEntry<>(embed + countIndent, y, width - countIndent, "min", 0, (o, entry) -> {
                                                            ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                                            Integer min = access.getCount().getMin();
                                                            if (min != null) {
                                                                entry.setValue(String.valueOf(min));
                                                            } else {
                                                                entry.setValue("");
                                                            }
                                                        }),
                                                        new NumberEntry<>(embed + countIndent, y, width - countIndent, "max", 0, (o, entry) -> {
                                                            ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                                            Integer max = access.getCount().getMax();
                                                            if (max != null) {
                                                                entry.setValue(String.valueOf(max));
                                                            } else {
                                                                entry.setValue("");
                                                            }
                                                        })
                                                ),
                                                (o, entry) -> {
                                                    for (DatapackEntry<ToolMatcher, ?> entryEntry : entry.getEntries()) {
                                                        entryEntry.deserialize(o);
                                                    }
                                                })
                                }),
                                new MultipleTypeEntry<ToolMatcher>(embed + indent, y, width - indent, new DatapackEntry[]{
                                        new NumberEntry<Integer, ToolMatcher>(embed + countIndent, y, width - countIndent, "durability", 0, (o, entry) -> {
                                            ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                            Integer max = access.getDurability().getMax();
                                            if (max != null) {
                                                entry.setValue("");
                                            } else {
                                                entry.setValue("");
                                            }
                                        }),
                                        new CompoundEntry<ToolMatcher>(embed + countIndent, y, width - countIndent, Optional.of("durability"),
                                                Lists.newArrayList(
                                                        new NumberEntry<>(embed + countIndent, y, width - countIndent, "min", 0, (o, entry) -> {
                                                            ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                                            Integer min = access.getDurability().getMin();
                                                            if (min != null) {
                                                                entry.setValue(String.valueOf(min));
                                                            } else {
                                                                entry.setValue("");
                                                            }
                                                        }),
                                                        new NumberEntry<>(embed + countIndent, y, width - countIndent, "max", 0, (o, entry) -> {
                                                            ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                                            Integer max = access.getDurability().getMax();
                                                            if (max != null) {
                                                                entry.setValue(String.valueOf(max));
                                                            } else {
                                                                entry.setValue("");
                                                            }
                                                        })
                                                ),
                                                (o, entry) -> {
                                                    for (DatapackEntry<ToolMatcher, ?> entryEntry : entry.getEntries()) {
                                                        entryEntry.deserialize(o);
                                                    }
                                                })
                                }),
                                new StringEntry<>(embed + indent, y, width - indent, "Potion ID", "", Optional.of("potion"), (o, entry) -> {
                                    ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                    if (access.getPotion() != null) {
                                        entry.setValue(Registry.POTION.getKey(access.getPotion()).toString());
                                    }
                                }),
                                new StringEntry<ToolMatcher>(embed + indent, y, width - indent, "NBT Data", "", Optional.of("nbt"), (o, entry) -> {
                                    ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                    JsonElement element = access.getNbtPredicate().serializeToJson();
                                    if (element.isJsonNull()) {
                                        entry.setValue("");
                                    } else {
                                        entry.setValue(element.getAsString());
                                    }
                                }).setEditMaxLength(100000),
                                FormatRegistry.createEnchantmentPredicateArray(embed + indent, y, width - indent, "enchantments", (o, entry) -> {
                                    ItemPredicateAccess access = (ItemPredicateAccess) o.predicate();
                                    for (EnchantmentPredicate enchantment : access.getEnchantments()) {
                                        CompoundEntry<EnchantmentPredicate> entry1 = entry.createEntry();
                                        entry1.deserialize(enchantment);
                                        entry.addEntry(entry1);
                                    }
                                })
                        ), (o, entry) -> {
                            for (DatapackEntry<ToolMatcher, ?> entryEntry : entry.getEntries()) {
                                entryEntry.deserialize(o);
                            }
                        })
                );
            })
    );

    static void init() {
    }

    private static void deserialize(ActionCondition con, CompoundAwareEntry<ActionCondition, ActionConditionType> entry) {
        System.out.println("whats heppening");
        for (DatapackEntry<ActionCondition, ?> entryEntry : entry.getEntries()) {
            System.out.println("we're dying");
            entryEntry.deserialize(con);
        }
    }
}
