package com.epherical.professions.profession.action.builtin.items;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.ActionEntry;
import com.epherical.professions.util.EnchantmentContainer;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
            logAction(professionContext, container != null ? new TextComponent(container.enchantment().getDescriptionId()) : Component.nullToEmpty(""));
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

    @Override
    public List<Component> displayInformation() {
        List<Component> components = super.displayInformation();
        Map<RewardType, Component> map = getRewardInformation();
        for (EnchantmentContainer enchant : enchantments) {
            components.add(((MutableComponent) enchant.enchantment().getFullname(enchant.level())).setStyle(
                    Style.EMPTY.withColor(ProfessionConfig.descriptors)).append(ProfessionPlatform.platform.displayInformation(this, map)));
        }
        return components;
    }


    @Override
    public List<ActionDisplay.Icon> clientFriendlyInformation(Component actionType) {
        List<ActionDisplay.Icon> components = super.clientFriendlyInformation(actionType);
        for (EnchantmentContainer enchantment : enchantments) {
            ActionDisplay.Icon icon = new ActionDisplay.Icon(Items.ENCHANTED_BOOK, ((MutableComponent) enchantment.enchantment().getFullname(enchantment.level()))
                    .setStyle(Style.EMPTY
                            .withColor(ProfessionConfig.descriptors)), allRewardInformation(), actionType);
            components.add(icon);
        }

        return components;
    }

    @Override
    public List<Action.Singular<Item>> convertToSingle(Profession profession) {
        List<Action.Singular<Item>> list = new ArrayList<>();
        for (Item realEntity : getRealItems()) {
            // todo; enchantment containers
            list.add(new EnchantAction.Single(realEntity, profession));
        }
        return list;
    }

    public List<EnchantmentContainer> getEnchantments() {
        return enchantments;
    }

    public static Builder enchant() {
        return new Builder();
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

    public static class Builder extends AbstractItemAction.Builder<Builder> {
        private final List<EnchantmentContainer> enchants = new ArrayList<>();

        public Builder enchant(Enchantment enchant, int level) {
            enchants.add(new EnchantmentContainer(enchant, level));
            return this;
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new EnchantAction(getConditions(), getRewards(), this.items, enchants);
        }
    }

    public class Single extends AbstractSingle<Item> {

        public Single(Item value, Profession profession) {
            super(value, profession);
        }

        @Override
        public ActionType getType() {
            return EnchantAction.this.getType();
        }

        @Override
        public Component createActionComponent() {
            return new TranslatableComponent(getType().getTranslationKey());
        }

        @Override
        public boolean handleAction(ProfessionContext context, Occupation player) {
            return EnchantAction.this.handleAction(context, player);
        }

        @Override
        public void giveRewards(ProfessionContext context, Occupation occupation) {
            EnchantAction.this.giveRewards(context, occupation);
        }
    }


}
