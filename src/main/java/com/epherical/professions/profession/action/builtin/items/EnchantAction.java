package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.epherical.professions.util.EnchantmentContainer;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantAction extends AbstractItemAction {

    protected final List<EnchantmentContainer> enchantments;

    protected EnchantAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Item>> items, List<EnchantmentContainer> enchantments) {
        super(conditions, rewards, items);
        this.enchantments = enchantments;
    }


    @Override
    public boolean test(ProfessionContext professionContext) {
        if (professionContext.hasParameter(ProfessionParameter.ITEM_INVOLVED)) {
            // if it has an item involved, it's just item checking.
            return super.test(professionContext);
        } else {
            EnchantmentContainer container = professionContext.getPossibleParameter(ProfessionParameter.ENCHANTMENT);
            return container != null && enchantments.contains(container);
        }
    }

    @Override
    public ActionType getType() {
        return Actions.ENCHANT_ITEM;
    }

    @Override
    public double modifyReward(ProfessionContext context, Reward reward, double base) {
        return base;
    }

    public static class Serializer extends AbstractItemAction.Serializer<EnchantAction> {


        @Override
        public void serialize(@NotNull JsonObject json, EnchantAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (EnchantmentContainer enchantment : value.enchantments) {
                String key = Registry.ENCHANTMENT.getKey(enchantment.enchantment()).toString() + "#" + enchantment.level();
                array.add(key);
            }
            json.add("enchants", array);
        }

        @Override
        public EnchantAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = new JsonArray();
            try {
                array = GsonHelper.getAsJsonArray(object, "enchants");
            } catch (JsonSyntaxException ignored) {/* optional */}
            List<EnchantmentContainer> enchantmentContainers = new ArrayList<>();
            for (JsonElement element : array) {
                String[] key = element.getAsString().split("#");
                Enchantment enchantment = Registry.ENCHANTMENT.get(new ResourceLocation(key[0]));
                if (enchantment != null) { // TODO; maybe throw a warning or something here if it wasn't added.
                    enchantmentContainers.add(new EnchantmentContainer(enchantment, Integer.parseInt(key[1])));
                }
            }
            return new EnchantAction(conditions, rewards, deserializeItems(object), enchantmentContainers);
        }
    }
}
