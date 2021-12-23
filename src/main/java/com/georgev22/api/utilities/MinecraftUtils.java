package com.georgev22.api.utilities;

import com.georgev22.api.colors.Color;
import com.georgev22.api.maps.ObjectMap;
import com.google.common.collect.Lists;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class MinecraftUtils {

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
        input.forEach(MinecraftUtils::broadcastMsg);
    }

    public static void broadcastMsg(final Object input) {
        broadcastMsg(String.valueOf(input));
    }

    public static void printMsg(final @NotNull List<String> input) {
        input.forEach(MinecraftUtils::printMsg);
    }

    public static void printMsg(final Object input) {
        printMsg(String.valueOf(input));
    }


    public static void msg(final CommandSender target, final String message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, Utils.placeHolder(message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, Utils.placeHolder(message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final String[] message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, Utils.placeHolder(message, map, ignoreCase));
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
        Validate.notNull(msg, "The string can't be null!");
        return ChatColor.translateAlternateColorCodes('&', msg);
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
        newColl.replaceAll(MinecraftUtils::colorize);
        return newColl;
    }

    public static @NotNull List<String> stripColor(final List<String> coll) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(MinecraftUtils::stripColor);
        return newColl;
    }

    public static void debug(final JavaPlugin plugin, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            MinecraftUtils.printMsg(Utils.placeHolder("[" + plugin.getDescription().getName() + "] [Debug] [Version: " + plugin.getDescription().getVersion() + "] " + msg, map, false));
        }
    }

    public static void debug(final JavaPlugin plugin, String... messages) {
        debug(plugin, null, messages);
    }

    public static void debug(final JavaPlugin plugin, @NotNull List<String> messages) {
        debug(plugin, null, messages.toArray(new String[0]));
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

        final StringBuilder sb = new StringBuilder();

        sb.append(colorize(completedColor));
        for (int i = 0; i < progressBars; i++) {
            sb.append(symbol);
        }

        sb.append(colorize(notCompletedColor));
        for (int i = 0; i < leftOver; i++) {
            sb.append(symbol);
        }
        return sb.toString();
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

    /**
     * Register a command given an executor and a name.
     *
     * @param commandName The name of the command
     * @param command     The class that extends the BukkitCommand class
     */
    public static void registerCommand(final String commandName, final Command command) {
        try {
            Field field = Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object result = field.get(Bukkit.getServer().getPluginManager());
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            commandMap.register(commandName, command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * unregister a command
     *
     * @param commandName The name of the command
     */
    public static void unRegisterCommand(String commandName) {
        try {
            Object result = Utils.Reflection.fetchField(Bukkit.getServer().getPluginManager().getClass(), Bukkit.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) result;
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

        static {
            try {
                currentVersion = MinecraftVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].toUpperCase());
            } catch (Exception var2) {
                currentVersion = UNKNOWN;
            }

        }
    }

    public static class MinecraftReflection {

        private static final String NET_MINECRAFT_PACKAGE = "net.minecraft";
        public static final String ORG_BUKKIT_CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
        public static final String NET_MINECRAFT_SERVER_PACKAGE = NET_MINECRAFT_PACKAGE + ".server";

        private static volatile Object theUnsafe;

        public static boolean isRepackaged() {
            return MinecraftVersion.getCurrentVersion().isBelowOrEqual(MinecraftVersion.V1_17_R1);
        }

        @Contract(pure = true)
        public static @NotNull String getNMSClassName(String className) {
            return NET_MINECRAFT_SERVER_PACKAGE + '.' + MinecraftVersion.getCurrentVersionName() + '.' + className;
        }

        public static @NotNull Class<?> getNMSClass(String className) throws ClassNotFoundException {
            return Class.forName(getNMSClassName(className));
        }

        public static Optional<Class<?>> getNMSOptionalClass(String className) {
            return Utils.Reflection.optionalClass(getNMSClassName(className), Bukkit.class.getClassLoader());
        }

        @Contract(pure = true)
        public static @NotNull String getOBCClassName(String className) {
            return ORG_BUKKIT_CRAFTBUKKIT_PACKAGE + '.' + MinecraftVersion.getCurrentVersionName() + '.' + className;
        }

        public static @NotNull Class<?> getOBCClass(String className) throws ClassNotFoundException {
            return Class.forName(getOBCClassName(className));
        }

        public static Optional<Class<?>> getOBCOptionalClass(String className) {
            return Utils.Reflection.optionalClass(getOBCClassName(className), Bukkit.class.getClassLoader());
        }
    }

}
