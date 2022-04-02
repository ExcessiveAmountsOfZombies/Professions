package com.epherical.professions.profession.rewards.builtin;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.UUID;

public record PaymentReward(double amount, @Nullable Currency currency) implements Reward {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public RewardType getType() {
        return Rewards.PAYMENT_REWARD;
    }

    @Override
    public void giveReward(ProfessionContext context, Action action, Occupation occupation) {
        Economy economy = ProfessionsMod.getEconomy();
        if (economy != null) {
            UUID playerID = context.getParameter(ProfessionParameter.THIS_PLAYER).getUuid();
            UniqueUser user = economy.getOrCreatePlayerAccount(playerID);
            if (user != null) {
                user.depositMoney(currency, amount, "Professions Action Reward");
            }
        }
    }

    @Override
    public Component rewardChatInfo() {
        return new TextComponent(String.format("$%.2f", amount)).setStyle(Style.EMPTY.withColor(ProfessionConfig.money));
    }

    public static class RewardSerializer implements Serializer<PaymentReward> {

        @Override
        public void serialize(JsonObject json, PaymentReward value, JsonSerializationContext serializationContext) {
            json.addProperty("amount", value.amount);
            if (value.currency != null) {
                json.addProperty("currency", value.currency.getIdentity());
            }
        }

        @Override
        public PaymentReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            double amount = GsonHelper.getAsDouble(json, "amount");
            Currency currency = null;
            if (ProfessionsMod.getEconomy() != null) {
                String currencyString = GsonHelper.getAsString(json, "currency");
                currency = ProfessionsMod.getEconomy().getCurrency(new ResourceLocation(currencyString));
                if (currency == null) {
                    LOGGER.warn("PaymentReward used was a null currency {}. Using default currency of {}", currencyString, ProfessionsMod.getEconomy().getDefaultCurrency());
                    currency = ProfessionsMod.getEconomy().getDefaultCurrency();
                }
            }
            return new PaymentReward(amount, currency);
        }
    }
}
