package com.epherical.professions.config;

import net.minecraft.network.chat.TextColor;

public class ProfessionConfig {

    public static int version = 2;

    public static int maxOccupations = 3;
    public static boolean useBuiltinDatapack = true;
    public static boolean autoJoinProfessions = false;
    public static boolean preventLeavingProfession = false;
    public static boolean displayXpAsPercentage = true;
    public static boolean allowCreativeModePayments = false;
    public static boolean logInChat = false;

    public static boolean overrideCurrencyID = false;
    public static String overriddenCurrencyID = "eights_economy:dollars";

    public static boolean clearProgressOnLeave = false;
    public static boolean persistBlockOwnership = true;
    public static long paymentCoolDown = 30L;

    public static boolean announceLevelUps = true;
    public static int announceEveryXLevel = 0;

    // Color Scheme
    public static TextColor headerBorders = TextColor.parseColor("#6e6e6e");
    public static TextColor descriptors = TextColor.parseColor("#00c2ab");
    public static TextColor variables = TextColor.parseColor("#d19b06");
    public static TextColor errors = TextColor.parseColor("#a80000");
    public static TextColor success = TextColor.parseColor("#499133");
    public static TextColor money = TextColor.parseColor("#47ad07");
    public static TextColor experience = TextColor.parseColor("#00a8b8");
    public static TextColor noMoreRewards = TextColor.parseColor("#913333");
    public static TextColor moreRewards = TextColor.parseColor("#99ff00");



    public static void reload() {
        //ProfessionsMod.getInstance().getConfig().reloadConfig();
    }

}
