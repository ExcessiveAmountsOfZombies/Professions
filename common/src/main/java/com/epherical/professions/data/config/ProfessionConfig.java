package com.epherical.professions.data.config;

import com.epherical.epherolib.libs.org.spongepowered.configurate.CommentedConfigurationNode;
import com.epherical.epherolib.libs.org.spongepowered.configurate.ConfigurateException;
import com.epherical.epherolib.libs.org.spongepowered.configurate.ConfigurationNode;
import com.epherical.epherolib.libs.org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import com.epherical.epherolib.libs.org.spongepowered.configurate.serialize.SerializationException;
import com.epherical.epherolib.libs.org.spongepowered.configurate.serialize.TypeSerializer;
import com.epherical.epherolib.libs.org.spongepowered.configurate.serialize.TypeSerializerCollection;
import com.epherical.professions.ProfessionsCommon;
import net.minecraft.network.chat.TextColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfessionConfig {

    public static int version = 1;

    // Color Scheme
    public static TextColor headerBorders = TextColor.parseColor("#6e6e6e").getOrThrow();
    public static TextColor descriptors = TextColor.parseColor("#00c2ab").getOrThrow();
    public static TextColor variables = TextColor.parseColor("#d19b06").getOrThrow();
    public static TextColor errors = TextColor.parseColor("#a80000").getOrThrow();
    public static TextColor success = TextColor.parseColor("#499133").getOrThrow();
    public static TextColor money = TextColor.parseColor("#47ad07").getOrThrow();
    public static TextColor experience = TextColor.parseColor("#00a8b8").getOrThrow();
    public static TextColor noMoreRewards = TextColor.parseColor("#913333").getOrThrow();
    public static TextColor moreRewards = TextColor.parseColor("#99ff00").getOrThrow();

    protected static final Logger LOGGER = LogManager.getLogger();

    protected HoconConfigurationLoader loader;
    protected ConfigurationNode rootNode;

    protected final String configName;
    protected final boolean devEnvironment;
    private TypeSerializerCollection.Builder serializers;

    private File configDir;

    public ProfessionConfig(boolean devEnvironment, String configName, File configDir) {
        this.devEnvironment = devEnvironment;
        this.configName = configName;
        this.serializers = TypeSerializerCollection.builder();
        this.configDir = configDir;
    }

    public <T, V extends TypeSerializer<T>> void addSerializer(Class<T> clazz, V instance) {
        serializers.register(clazz, instance);
    }

    public void addSerializer(TypeSerializerCollection collection) {
        serializers.registerAll(collection);
    }


    public boolean loadConfig() {
        File configDirectory = new File(configDir, ProfessionsCommon.MOD_ID);
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


        } catch (SerializationException e) {
            e.printStackTrace();
        }

        return node;
    }

    protected void parseConfig(ConfigurationNode node) {
        version = node.node("version").getInt(version);

        headerBorders = TextColor.parseColor(node.node("colors")
                .node("headerBoarders").getString(headerBorders.serialize())).getOrThrow();
        descriptors = TextColor.parseColor(node.node("colors")
                .node("descriptors").getString(descriptors.serialize())).getOrThrow();
        variables = TextColor.parseColor(node.node("colors")
                .node("variables").getString(variables.serialize())).getOrThrow();
        errors = TextColor.parseColor(node.node("colors")
                .node("errors").getString(errors.serialize())).getOrThrow();
        success = TextColor.parseColor(node.node("colors")
                .node("success").getString(success.serialize())).getOrThrow();
        money = TextColor.parseColor(node.node("colors")
                .node("money").getString(money.serialize())).getOrThrow();
        experience = TextColor.parseColor(node.node("colors")
                .node("exp").getString(experience.serialize())).getOrThrow();
        noMoreRewards = TextColor.parseColor(node.node("colors")
                .node("noMoreRewards").getString(noMoreRewards.serialize())).getOrThrow();
        moreRewards = TextColor.parseColor(node.node("colors")
                .node("moreRewards").getString(moreRewards.serialize())).getOrThrow();
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
