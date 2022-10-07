package com.epherical.professions.config;

import net.minecraft.network.chat.TextColor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class Config {

    private ForgeConfigSpec configSpec;

    private final ForgeConfigSpec.ConfigValue<Integer> version;
    private final ForgeConfigSpec.ConfigValue<Integer> maxOccupations;
    private final ForgeConfigSpec.ConfigValue<Boolean> useBuiltinDatapack;
    private final ForgeConfigSpec.ConfigValue<Boolean> allowCreativeModePayments;
    private final ForgeConfigSpec.ConfigValue<Boolean> displayXpAsPercentage;

    private final ForgeConfigSpec.ConfigValue<Boolean> autoJoinProfessions;
    private final ForgeConfigSpec.ConfigValue<Boolean> preventLeavingProfession;

    private final ForgeConfigSpec.ConfigValue<Boolean> announceLevelUps;
    private final ForgeConfigSpec.ConfigValue<Integer> announceEveryXLevel;

    private final ForgeConfigSpec.ConfigValue<String> headerBorders;
    private final ForgeConfigSpec.ConfigValue<String> descriptors;
    private final ForgeConfigSpec.ConfigValue<String> variables;
    private final ForgeConfigSpec.ConfigValue<String> errors;
    private final ForgeConfigSpec.ConfigValue<String> success;
    private final ForgeConfigSpec.ConfigValue<String> money;
    private final ForgeConfigSpec.ConfigValue<String> exp;
    private final ForgeConfigSpec.ConfigValue<String> noMoreRewards;
    private final ForgeConfigSpec.ConfigValue<String> moreRewards;

    private final ForgeConfigSpec.ConfigValue<Boolean> clearProgressOnLeave;
    private ForgeConfigSpec.ConfigValue<Boolean> persistBlockOwnership;
    private final ForgeConfigSpec.ConfigValue<Integer> paymentCoolDown;

    private final ForgeConfigSpec.ConfigValue<Boolean> logInChat;
    private final ForgeConfigSpec.ConfigValue<Boolean> displayOutput;

    public Config() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        version = builder.comment("CONFIG VERSION, DO NOT EDIT!")
                .define("version", ProfessionConfig.version);
        maxOccupations = builder.comment("The max amount of occupations a user can have active at a time.",
                        "Default is 3, set to 0 to disable and allow any amount of occupations to be joined.")
                .define("maxOccupations", ProfessionConfig.maxOccupations);

        autoJoinProfessions = builder.comment("Default false. If set to true, when the player joins the server, they will join *ALL* \n" +
                "possible professions, disregarding 'maxOccupations'.").define("autoJoinProfessions", ProfessionConfig.autoJoinProfessions);
        preventLeavingProfession = builder.comment("Default false. If set to true, will prevent the player from leaving any profession. \n" +
                "Good for enforcing a progression system").define("preventLeavingProfession", ProfessionConfig.preventLeavingProfession);

        useBuiltinDatapack = builder.comment(
                "Default true. If you are a regular user you can ignore this. This is to provide an all in one experience",
                "without having to download additional files. Modpack developers looking to create or rebalance their own professions",
                "may want to disable this.").define("useBuiltinDatapack", ProfessionConfig.useBuiltinDatapack);
        allowCreativeModePayments = builder.comment("This will allow users in creative mode to get paid or not. Defaults to false.")
                .define("allowCreativeModePayments", ProfessionConfig.allowCreativeModePayments);
        displayXpAsPercentage = builder.comment("Will display the /professions stats command either as a percentage of the way to the next level",
                        "or the xp in numbers required for the next level e.g (203/2010). Defaults to percentage.")
                .define("displayXpAsPercentage", ProfessionConfig.displayXpAsPercentage);
        builder.comment("Announcements").push("announce");
        announceLevelUps = builder.comment("Determines if a message should be announced to all online players when someone levels up")
                .define("levelUps", ProfessionConfig.announceLevelUps);
        announceEveryXLevel = builder.comment("If you want to only announce level ups every so often, you can change this from 0.",
                        "example: 5 would announce to all players at level 5, 10, 15, etc")
                .define("every-x-levels", ProfessionConfig.announceEveryXLevel);
        builder.pop();
        builder.comment("Colors").push("colors");
        headerBorders = builder.comment("The color of any headers/footers for commands")
                .define("headerBorders", ProfessionConfig.headerBorders.serialize());
        descriptors = builder.comment("This can include player names and subjects of a particular action.")
                .define("descriptors", ProfessionConfig.descriptors.serialize());
        variables = builder.comment("Used in certain commands to differentiate from the rest of the message.")
                .define("variables", ProfessionConfig.variables.serialize());
        errors = builder.comment("Used to set the main color of error messages.")
                .define("errors", ProfessionConfig.errors.serialize());
        success = builder.comment("Used to set the main color for success messages.")
                .define("success", ProfessionConfig.success.serialize());
        money = builder.comment("Used for any messages that involve money.")
                .define("money", ProfessionConfig.money.serialize());
        exp = builder.comment("Used for any messages that involve experience.")
                .define("exp", ProfessionConfig.experience.serialize());
        noMoreRewards = builder.comment("Used for any messages that involve whether or not there are more rewards. (/professions info)")
                .define("noMoreRewards", ProfessionConfig.noMoreRewards.serialize());
        moreRewards = builder.comment("Used for any messages that involve more rewards. (/professions info)")
                .define("moreRewards", ProfessionConfig.moreRewards.serialize());
        builder.pop();
        builder.comment("Balancing").push("balancing");
        clearProgressOnLeave = builder.comment("Default: False. Set to true if you want accumulated levels on a profession to be entirely cleared",
                        "when they leave their profession.")
                .define("clearProgressOnLeave", ProfessionConfig.clearProgressOnLeave);
        //persistBlockOwnership = builder.comment("");
        paymentCoolDown = builder.comment("Default 30 seconds. This determines how long the user will have to wait before they can get paid for an action",
                        "in the same block position.")
                .define("paymentCoolDown", (int) ProfessionConfig.paymentCoolDown);
        builder.pop();
        builder.comment("Actions").push("actions");
        logInChat = builder.comment("Will display every valid action in chat. Defaults to false.")
                .define("logInChat", ProfessionConfig.logInChat);
        displayOutput = builder.comment("Will display valid action output in your action bar. Defaults to false.")
                .define("displayOutput", ProfessionConfig.displayOutput);
        builder.pop();
        this.configSpec = builder.build();
    }


    public void initConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == configSpec) {
            ProfessionConfig.version = version.get();
            ProfessionConfig.maxOccupations = maxOccupations.get();
            ProfessionConfig.useBuiltinDatapack = useBuiltinDatapack.get();
            ProfessionConfig.allowCreativeModePayments = allowCreativeModePayments.get();
            ProfessionConfig.displayXpAsPercentage = displayXpAsPercentage.get();
            ProfessionConfig.announceLevelUps = announceLevelUps.get();
            ProfessionConfig.announceEveryXLevel = announceEveryXLevel.get();
            ProfessionConfig.headerBorders = TextColor.parseColor(headerBorders.get());
            ProfessionConfig.descriptors = TextColor.parseColor(descriptors.get());
            ProfessionConfig.variables = TextColor.parseColor(variables.get());
            ProfessionConfig.errors = TextColor.parseColor(errors.get());
            ProfessionConfig.success = TextColor.parseColor(success.get());
            ProfessionConfig.money = TextColor.parseColor(money.get());
            ProfessionConfig.experience = TextColor.parseColor(exp.get());
            ProfessionConfig.noMoreRewards = TextColor.parseColor(noMoreRewards.get());
            ProfessionConfig.moreRewards = TextColor.parseColor(moreRewards.get());
            ProfessionConfig.clearProgressOnLeave = clearProgressOnLeave.get();
            ProfessionConfig.paymentCoolDown = paymentCoolDown.get();
            ProfessionConfig.logInChat = logInChat.get();
            ProfessionConfig.displayOutput = displayOutput.get();
            ProfessionConfig.preventLeavingProfession = preventLeavingProfession.get();
            ProfessionConfig.autoJoinProfessions = autoJoinProfessions.get();
        }
    }

    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }
}
