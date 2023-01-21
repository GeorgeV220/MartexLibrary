package com.georgev22.library.minecraft;

import com.georgev22.library.extensions.java.JavaExtension;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.utilities.Color;
import com.georgev22.library.utilities.DiscordWebHook;
import com.georgev22.library.utilities.Utils;
import com.google.common.collect.Lists;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BukkitMinecraftUtils {

    public static boolean isList(final @NotNull FileConfiguration file, final String path) {
        return Utils.isList(file.get(path));
    }

    public static void broadcastMsg(final String input) {
        Bukkit.broadcastMessage(colorize(input));
    }

    public static void printMsg(final String input) {
        Bukkit.getConsoleSender().sendMessage(colorize(input));
    }


    public static void broadcastMsg(final @NotNull List<String> input) {
        input.forEach(BukkitMinecraftUtils::broadcastMsg);
    }

    public static void broadcastMsg(final @NotNull String... input) {
        Arrays.stream(input).forEach(BukkitMinecraftUtils::broadcastMsg);
    }

    public static void broadcastMsg(final Object input) {
        broadcastMsg(String.valueOf(input));
    }

    public static void printMsg(final @NotNull List<String> input) {
        input.forEach(BukkitMinecraftUtils::printMsg);
    }

    public static void printMsg(final @NotNull String... input) {
        Arrays.stream(input).forEach(BukkitMinecraftUtils::printMsg);
    }

    public static void printMsg(final Object input) {
        printMsg(String.valueOf(input));
    }


    public static void msg(final CommandSender target, final String message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeholderAPI(target, message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeholderAPI(target, message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final String[] message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeholderAPI(target, message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final FileConfiguration file, final String path) {
        msg(target, file, path, null, false);
    }

    public static void msg(final CommandSender target, final FileConfiguration file, final String path,
                           final Map<String, String> map, final boolean replace) {
        Validate.notNull(file, "The file can't be null");
        Validate.notNull(file, "The path can't be null");

        if (!file.isSet(path)) {
            throw new IllegalArgumentException("The path: " + path + " doesn't exist.");
        }

        if (isList(file, path)) {
            msg(target, file.getStringList(path), map, replace);
        } else {
            msg(target, file.getString(path), map, replace);
        }
    }

    public static void msg(final CommandSender target, final String message) {
        Validate.notNull(target, "The target can't be null");
        if (message == null) {
            return;
        }
        target.sendMessage(colorize(message));
    }

    public static void msg(final CommandSender target, final String... message) {
        Validate.notNull(target, "The target can't be null");
        if (message == null || message.length == 0) {
            return;
        }
        Validate.noNullElements(message, "The string array can't have null elements.");
        target.sendMessage(colorize(message));
    }

    public static void msg(final CommandSender target, final List<String> message) {
        Validate.notNull(target, "The target can't be null");
        if (message == null || message.isEmpty()) {
            return;
        }
        Validate.noNullElements(message, "The list can't have null elements.");
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
        Validate.notNull(unEditedMessage, "The string can't be null!");
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
        return ChatColor.translateAlternateColorCodes('&', unEditedMessage);
    }

    public static String stripColor(final String msg) {
        Validate.notNull(msg, "The string can't be null!");
        return ChatColor.stripColor(msg);
    }

    /**
     * Returns a translated string array.
     *
     * @param array Array of messages
     * @return A translated message array
     */
    public static String @NotNull [] colorize(final String... array) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = colorize(newarr[i]);
        }
        return newarr;
    }

    public static String @NotNull [] stripColor(final String... array) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = stripColor(newarr[i]);
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
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(BukkitMinecraftUtils::colorize);
        return newColl;
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

    public static @NotNull List<String> stripColor(final List<String> coll) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(BukkitMinecraftUtils::stripColor);
        return newColl;
    }

    public static void debug(final JavaExtension javaExtension, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            BukkitMinecraftUtils.printMsg(Utils.placeHolder("[" + javaExtension.getDescription().getName() + "] [Debug] [Version: " + javaExtension.getDescription().getVersion() + "] " + msg, map, false));
        }
    }

    public static void debug(final JavaExtension javaExtension, String... messages) {
        debug(javaExtension, new HashObjectMap<>(), messages);
    }

    public static void debug(final JavaExtension javaExtension, @NotNull List<String> messages) {
        debug(javaExtension, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static void debug(final String name, String version, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            BukkitMinecraftUtils.printMsg(Utils.placeHolder("[" + name + "] [Debug] [Version: " + version + "] " + msg, map, false));
        }
    }

    public static void debug(final String name, String version, String... messages) {
        debug(name, version, new HashObjectMap<>(), messages);
    }

    public static void debug(final String name, String version, @NotNull List<String> messages) {
        debug(name, version, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static void debug(final JavaPlugin plugin, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            BukkitMinecraftUtils.printMsg(Utils.placeHolder("[" + plugin.getDescription().getName() + "] [Debug] [Version: " + plugin.getDescription().getVersion() + "] " + msg, map, false));
        }
    }

    public static void debug(final JavaPlugin plugin, String... messages) {
        debug(plugin, new HashObjectMap<>(), messages);
    }

    public static void debug(final JavaPlugin plugin, @NotNull List<String> messages) {
        debug(plugin, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static ItemStack @NotNull [] getItems(final @NotNull ItemStack item, int amount) {

        final int maxSize = item.getMaxStackSize();
        if (amount <= maxSize) {
            item.setAmount(Math.max(amount, 1));
            return new ItemStack[]{item};
        }
        final List<ItemStack> resultItems = Lists.newArrayList();
        do {
            item.setAmount(Math.min(amount, maxSize));
            resultItems.add(new ItemStack(item));
            amount = amount >= maxSize ? amount - maxSize : 0;
        } while (amount != 0);
        return resultItems.toArray(new ItemStack[0]);
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

    public static @NotNull ItemStack resetItemMeta(final @NotNull ItemStack item) {
        final ItemStack copy = item.clone();
        copy.setItemMeta(Bukkit.getItemFactory().getItemMeta(copy.getType()));
        return copy;
    }

    /**
     * Register listeners
     *
     * @param listeners Class that implements Listener interface
     */
    public static void registerListeners(Plugin plugin, Listener @NotNull ... listeners) {
        final PluginManager pm = Bukkit.getPluginManager();
        for (final Listener listener : listeners) {
            pm.registerEvents(listener, plugin);
        }
    }

    public static @Nullable SimpleCommandMap getSimpleCommandMap() {
        try {
            Field field = Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object result = field.get(Bukkit.getServer().getPluginManager());
            return (SimpleCommandMap) result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register a command given an executor and a name.
     *
     * @param commandName The name of the command
     * @param command     The class that extends the BukkitCommand class
     */
    public static void registerCommand(final String commandName, final Command command) {
        registerCommand(commandName, commandName, command);
    }

    /**
     * Register a command given an executor and a name.
     *
     * @param commandName The name of the command
     * @param prefix      The prefix in front of the command
     * @param command     The class that extends the BukkitCommand class
     */
    public static void registerCommand(final String commandName, final String prefix, final Command command) {
        getSimpleCommandMap().register(commandName, prefix, command);
    }

    /**
     * unregister a command
     *
     * @param commandName The name of the command
     */
    public static void unRegisterCommand(String commandName) {
        try {
            SimpleCommandMap simpleCommandMap = getSimpleCommandMap();
            Object map = Utils.Reflection.fetchField(MinecraftVersion.getCurrentVersion().isBelowOrEqual(MinecraftVersion.V1_12_R1) ? simpleCommandMap.getClass() : simpleCommandMap.getClass().getSuperclass(), simpleCommandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            Command command = simpleCommandMap.getCommand(commandName);
            knownCommands.remove(command.getName());
            for (String alias : command.getAliases()) {
                knownCommands.remove(alias);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * unregister a command
     *
     * @param commandName The name of the command
     */
    public static void unregisterCommand(String commandName) {
        getSimpleCommandMap().getCommand(commandName).unregister(getSimpleCommandMap());
    }


    /**
     * Run the commands from config
     *
     * @param s Command to run
     * @since v5.0
     */
    public static void runCommand(Plugin plugin, String s) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (s == null)
                return;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        });
    }

    /**
     * Kick all players.
     *
     * @param kickMessage The kick message to display.
     * @since v5.0
     */
    public static void kickAll(Plugin plugin, String kickMessage) {
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(colorize(kickMessage))));
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
        return new DiscordWebHook(fileConfiguration.getString(path + ".webhook url")).setContent(Utils.placeHolder(fileConfiguration.getString(path + ".message"), messagePlaceHolders, true))
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
        return new DiscordWebHook.EmbedObject().setTitle(Utils.placeHolder(fileConfiguration.getString(path + ".title"), placeholders, true))
                .setDescription(Utils.placeHolder(fileConfiguration.getString(path + ".description"), placeholders, true))
                .setColor(Color.from(fileConfiguration.getString(path + ".color")))
                .setThumbnail(fileConfiguration.getString(path + ".thumbnail url"))
                .setFooter(Utils.placeHolder(fileConfiguration.getString(path + ".footer.message"), placeholders, true),
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
                Utils.placeHolder(fileConfiguration.getString(path + ".name"), placeholders, true),
                Utils.placeHolder(fileConfiguration.getString(path + ".message"), placeholders, true),
                fileConfiguration.getBoolean(path + ".inline"));
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
    public static DiscordWebHook buildDiscordWebHookFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, String path, Map<String, String> embedPlaceHolders, Map<String, String> messagePlaceHolders) {
        return new DiscordWebHook(fileConfiguration.getString(path + ".webhook url")).setContent(Utils.placeHolder(fileConfiguration.getString(path + ".message"), messagePlaceHolders, true))
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
    public static @NotNull List<DiscordWebHook.EmbedObject> buildEmbedsFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
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
    public static @NotNull List<DiscordWebHook.EmbedObject.Field> buildFieldsFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
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
    public static DiscordWebHook.EmbedObject buildEmbedFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
        return new DiscordWebHook.EmbedObject().setTitle(Utils.placeHolder(fileConfiguration.getString(path + ".title"), placeholders, true))
                .setDescription(Utils.placeHolder(fileConfiguration.getString(path + ".description"), placeholders, true))
                .setColor(Color.from(fileConfiguration.getString(path + ".color")))
                .setThumbnail(fileConfiguration.getString(path + ".thumbnail url"))
                .setFooter(Utils.placeHolder(fileConfiguration.getString(path + ".footer.message"), placeholders, true),
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
    public static DiscordWebHook.EmbedObject.@NotNull Field buildFieldFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, String path, Map<String, String> placeholders) {
        return new DiscordWebHook.EmbedObject.Field(
                Utils.placeHolder(fileConfiguration.getString(path + ".name"), placeholders, true),
                Utils.placeHolder(fileConfiguration.getString(path + ".message"), placeholders, true),
                fileConfiguration.getBoolean(path + ".inline"));
    }

    /**
     * Gets a list of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to ItemStack list.
     * @return ItemStack list created from the Base64 string.
     */
    @Contract("null -> new")
    public static @Nullable List<ItemStack> itemStackListFromBase64(String data) {
        if (data == null || data.isEmpty()) {
            return Lists.newArrayList();
        }
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return Arrays.asList(items);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A method to serialize an {@link ItemStack} list to Base64 String.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     */
    public static @NotNull String itemStackListToBase64(List<ItemStack> items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.size());

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
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

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param target     target for the placeholders.
     * @param str        the input string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string with the placeholders replaced
     */
    public static String placeholderAPI(final CommandSender target, String str, final Map<String, String> map, final boolean ignoreCase) {
        Validate.notNull(target, "The target can't be null!");
        Validate.notNull(str, "The string can't be null!");
        if (map == null) {
            return str;
        }
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            str = ignoreCase ? Utils.replaceIgnoreCase(str, entry.getKey(), entry.getValue())
                    : str.replace(entry.getKey(), entry.getValue());
        }
        try {
            if (target instanceof OfflinePlayer offlinePlayer) {
                return me.clip.placeholderapi.PlaceholderAPI.setBracketPlaceholders(offlinePlayer, str);
            }
            return str;
        } catch (Throwable error) {
            return str;
        }
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param target     target for the placeholders.
     * @param array      the input array of string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string array with the placeholders replaced
     */
    public static String @NotNull [] placeholderAPI(final CommandSender target, final String[] array, final Map<String, String> map, final boolean ignoreCase) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newArray = Arrays.copyOf(array, array.length);
        if (map == null) {
            return newArray;
        }
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = placeholderAPI(target, newArray[i], map, ignoreCase);
        }
        return newArray;
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param target     target for the placeholders.
     * @param coll       the input string list to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string list with the placeholders replaced
     */
    public static List<String> placeholderAPI(final CommandSender target, final List<String> coll, final Map<String, String> map,
                                              final boolean ignoreCase) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        return map == null ? coll
                : coll.stream().map(str -> placeholderAPI(target, str, map, ignoreCase)).collect(Collectors.toList());
    }

    public enum MinecraftVersion {
        V_1_7_R1,
        V_1_7_R2,
        V_1_7_R3,
        V_1_7_R4,
        V1_8_R1,
        V1_8_R2,
        V1_8_R3,
        V1_9_R1,
        V1_9_R2,
        V1_10_R1,
        V1_11_R1,
        V1_12_R1,
        V1_13_R1,
        V1_13_R2,
        V1_14_R1,
        V1_15_R1,
        V1_16_R1,
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
                currentVersion = MinecraftVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].toUpperCase());
            } catch (Exception var2) {
                currentVersion = UNKNOWN;
            }

        }
    }

    public static class MinecraftReflection {

        public static final String NET_MINECRAFT_PACKAGE = "net.minecraft";
        public static final String ORG_BUKKIT_CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
        public static final String NET_MINECRAFT_SERVER_PACKAGE = NET_MINECRAFT_PACKAGE + ".server";

        private static volatile Object theUnsafe;

        public static boolean isRepackaged() {
            return MinecraftVersion.getCurrentVersion().isAboveOrEqual(MinecraftVersion.V1_17_R1);
        }

        @Contract(pure = true)
        public static @NotNull String getNMSClassName(String className) {
            return NET_MINECRAFT_SERVER_PACKAGE + '.' + MinecraftVersion.getCurrentVersionNameVtoLowerCase() + '.' + className;
        }

        public static @NotNull Class<?> getNMSClass(String className) throws ClassNotFoundException {
            return Class.forName(getNMSClassName(className));
        }

        public static Optional<Class<?>> getNMSOptionalClass(String className) {
            return Utils.Reflection.optionalClass(getNMSClassName(className), Bukkit.class.getClassLoader());
        }

        public static @NotNull String getNMSClassName(String className, String fullClassName) {
            return isRepackaged() ? fullClassName : getNMSClassName(className);
        }

        public static @NotNull Class<?> getNMSClass(String className, String fullClassName) throws ClassNotFoundException {
            return isRepackaged() ? Class.forName(fullClassName) : getNMSClass(className);
        }

        public static Optional<Class<?>> getNMSOptionalClass(String className, String fullClassName) {
            return isRepackaged() ? Utils.Reflection.optionalClass(fullClassName, Bukkit.class.getClassLoader()) : getNMSOptionalClass(className);
        }

        @Contract(pure = true)
        public static @NotNull String getOBCClassName(String className) {
            return ORG_BUKKIT_CRAFTBUKKIT_PACKAGE + '.' + MinecraftVersion.getCurrentVersionNameVtoLowerCase() + '.' + className;
        }

        public static @NotNull Class<?> getOBCClass(String className) throws ClassNotFoundException {
            return Class.forName(getOBCClassName(className));
        }

        public static Optional<Class<?>> getOBCOptionalClass(String className) {
            return Utils.Reflection.optionalClass(getOBCClassName(className), Bukkit.class.getClassLoader());
        }
    }

    public static class SerializableLocation implements ConfigurationSerializable, com.georgev22.library.yaml.serialization.ConfigurationSerializable {

        /**
         * Name of the world
         */
        private final String world;
        /**
         * UID of the world
         */
        private final String uuid;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        private transient WeakReference<Location> weakLoc;

        /**
         * Constructs a {@link SerializableLocation} with the information of
         * the given {@link Location}.
         *
         * @param location {@link Location} to be serialized
         */
        public SerializableLocation(@NotNull Location location) {
            this.world = location.getWorld().getName();
            this.uuid = location.getWorld().getUID().toString();
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
            this.yaw = location.getYaw();
            this.pitch = location.getPitch();
        }

        /**
         * Constructs a {@link SerializableLocation} with the given
         * information. This constructor
         * is meant to be used if {@link SerializableLocation} is serialized in
         * another
         * {@link ConfigurationSerializable} class.
         *
         * @param map {@link Map} that contains the necessary data
         */
        public SerializableLocation(@NotNull Map<String, Object> map) {
            this.world = (String) map.get("world");
            this.uuid = (String) map.get("uuid");
            this.x = (Double) map.get("x");
            this.y = (Double) map.get("y");
            this.z = (Double) map.get("z");
            this.yaw = (Float) map.get("yaw");
            this.pitch = (Float) map.get("pitch");
        }

        /**
         * Restores from a map back into the class. Used with
         * {@link ConfigurationSerializable}.
         *
         * @param map a {@link Map} which represents a {@link SerializableLocation}
         * @return A {@link SerializableLocation}
         */
        @Contract("_ -> new")
        @NotNull
        public static SerializableLocation deserialize(@NotNull Map<String, Object> map) {
            World world = null;
            if (map.containsKey("world")) {
                world = Bukkit.getWorld((String) map.get("world")) != null ? Bukkit.getWorld((String) map.get("world")) : Bukkit.getWorld(UUID.fromString((String) map.get("uuid")));
                if (world == null) {
                    throw new IllegalStateException("Cannot find world by UUID or name");
                }
            }

            return new SerializableLocation(new Location(world, NumberConversions.toDouble(map.get("x")), NumberConversions.toDouble(map.get("y")), NumberConversions.toDouble(map.get("z")), NumberConversions.toFloat(map.get("yaw")), NumberConversions.toFloat(map.get("pitch"))));
        }

        /**
         * Serialize this {@link SerializableLocation} into a Map which contain
         * the values of
         * this class
         *
         * @return {@link Map} object
         */
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("world", this.world);
            map.put("uuid", this.uuid);
            map.put("x", this.x);
            map.put("y", this.y);
            map.put("z", this.z);
            map.put("yaw", this.yaw);
            map.put("pitch", this.pitch);
            return map;
        }

        /**
         * Resolves the {@link World} on the {@link org.bukkit.Server}, as a proper location has a reference to
         * the world it belongs to.
         *
         * @return {@link Location} represented
         */
        public Location getLocation() {
            if (weakLoc == null || weakLoc.get() == null) {
                World world = Bukkit.getWorld(this.uuid);
                if (world == null) {
                    Logger.getLogger(this.getClass().getName()).warning("World UUID not found, falling back to World Name");
                    world = Bukkit.getWorld(this.world);
                }
                if (world == null) {
                    throw new IllegalStateException("Cannot find world by UUID or name");
                }
                weakLoc = new WeakReference<>(new Location(world, x, y, z, yaw, pitch));
            }
            return weakLoc.get();
        }
    }

}
