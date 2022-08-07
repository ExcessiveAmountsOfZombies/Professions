package com.epherical.professions.profession.rewards.builtin;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.professions.FabricRegConstants;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.client.entry.NumberEntry;
import com.epherical.professions.client.entry.StringEntry;
import com.epherical.professions.client.format.Format;
import com.epherical.professions.client.format.FormatBuilder;
import com.epherical.professions.client.format.RegularFormat;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
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

import java.util.List;
import java.util.UUID;

public record PaymentReward(double amount, @Nullable Currency currency) implements Reward {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public RewardType getType() {
        return FabricRegConstants.PAYMENT_REWARD;
    }

    @Override
    public void giveReward(ProfessionContext context, Action action, Occupation occupation) {
        Economy economy = ProfessionsFabric.getEconomy();
        if (economy != null) {
            UUID playerID = context.getParameter(ProfessionParameter.THIS_PLAYER).getUuid();
            UniqueUser user = economy.getOrCreatePlayerAccount(playerID);
            Currency currency = this.currency;
            if (ProfessionConfig.overrideCurrencyID) {
                currency = economy.getCurrency(new ResourceLocation(ProfessionConfig.overriddenCurrencyID));
                if (currency == null) {
                    LOGGER.warn("You overrode the currency in the datapack, but an override currency could not be found! If this was a mistake, go in the config");
                    LOGGER.warn("And change 'overrideCurrencyID' to false!");
                    throw new RuntimeException("payment could not be processed.");
                }
            }
            if (user != null) {
                if (amount > 0) {
                    user.depositMoney(currency, amount, "Professions Action Reward");
                } else {
                    user.withdrawMoney(currency, amount, "Professions Action Penalty");
                }
                context.getParameter(ProfessionParameter.ACTION_LOGGER).addMoneyReward(rewardChatInfo());
            }
        }
    }

    @Override
    public Component rewardChatInfo() {
        return new TextComponent(String.format("$%.2f", amount)).setStyle(Style.EMPTY.withColor(ProfessionConfig.money));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class RewardSerializer implements Serializer<PaymentReward> {

        @Override
        public void serialize(JsonObject json, PaymentReward value, JsonSerializationContext serializationContext) {
            json.addProperty("amount", value.amount);
            if (value.currency != null) {
                json.addProperty("currency", value.currency.getIdentity());
            } else {
                json.addProperty("currency", "eights_economy:dollars");
            }
        }

        @Override
        public PaymentReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            double amount = GsonHelper.getAsDouble(json, "amount");
            Currency currency = null;
            if (ProfessionsFabric.getEconomy() != null) {
                String currencyString = GsonHelper.getAsString(json, "currency");
                currency = ProfessionsFabric.getEconomy().getCurrency(new ResourceLocation(currencyString));
                if (currency == null) {
                    LOGGER.warn("PaymentReward used was a null currency {}. Using default currency of {}", currencyString, ProfessionsFabric.getEconomy().getDefaultCurrency().getIdentity());
                    currency = ProfessionsFabric.getEconomy().getDefaultCurrency();
                }
            }
            return new PaymentReward(amount, currency);
        }
    }

    public static class Builder implements Reward.Builder {
        private double amount;
        private Currency currency;

        public Builder money(double amount, Currency currency) {
            this.amount = amount;
            this.currency = currency;
            return this;
        }

        @Override
        public Reward build() {
            return new PaymentReward(amount, currency);
        }
    }

    public static class DatapackBuilder implements FormatBuilder<PaymentReward> {

        @Override
        public Format<PaymentReward> buildDefaultFormat() {
            return deserializeToFormat(null);
        }

        @Override
        public Format<PaymentReward> deserializeToFormat(PaymentReward paymentReward) {
            RegularFormat<PaymentReward> format = new RegularFormat<>((embed, y, width) -> List.of(
                    new NumberEntry<Double, PaymentReward>(embed, y, width, "amount", 1.0, (reward, entry) -> {
                        entry.setValue(String.valueOf(reward.amount));
                    }),
                    new StringEntry<PaymentReward>(embed, y, width, "currency", ProfessionConfig.overriddenCurrencyID,
                            (reward, entry) -> {
                                if (reward.currency != null) {
                                    entry.setValue(reward.currency.getIdentity());
                                } else {
                                    entry.setValue("eights_economy:dollars");
                                }
                            })
            ));

            return format;
        }
    }
}
