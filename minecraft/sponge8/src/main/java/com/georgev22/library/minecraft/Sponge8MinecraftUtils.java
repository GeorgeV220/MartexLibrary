package com.georgev22.library.minecraft;

import com.georgev22.library.extensions.java.JavaExtension;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.utilities.Color;
import com.georgev22.library.utilities.DiscordWebHook;
import com.georgev22.library.utilities.LoggerWrapper;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.google.common.collect.Lists;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.georgev22.library.utilities.Utils.placeHolder;

public class Sponge8MinecraftUtils {

    public static boolean isList(final @NotNull FileConfiguration file, final String path) {
        return Utils.isList(file.get(path));
    }

    public static void broadcastMsg(@NotNull Server server, final String input) {
        server.sendMessage(textComponent(colorize(input)));
    }

    public static void printMsg(@NotNull Logger logger, final String input) {
        logger.info(colorize(input));
    }


    public static void broadcastMsg(@NotNull Server server, final @NotNull List<String> input) {
        input.forEach(s -> broadcastMsg(server, s));
    }

    public static void broadcastMsg(@NotNull Server server, final @NotNull String... input) {
        Arrays.stream(input).forEach(s -> broadcastMsg(server, s));
    }

    public static void broadcastMsg(@NotNull Server server, final Object input) {
        broadcastMsg(server, String.valueOf(input));
    }

    public static void printMsg(@NotNull Logger logger, final @NotNull List<String> input) {
        input.forEach(s -> printMsg(logger, s));
    }

    public static void printMsg(@NotNull Logger logger, final Object input) {
        printMsg(logger, String.valueOf(input));
    }

    public static void printMsg(@NotNull Logger logger, final String... input) {
        Arrays.stream(input).forEach(s -> printMsg(logger, s));
    }


    public static void msg(final Audience target, final String message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeHolder(message, map, ignoreCase));
    }

    public static void msg(final Audience target, final List<String> message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeHolder(message, map, ignoreCase));
    }

    public static void msg(final Audience target, final String[] message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeHolder(message, map, ignoreCase));
    }

    public static void msg(final Audience target, final FileConfiguration file, final String path) {
        msg(target, file, path, null, false);
    }

    public static void msg(final Audience target, final FileConfiguration file, final String path,
                           final Map<String, String> map, final boolean replace) {
        if (file == null) {
            throw new IllegalArgumentException("The file can't be null.");
        }

        if (path == null) {
            throw new IllegalArgumentException("The path can't be null");
        }

        if (!file.contains(path)) {
            throw new IllegalArgumentException("The path: " + path + " doesn't exist.");
        }

        if (isList(file, path)) {
            msg(target, file.getStringList(path), map, replace);
        } else {
            msg(target, file.getString(path), map, replace);
        }
    }

    public static void msg(final Audience target, final String message) {
        if (target == null) {
            throw new IllegalArgumentException("The target can't be null");
        }
        if (message == null) {
            return;
        }
        target.sendMessage(textComponent(colorize(message)));
    }

    public static void msg(final Audience target, final String... messages) {
        if (target == null) {
            throw new IllegalArgumentException("The target can't be null");
        }
        if (messages == null || messages.length == 0) {
            return;
        }
        if (Arrays.stream(messages).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string array can't have null elements.");
        }
        for (String message : messages)
            target.sendMessage(textComponent(colorize(message)));
    }

    public static void msg(final Audience target, final List<String> message) {
        if (target == null) {
            throw new IllegalArgumentException("The target can't be null");
        }
        if (message == null || message.isEmpty()) {
            return;
        }
        if (message.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string array can't have null elements.");
        }
        msg(target, message.toArray(new String[0]));
    }


    /**
     * Returns a translated string.
     *
     * @param msg The message to be translated
     * @return A translated message
     */
    public static @NotNull String colorize(final String msg) {
        String unEditedMessage = msg;
        if (unEditedMessage == null) {
            throw new IllegalArgumentException("The string can't be null!");
        }
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(unEditedMessage);
        while (matcher.find()) {
            String hexCode = unEditedMessage.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            unEditedMessage = unEditedMessage.replace(hexCode, builder.toString());
            matcher = pattern.matcher(unEditedMessage);
        }
        return LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize(unEditedMessage));
    }

    /**
     * Returns a translated string array.
     *
     * @param array Array of messages
     * @return A translated message array
     */
    public static String @NotNull [] colorize(final String... array) {
        if (array == null) {
            throw new IllegalArgumentException("The string array can't be null!");
        }
        if (Arrays.stream(array).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string array can't have null elements!");
        }
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = colorize(newarr[i]);
        }
        return newarr;
    }


    /**
     * Returns a translated string collection.
     *
     * @param coll The collection to be translated
     * @return A translated message
     */
    public static @NotNull List<String> colorize(final List<String> coll) {
        if (coll == null) throw new IllegalArgumentException("The string collection can't be null!");
        if (coll.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("The string collection can't have null elements!");
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(Sponge8MinecraftUtils::colorize);
        return newColl;
    }

    public static @NotNull TextComponent textComponent(String input) {
        return LegacyComponentSerializer.legacySection().deserialize(input);
    }

    /**
     * Converts a String List that contains color codes to Color List
     *
     * @param list the String List that contains the color codes
     * @return the new Color List with the colors of the input Color String List
     */
    public static @NotNull List<Color> colorsStringListToColorList(@NotNull List<String> list) {
        return colorsStringListToColorList(list.toArray(new String[0]));
    }

    /**
     * Converts a String Array that contains color codes to Color List
     *
     * @param array the String Array that contains the color codes
     * @return the new Color List with the colors of the input Color String Array
     */
    public static @NotNull List<Color> colorsStringListToColorList(String @NotNull ... array) {
        List<Color> colorList = Lists.newArrayList();
        for (String str : array) {
            colorList.add(Color.from(str));
        }
        return colorList;
    }

    public static void debug(final JavaExtension javaExtension, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            printMsg(javaExtension.getLogger(), placeHolder("[" + javaExtension.getDescription().getName() + "] [Debug] [Version: " + javaExtension.getDescription().getVersion() + "] " + msg, map, false));
        }
    }

    public static void debug(final JavaExtension javaExtension, String... messages) {
        debug(javaExtension, new HashObjectMap<>(), messages);
    }

    public static void debug(final JavaExtension javaExtension, @NotNull List<String> messages) {
        debug(javaExtension, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static void debug(Logger logger, final String name, String version, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            printMsg(logger, placeHolder("[" + name + "] [Debug] [Version: " + version + "] " + msg, map, false));
        }
    }

    public static void debug(Logger logger, final String name, String version, String... messages) {
        debug(logger, name, version, new HashObjectMap<>(), messages);
    }

    public static void debug(Logger logger, final String name, String version, @NotNull List<String> messages) {
        debug(logger, name, version, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static void debug(PluginContainer pluginContainer, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            printMsg(new LoggerWrapper(pluginContainer.logger()), placeHolder("[" + pluginContainer.metadata().name().get() + "] [Debug] [Version: " + pluginContainer.metadata().version().getQualifier() + "] " + msg, map, false));
        }
    }

    public static void debug(PluginContainer pluginContainer, String... messages) {
        debug(pluginContainer, new HashObjectMap<>(), messages);
    }

    public static void debug(PluginContainer pluginContainer, @NotNull List<String> messages) {
        debug(pluginContainer, new HashObjectMap<>(), messages.toArray(new String[0]));
    }


    public static @NotNull String getProgressBar(double current, double max, int totalBars, String symbol, String completedColor,
                                                 String notCompletedColor) {
        final double percent = (float) Math.min(current, max) / max;
        final int progressBars = (int) (totalBars * percent);
        final int leftOver = totalBars - progressBars;

        return colorize(completedColor) +
                String.valueOf(symbol).repeat(Math.max(0, progressBars)) +
                colorize(notCompletedColor) +
                String.valueOf(symbol).repeat(Math.max(0, leftOver));
    }

    /**
     * Register listeners
     *
     * @param pluginContainer Plugin object
     * @param listeners       Class that have the events
     */
    public static <T> void registerListeners(PluginContainer pluginContainer, Object @NotNull ... listeners) {
        for (Object listener : listeners) {
            Sponge.eventManager().registerListeners(pluginContainer, listener);
        }
    }

    /**
     * Kick all players.
     *
     * @param server      The Server instance
     * @param kickMessage The kick message to display.
     * @since v5.0
     */
    public static void kickAll(@NotNull Server server, String kickMessage) {
        server.onlinePlayers().forEach(player -> player.kick(textComponent(colorize(kickMessage))));
    }

    private static boolean join = false;
    private static String disableJoinMessage = "";

    /**
     * Disallow or allow the player login to the server with a custom message.
     *
     * @param b       True -> disallow player login. False -> allow player login.
     * @param message The message to display when the player is disallowed to login.
     * @since v5.0
     */
    public static void disallowLogin(boolean b, String message) {
        join = b;
        disableJoinMessage = message;
    }

    /**
     * @return true if the player login is disallowed or false if the player login is allowed.
     * @since v5.0
     */
    public static boolean isLoginDisallowed() {
        return join;
    }

    /**
     * @return The message to display when the player is disallowed to login.
     * @since v5.0
     */
    public static String getDisallowLoginMessage() {
        return disableJoinMessage;
    }

    /**
     * Build a DiscordWebHook from a yaml file.
     *
     * @param fileConfiguration   The FileConfiguration instance of your config file.
     * @param path                The path in the config.
     * @param embedPlaceHolders   The placeholders of the embeds.
     * @param messagePlaceHolders The placeholders of the message.
     * @return {@link DiscordWebHook} instance.
     */
    public static DiscordWebHook buildDiscordWebHookFromConfig(@NotNull FileConfiguration fileConfiguration, String path, Map<String, String> embedPlaceHolders, Map<String, String> messagePlaceHolders) {
        return new DiscordWebHook(fileConfiguration.getString(path + ".webhook url")).setContent(placeHolder(fileConfiguration.getString(path + ".message"), messagePlaceHolders, true))
                .setAvatarUrl(fileConfiguration.getString(path + ".avatar url"))
                .setUsername(fileConfiguration.getString(path + ".username")).addEmbeds(buildEmbedsFromConfig(fileConfiguration, path + ".embeds", embedPlaceHolders).toArray(new DiscordWebHook.EmbedObject[0]));
    }

    /**
     * Build DiscordWebHook Embeds from a yaml file.
     *
     * @param fileConfiguration The FileConfiguration instance of your config file.
     * @param path              The path in the config.
     * @param placeholders      The placeholders of the embeds.
     * @return A list that contains {@link DiscordWebHook.EmbedObject} objects.
     */
    public static @NotNull List<DiscordWebHook.EmbedObject> buildEmbedsFromConfig(@NotNull FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
        List<DiscordWebHook.EmbedObject> embedObjects = Lists.newArrayList();
        for (String s : fileConfiguration.getConfigurationSection(path).getKeys(false)) {
            embedObjects.add(buildEmbedFromConfig(fileConfiguration, path, placeholders));
        }
        return embedObjects;
    }

    /**
     * Build DiscordWebHook Embed Fields from a yaml file.
     *
     * @param fileConfiguration The FileConfiguration instance of your config file.
     * @param path              The path in the config.
     * @param placeholders      The placeholders of the fields.
     * @return A list that contains {@link DiscordWebHook.EmbedObject.Field} objects.
     */
    public static @NotNull List<DiscordWebHook.EmbedObject.Field> buildFieldsFromConfig(@NotNull FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
        List<DiscordWebHook.EmbedObject.Field> fields = Lists.newArrayList();
        for (String s : fileConfiguration.getConfigurationSection(path).getKeys(false)) {
            fields.add(buildFieldFromConfig(fileConfiguration, path + "." + s, placeholders));
        }
        return fields;
    }

    /**
     * Build DiscordWebHook Embed from a yaml file.
     *
     * @param fileConfiguration The FileConfiguration instance of your config file.
     * @param path              The path in the config.
     * @param placeholders      The placeholders of the embed.
     * @return {@link DiscordWebHook.EmbedObject} instance.
     */
    public static DiscordWebHook.EmbedObject buildEmbedFromConfig(@NotNull FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
        return new DiscordWebHook.EmbedObject().setTitle(placeHolder(fileConfiguration.getString(path + ".title"), placeholders, true))
                .setDescription(placeHolder(fileConfiguration.getString(path + ".description"), placeholders, true))
                .setColor(Color.from(fileConfiguration.getString(path + ".color")))
                .setThumbnail(fileConfiguration.getString(path + ".thumbnail url"))
                .setFooter(placeHolder(fileConfiguration.getString(path + ".footer.message"), placeholders, true),
                        fileConfiguration.getString(path + ".footer.icon url"))
                .setImage(fileConfiguration.getString(path + ".image url"))
                .addFields(buildFieldsFromConfig(fileConfiguration, path + ".fields.", placeholders).toArray(new DiscordWebHook.EmbedObject.Field[0]))
                .setAuthor(fileConfiguration.getString(path + ".author.name"), fileConfiguration.getString(path + ".author.url"),
                        fileConfiguration.getString(path + ".icon url"))
                .setUrl(fileConfiguration.getString(path + ".url"));
    }

    /**
     * Build DiscordWebHook Embed Field from a yaml file.
     *
     * @param fileConfiguration The FileConfiguration instance of your config file.
     * @param path              The path in the config.
     * @param placeholders      The placeholders of the field.
     * @return {@link DiscordWebHook.EmbedObject.Field} instance.
     */
    @Contract("_, _, _ -> new")
    public static DiscordWebHook.EmbedObject.@NotNull Field buildFieldFromConfig(@NotNull FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
        return new DiscordWebHook.EmbedObject.Field(
                placeHolder(fileConfiguration.getString(path + ".name"), placeholders, true),
                placeHolder(fileConfiguration.getString(path + ".message"), placeholders, true),
                fileConfiguration.getBoolean(path + ".inline"));
    }

    /**
     * Check if a username belongs to a premium account
     *
     * @param username player name
     * @return boolean
     */
    public static boolean isUsernamePremium(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return !result.toString().equals("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public enum MinecraftVersion {
        V1_15_R1,
        V1_16_R2,
        V1_16_R3,
        V1_17_R1,
        V1_18_R1,
        V1_18_R2,
        V1_19_R1,
        V1_19_R2,
        UNKNOWN,
        ;

        private static MinecraftVersion currentVersion;

        /**
         * Check if the version is above or equal.
         *
         * @param minecraftVersion The {@link MinecraftVersion} to be checked.
         * @return if the minecraft version is above or equal.
         */
        public boolean isAboveOrEqual(@NotNull MinecraftVersion minecraftVersion) {
            return this.ordinal() >= minecraftVersion.ordinal();
        }

        /**
         * Check if the version is above.
         *
         * @param minecraftVersion The {@link MinecraftVersion} to be checked.
         * @return if the minecraft version is above.
         */
        public boolean isAbove(@NotNull MinecraftVersion minecraftVersion) {
            return this.ordinal() > minecraftVersion.ordinal();
        }

        /**
         * Check if the version is below or equal.
         *
         * @param minecraftVersion The {@link MinecraftVersion} to be checked.
         * @return if the minecraft version is below or equal.
         */
        public boolean isBelowOrEqual(@NotNull MinecraftVersion minecraftVersion) {
            return this.ordinal() <= minecraftVersion.ordinal();
        }

        /**
         * Check if the version is below.
         *
         * @param minecraftVersion The {@link MinecraftVersion} to be checked.
         * @return if the minecraft version is below.
         */
        public boolean isBelow(@NotNull MinecraftVersion minecraftVersion) {
            return this.ordinal() < minecraftVersion.ordinal();
        }

        /**
         * Returns the current minecraft server version.
         *
         * @return the current minecraft server version.
         */
        public static MinecraftVersion getCurrentVersion() {
            return currentVersion;
        }

        @Contract(pure = true)
        public static @NotNull String getCurrentVersionName() {
            return currentVersion.name();
        }

        @Contract(pure = true)
        public static @NotNull String getCurrentVersionNameVtoLowerCase() {
            return currentVersion.name().replace("V", "v");
        }


        static {
            try {
                switch (Sponge.platform().minecraftVersion().name()) {
                    case "1.19.3" -> currentVersion = MinecraftVersion.V1_19_R2;
                    case "1.19.2" -> currentVersion = MinecraftVersion.V1_19_R1;
                    case "1.18.2" -> currentVersion = MinecraftVersion.V1_18_R2;
                    case "1.18.1" -> currentVersion = MinecraftVersion.V1_18_R1;
                    case "1.17", "1.17.1" -> currentVersion = MinecraftVersion.V1_17_R1;
                    case "1.16.5" -> currentVersion = MinecraftVersion.V1_16_R3;
                    case "1.16.4" -> currentVersion = MinecraftVersion.V1_16_R2;
                    case "1.15.2" -> currentVersion = MinecraftVersion.V1_15_R1;
                    default -> currentVersion = UNKNOWN;
                }
            } catch (Exception var2) {
                currentVersion = UNKNOWN;
            }

        }
    }
}
