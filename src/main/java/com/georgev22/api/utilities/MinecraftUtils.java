package com.georgev22.api.utilities;

import com.georgev22.api.colors.Color;
import com.georgev22.api.maps.ObjectMap;
import com.google.common.collect.Lists;
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinecraftUtils {

    public static boolean isList(final FileConfiguration file, final String path) {
        return Utils.isList(file.get(path));
    }

    public static void broadcastMsg(final String input) {
        Bukkit.broadcastMessage(colorize(input));
    }

    public static void printMsg(final String input) {
        Bukkit.getConsoleSender().sendMessage(colorize(input));
    }


    public static void broadcastMsg(final List<String> input) {
        input.forEach(MinecraftUtils::broadcastMsg);
    }

    public static void broadcastMsg(final Object input) {
        broadcastMsg(String.valueOf(input));
    }

    public static void printMsg(final List<String> input) {
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
    public static String colorize(final String msg) {
        Validate.notNull(msg, "The string can't be null!");
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String unColorize(final String msg) {
        Validate.notNull(msg, "The string can't be null!");
        return ChatColor.stripColor(msg);
    }

    /**
     * Returns a translated string array.
     *
     * @param array Array of messages
     * @return A translated message array
     */
    public static String[] colorize(final String... array) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = colorize(newarr[i]);
        }
        return newarr;
    }

    public static String[] unColorize(final String... array) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = unColorize(newarr[i]);
        }
        return newarr;
    }

    /**
     * Returns a translated string collection.
     *
     * @param coll The collection to be translated
     * @return A translated message
     */
    public static List<String> colorize(final List<String> coll) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(MinecraftUtils::colorize);
        return newColl;
    }

    public static List<String> unColorize(final List<String> coll) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(MinecraftUtils::unColorize);
        return newColl;
    }

    public static void debug(final JavaPlugin plugin, final Map<String, String> map, String... messages) {
        for (final String msg : messages) {
            MinecraftUtils.printMsg(Utils.placeHolder("[" + plugin.getDescription().getName() + "] [Debug] [Version: " + plugin.getDescription().getVersion() + "] " + msg, map, false));
        }
    }

    public static void debug(final JavaPlugin plugin, String... messages) {
        debug(plugin, null, messages);
    }

    public static void debug(final JavaPlugin plugin, List<String> messages) {
        debug(plugin, null, messages.toArray(new String[0]));
    }

    public static ItemStack[] getItems(final ItemStack item, int amount) {

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


    public static String getProgressBar(double current, double max, int totalBars, String symbol, String completedColor,
                                        String notCompletedColor) {
        final double percent = (float) Math.min(current, max) / max;
        final int progressBars = (int) (totalBars * percent);
        final int leftOver = totalBars - progressBars;

        return colorize(completedColor) +
                String.valueOf(symbol).repeat(Math.max(0, progressBars)) +
                colorize(notCompletedColor) +
                String.valueOf(symbol).repeat(Math.max(0, leftOver));
    }

    public static ItemStack resetItemMeta(final ItemStack item) {
        final ItemStack copy = item.clone();
        copy.setItemMeta(Bukkit.getItemFactory().getItemMeta(copy.getType()));
        return copy;
    }

    /**
     * Register listeners
     *
     * @param listeners Class that implements Listener interface
     */
    public static void registerListeners(Plugin plugin, Listener... listeners) {
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
            Field field1 = Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            field1.setAccessible(true);
            Object result = field1.get(Bukkit.getServer().getPluginManager());
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Field field = MinecraftVersion.getCurrentVersion().isBelowOrEqual(MinecraftVersion.V1_12_R1) ? commandMap.getClass().getDeclaredField("knownCommands") : commandMap.getClass().getSuperclass().getDeclaredField("knownCommands");
            field.setAccessible(true);
            Object map = field.get(commandMap);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            Command command = commandMap.getCommand(commandName);
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

    public static DiscordWebHook.EmbedObject buildFromConfig(FileConfiguration fileConfiguration, String path, ObjectMap<String, String> placeholders) {
        return new DiscordWebHook.EmbedObject().setTitle(Utils.placeHolder(fileConfiguration.getString(path + ".title"), placeholders, true))
                .setDescription(Utils.placeHolder(fileConfiguration.getString(path + ".description"), placeholders, true))
                .setColor(Color.from(fileConfiguration.getString(path + ".color")))
                .setThumbnail(fileConfiguration.getString(path + ".thumbnail url"))
                .setFooter(Utils.placeHolder(fileConfiguration.getString(path + ".footer.message"), placeholders, true),
                        fileConfiguration.getString(path + ".footer.icon url"))
                .setImage(fileConfiguration.getString(path + ".image url"))
                .setAuthor(fileConfiguration.getString(path + ".author.name"), fileConfiguration.getString(path + ".author.url"),
                        fileConfiguration.getString(path + ".icon url"))
                .setUrl(fileConfiguration.getString(path + ".url"));
    }


    public enum MinecraftVersion {
        UNKNOWN,
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
        V1_17_R1;

        private static MinecraftVersion currentVersion;

        public boolean isAboveOrEqual(MinecraftVersion minecraftVersion) {
            return this.ordinal() >= minecraftVersion.ordinal();
        }

        public boolean isAbove(MinecraftVersion minecraftVersion) {
            return this.ordinal() > minecraftVersion.ordinal();
        }

        public boolean isBelowOrEqual(MinecraftVersion minecraftVersion) {
            return this.ordinal() <= minecraftVersion.ordinal();
        }

        public boolean isBelow(MinecraftVersion minecraftVersion) {
            return this.ordinal() < minecraftVersion.ordinal();
        }

        public static MinecraftVersion getCurrentVersion() {
            return currentVersion;
        }

        static {
            try {
                currentVersion = MinecraftVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].toUpperCase());
            } catch (Exception var2) {
                currentVersion = UNKNOWN;
            }

        }
    }
}
