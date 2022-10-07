package com.epherical.professions.config;

import com.epherical.professions.Constants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.TextColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static com.epherical.professions.config.ProfessionConfig.*;

public class CommonConfig {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected HoconConfigurationLoader loader;
    protected ConfigurationNode rootNode;

    protected final String configName;
    protected final boolean devEnvironment;
    private TypeSerializerCollection.Builder serializers;

    public CommonConfig(boolean devEnvironment, String configName) {
        this.devEnvironment = devEnvironment;
        this.configName = configName;
        this.serializers = TypeSerializerCollection.builder();
    }

    public CommonConfig(boolean devEnvironment) {
        this(devEnvironment, "professions.conf");
    }

    public <T, V extends TypeSerializer<T>> void addSerializer(Class<T> clazz, V instance) {
        serializers.register(clazz, instance);
    }

    public void addSerializer(TypeSerializerCollection collection) {
        serializers.registerAll(collection);
    }


    public boolean loadConfig() {
        File configDirectory = new File(FabricLoader.getInstance().getConfigDir().toFile(), Constants.MOD_ID);
        File file = new File(configDirectory, configName);

        boolean createdFile = false;
        URL path = null;
        if (devEnvironment) {
            // If we are in a development environment, we are going to use the config that is present in the jar instead.
            path = getClass().getClassLoader().getResource(configName);
        } else {
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(configName)) {
                byte[] bytes = new byte[stream.available()];
                stream.read(bytes);
                LOGGER.debug("Creating default config file: " + configName);
                createdFile = createdFile(file);
                if (createdFile) {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        outputStream.write(bytes);
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Could not find an internal config file for {}. Not to worry, let's try and generate a default.", configName);
            } finally {
                try {
                    createdFile = createdFile(file);
                    path = file.toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        this.loader = HoconConfigurationLoader.builder()
                .sink(() -> new BufferedWriter(new FileWriter(file)))
                .defaultOptions(options -> options.serializers(builder -> builder.registerAll(serializers.build())))
                .url(path)
                .build();
        try {
            if (createdFile) {
                this.loader.save(generateConfig(CommentedConfigurationNode.root()));
            }
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
        try {
            int currentConfigVersion = version;
            this.rootNode = loader.load();
            parseConfig(rootNode);
            int value = this.rootNode.node("version").getInt();
            if (value != currentConfigVersion) {
                LOGGER.info("Upgrading professions config from {} to {}", version, currentConfigVersion);
                version = currentConfigVersion;
                this.loader.save(generateConfig(CommentedConfigurationNode.root()));
                this.rootNode = loader.load();
                parseConfig(rootNode);
            }
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected ConfigurationNode generateConfig(CommentedConfigurationNode node) {
        try {
            node.node("version").set(version)
                    .comment("CONFIG VERSION, DO NOT EDIT!");
            node.node("maxOccupations").set(maxOccupations)
                    .comment("The max amount of occupations a user can have active at a time.\n" +
                            "Default is 3, set to 0 to disable and allow any amount of occupations to be joined.");

            node.node("autoJoinProfessions").set(autoJoinProfessions)
                    .comment("Default false. If set to true, when the player joins the server, they will join *ALL* \n" +
                            "possible professions, disregarding 'maxOccupations'.");
            node.node("preventLeavingProfession").set(preventLeavingProfession)
                    .comment("Default false. If set to true, will prevent the player from leaving any profession. \n" +
                            "Good for enforcing a progression system");

            node.node("useBuiltinDatapack").set(useBuiltinDatapack)
                    .comment("""
                            Default true. If you are a regular user you can ignore this. This is to provide an all in one experience\s
                            without having to download additional files. Modpack developers looking to create or rebalance their own professions\s
                            may want to disable this.""");
            node.node("useHardcoreDatapack").set(useHardcoreDatapack)
                            .comment("""
                                    Default false. Requires 'useBuiltinDatapack' to also be true. The hardcore datapack locks progression\s
                                    meaning you will need to level up to use certain things. I recommend using autoJoinProfessions and preventLeavingProfession\s
                                    if you are going to be using this mode.
                                    """);
            node.node("allowCreativeModePayments").set(allowCreativeModePayments)
                    .comment("This will allow users in creative mode to get paid or not. Defaults to false.");
            node.node("displayXpAsPercentage").set(displayXpAsPercentage)
                    .comment("Will display the /professions stats command either as a percentage of the way to the next level \n" +
                            "or the xp in numbers required for the next level e.g (203/2010). Defaults to percentage.");

            node.node("currency").node("overrideCurrencyID").set(overrideCurrencyID)
                    .comment("If you want, you can set this to true and it will override\n" +
                            "the currency in the datapack to be whatever is set in 'overridenCurrencyID'");
            node.node("currency").node("overriddenCurrencyID").set(overriddenCurrencyID)
                    .comment("By default this is eights_economy:dollars, the default currency in EightsEconomyP. If you have a different\n" +
                            "currency you want to use, do <namespace>:<path> if you know what it is.");


            // announcements
            node.node("announce").node("levelUps").set(announceLevelUps)
                    .comment("Determines if a message should be announced to all online players when someone levels up");
            node.node("announce").node("every-x-levels").set(announceEveryXLevel)
                    .comment("If you want to only announce level ups every so often, you can change this from 0. \n" +
                            "example: 5 would announce to all players at level 5, 10, 15, etc");
            // color schemes
            node.node("colors").node("headerBorders").set(headerBorders.serialize())
                    .comment("The color of any headers/footers for commands");
            node.node("colors").node("descriptors").set(descriptors.serialize())
                    .comment("This can include player names and subjects of a particular action.");
            node.node("colors").node("variables").set(variables.serialize())
                    .comment("Used in certain commands to differentiate from the rest of the message.");
            node.node("colors").node("errors").set(errors.serialize())
                    .comment("Used to set the main color of error messages");
            node.node("colors").node("success").set(success.serialize())
                    .comment("Used to set the main color for success messages");
            node.node("colors").node("money").set(money.serialize())
                    .comment("Used for any messages that involve money");
            node.node("colors").node("exp").set(experience.serialize())
                    .comment("Used for any messages that involve experience");
            node.node("colors").node("noMoreRewards").set(noMoreRewards.serialize())
                    .comment("Used for any messages that involve whether or not there are more rewards. (/professions info)");
            node.node("colors").node("moreRewards").set(moreRewards.serialize())
                    .comment("Used for any messages that involve more rewards (/professions info)");

            // balancing
            node.node("balancing").node("clearProgressOnLeave").set(clearProgressOnLeave)
                    .comment("Default: False. Set to true if you want accumulated levels on a profession to be entirely cleared \n" +
                            "when they leave their profession.");
            node.node("balancing").node("persistBlockOwnership").set(persistBlockOwnership)
                    .comment("""
                            Default: True. When certain blocks are placed, they are registered to a player to allow them to earn experience\s
                            while not around the block. If this is set to false, any time the server is restarted, it will have to be re-opened\s
                            to give the player payouts again.""");
            node.node("balancing").node("paymentCoolDown").set(paymentCoolDown)
                    .comment("Default 30 seconds. This determines how long the user will have to wait before they can get paid for an action \n" +
                            "in the same block position.");

            node.node("actions").node("logInChat").set(logInChat)
                    .comment("Will display every valid action in chat. Defaults to false.");
            node.node("actions").node("displayOutput").set(displayOutput)
                    .comment("WIll display every valid action in your action bar. Defaults to false.");


        } catch (SerializationException e) {
            e.printStackTrace();
        }

        return node;
    }

    protected void parseConfig(ConfigurationNode node) {
        version = node.node("version").getInt(version);
        maxOccupations = node.node("maxOccupations").getInt(maxOccupations);
        useBuiltinDatapack = node.node("useBuiltinDatapack").getBoolean(useBuiltinDatapack);
        useHardcoreDatapack = node.node("useHardcoreDatapack").getBoolean(useHardcoreDatapack);
        autoJoinProfessions = node.node("autoJoinProfessions").getBoolean(autoJoinProfessions);
        preventLeavingProfession = node.node("preventLeavingProfession").getBoolean(preventLeavingProfession);
        displayXpAsPercentage = node.node("displayXpAsPercentage").getBoolean(displayXpAsPercentage);
        allowCreativeModePayments = node.node("allowCreativeModePayments").getBoolean(allowCreativeModePayments);

        overrideCurrencyID = node.node("currency").node("overrideCurrencyID").getBoolean(overrideCurrencyID);
        overriddenCurrencyID = node.node("currency").node("overriddenCurrencyID").getString(overriddenCurrencyID);

        announceLevelUps = node.node("announce").node("levelUps").getBoolean(announceLevelUps);
        announceEveryXLevel = node.node("announce").node("every-x-levels").getInt(announceEveryXLevel);

        headerBorders = TextColor.parseColor(node.node("colors").node("headerBoarders").getString(headerBorders.serialize()));
        descriptors = TextColor.parseColor(node.node("colors").node("descriptors").getString(descriptors.serialize()));
        variables = TextColor.parseColor(node.node("colors").node("variables").getString(variables.serialize()));
        errors = TextColor.parseColor(node.node("colors").node("errors").getString(errors.serialize()));
        success = TextColor.parseColor(node.node("colors").node("success").getString(success.serialize()));
        money = TextColor.parseColor(node.node("colors").node("money").getString(money.serialize()));
        experience = TextColor.parseColor(node.node("colors").node("exp").getString(experience.serialize()));
        noMoreRewards = TextColor.parseColor(node.node("colors").node("noMoreRewards").getString(noMoreRewards.serialize()));
        moreRewards = TextColor.parseColor(node.node("colors").node("moreRewards").getString(moreRewards.serialize()));

        clearProgressOnLeave = node.node("balancing").node("clearProgressOnLeave").getBoolean(clearProgressOnLeave);
        persistBlockOwnership = node.node("balancing").node("persistBlockOwnership").getBoolean(persistBlockOwnership);
        paymentCoolDown = node.node("balancing").node("paymentCoolDown").getLong(paymentCoolDown);

        logInChat = node.node("actions").node("logInChat").getBoolean(logInChat);
        displayOutput = node.node("actions").node("displayOutput").getBoolean(displayOutput);
    }

    private boolean canCreateFile(File file) {
        return file.exists();
    }

    private boolean createdFile(File file) {
        try {
            if (!file.getParentFile().exists() && file.getParentFile().mkdirs()) {
                LOGGER.debug("Created directory for: " + file.getParentFile().getCanonicalPath());
            }

            if (!file.exists() && file.createNewFile()) {
                return true;
            }
        } catch (IOException e) {
            LOGGER.warn("Error creating new config file ", e);
            return false;
        }
        return false;
    }

    public boolean reloadConfig() {
        try {
            rootNode = this.loader.load();
            parseConfig(rootNode);
            loader.save(rootNode);
            return true;
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ConfigurationNode getRootNode() {
        return rootNode;
    }

}
