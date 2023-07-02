package com.georgev22.library.minecraft.inventory;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.minecraft.inventory.utils.actions.Action;
import com.georgev22.library.minecraft.inventory.utils.actions.ActionManager;
import com.georgev22.library.utilities.Utils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.georgev22.library.utilities.Utils.Assertions.notNull;

public class ItemBuilder {
    private final ItemStack itemStack;
    private Material material;
    private Short durability;
    private String title;
    private int amount;
    private final List<String> lores;
    private final List<ItemFlag> flags;
    private final ObjectMap<Enchantment, Integer> enchantments;
    private boolean unbreakable;
    private final NBTItem nbtItem;

    public ItemBuilder(@NotNull XMaterial material) {
        this(material.parseMaterial());
    }

    public ItemBuilder(@NotNull XMaterial material, boolean showAllAttributes) {
        this(material.parseMaterial(), showAllAttributes);
    }

    public ItemBuilder(Material material) {
        this(material, true);
    }

    public ItemBuilder(Material material, boolean showAllAttributes) {
        this.amount = 1;
        this.lores = Lists.newArrayList();
        this.flags = Lists.newArrayList();
        this.enchantments = ObjectMap.newHashObjectMap();
        this.unbreakable = false;
        Preconditions.checkArgument(material != null, "ItemStack cannot be null");
        this.itemStack = new ItemStack(material);
        this.showAllAttributes(showAllAttributes);
        this.nbtItem = new NBTItem(itemStack, true);
    }

    public ItemBuilder(ItemStack itemStack) {
        this(itemStack, true);
    }

    public ItemBuilder(ItemStack itemStack, boolean showAllAttributes) {
        this.amount = 1;
        this.lores = Lists.newArrayList();
        this.flags = Lists.newArrayList();
        this.enchantments = ObjectMap.newHashObjectMap();
        this.unbreakable = false;
        Preconditions.checkArgument(itemStack != null, "ItemStack cannot be null");
        this.itemStack = itemStack;
        this.showAllAttributes(showAllAttributes);
        this.nbtItem = new NBTItem(itemStack, true);
    }

    public static ItemBuilder buildItemFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path) {
        return buildItemFromConfig(fileConfiguration, path, ObjectMap.newHashObjectMap(), ObjectMap.newHashObjectMap());
    }

    public static ItemBuilder buildItemFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements) {
        return buildItemFromConfig(fileConfiguration, path, loresReplacements, ObjectMap.newHashObjectMap());
    }

    public static ItemBuilder buildItemFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements, @NotNull ObjectMap<String, String> titleReplacements) {
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null) {
            return new ItemBuilder(Material.PAPER).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!"));
        }
        return new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial())
                .amount(fileConfiguration.getInt(path + ".amount"))
                .title(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getString(path + ".title"), notNull("titleReplacements", titleReplacements), true)))
                .lores(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getStringList(path + ".lores"), notNull("loresReplacements", loresReplacements), true)))
                .showAllAttributes(fileConfiguration.getBoolean(path + ".show all attributes"))
                .glow(fileConfiguration.getBoolean(path + ".glow"))
                .colors(fileConfiguration.getBoolean(path + ".animated") ? (fileConfiguration.getBoolean(path + ".random colors") ? Utils.randomColors(3) : fileConfiguration.getStringList(path + ".colors")) : Lists.newArrayList("NOT ANIMATED"))
                .animation(fileConfiguration.getString(path + ".animation"))
                .commands(buildItemCommandFromConfig(fileConfiguration, path))
                .frames(buildFramesFromConfig(fileConfiguration, path, loresReplacements, titleReplacements));
    }

    public static ItemBuilder buildSimpleItemFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path) {
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null) {
            return new ItemBuilder(Material.PAPER).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!"));
        }
        return new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial())
                .amount(fileConfiguration.getInt(path + ".amount"));
    }

    public static ItemBuilder buildSimpleItemFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements, @NotNull ObjectMap<String, String> titleReplacements) {
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null || fileConfiguration.get(path + ".amount") == null) {
            return new ItemBuilder(Material.PAPER).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!"));
        }
        if (fileConfiguration.get(path + ".title") == null || fileConfiguration.get(path + ".lores") == null || fileConfiguration.get(path + ".enchantments") == null)
            return buildSimpleItemFromConfig(fileConfiguration, path);
        return new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial())
                .amount(fileConfiguration.getInt(path + ".amount"))
                .title(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getString(path + ".title"), notNull("titleReplacements", titleReplacements), true)))
                .lores(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getStringList(path + ".lores"), notNull("loresReplacements", loresReplacements), true)))
                .enchantment(fileConfiguration.getStringList(path + ".enchantments"))
                ;
    }

    private static @NotNull List<ItemCommand> buildItemCommandFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path) {
        List<ItemCommand> itemCommands = Lists.newArrayList();
        if (fileConfiguration.getConfigurationSection(path + ".commands") != null && !Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".commands")).getKeys(true).isEmpty()) {
            fileConfiguration.getStringList(path + ".commands.RIGHT");
            if (!fileConfiguration.getStringList(path + ".commands.RIGHT").isEmpty()) {
                ItemCommand itemCommand = new ItemCommand(ItemCommandType.RIGHT, fileConfiguration.getInt(path + ".commands cooldown.RIGHT"), fileConfiguration.getStringList(path + ".commands.RIGHT"));
                itemCommands.add(itemCommand);
            }
            fileConfiguration.getStringList(path + ".commands.LEFT");
            if (!fileConfiguration.getStringList(path + ".commands.LEFT").isEmpty()) {
                ItemCommand itemCommand = new ItemCommand(ItemCommandType.LEFT, fileConfiguration.getInt(path + ".commands cooldown.LEFT"), fileConfiguration.getStringList(path + ".commands.LEFT"));
                itemCommands.add(itemCommand);
            }
            fileConfiguration.getStringList(path + ".commands.MIDDLE");
            if (!fileConfiguration.getStringList(path + ".commands.MIDDLE").isEmpty()) {
                ItemCommand itemCommand = new ItemCommand(ItemCommandType.MIDDLE, fileConfiguration.getInt(path + ".commands cooldown.MIDDLE"), fileConfiguration.getStringList(path + ".commands.MIDDLE"));
                itemCommands.add(itemCommand);
            }
        }
        return itemCommands;
    }

    public static @NotNull List<ItemStack> buildFramesFromConfig(@NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements, @NotNull ObjectMap<String, String> titleReplacements) {
        List<ItemStack> itemStacks = Lists.newArrayList();
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null) {
            return Lists.newArrayList(new ItemBuilder(Material.ANVIL).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!")).build());
        }
        if (fileConfiguration.getConfigurationSection(path + ".frames") != null && !Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".frames")).getKeys(false).isEmpty()) {
            itemStacks.add(new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial()).build());
            for (String b : Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".frames")).getKeys(false)) {
                itemStacks.add(new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".frames." + b + ".item")).parseMaterial()).build());
            }
        }
        return itemStacks;
    }

    public static @NotNull List<Action> buildActionsFromConfig(@NotNull ActionManager actionManager, @NotNull com.georgev22.library.yaml.file.FileConfiguration fileConfiguration, @NotNull String path, @NotNull Action action) {
        List<Action> actions = Lists.newArrayList();
        if (fileConfiguration.get(path) == null) {
            return actions;
        }
        if (!fileConfiguration.isSet(path + ".actions")) {
            return actions;
        }
        com.georgev22.library.yaml.ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(path + ".actions");
        if (configurationSection == null) {
            return actions;
        }

        if (!configurationSection.getKeys(false).isEmpty()) {
            for (String key : Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".actions")).getKeys(false)) {
                actions.add(actionManager.addAction(
                                action,
                                ObjectMap.Pair.create(
                                        key,
                                        fileConfiguration.getStringList(path + ".actions." + key).stream()
                                                .map(str -> (Object) str)
                                                .toList()
                                )
                        )
                );
            }
        }

        return actions;
    }


    public static ItemBuilder buildItemFromConfig(@NotNull FileConfiguration fileConfiguration, @NotNull String path) {
        return buildItemFromConfig(fileConfiguration, path, ObjectMap.newHashObjectMap(), ObjectMap.newHashObjectMap());
    }

    public static ItemBuilder buildItemFromConfig(@NotNull FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements) {
        return buildItemFromConfig(fileConfiguration, path, loresReplacements, ObjectMap.newHashObjectMap());
    }

    public static ItemBuilder buildItemFromConfig(@NotNull FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements, @NotNull ObjectMap<String, String> titleReplacements) {
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null) {
            return new ItemBuilder(Material.PAPER).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!"));
        }
        return new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial())
                .amount(fileConfiguration.getInt(path + ".amount"))
                .title(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getString(path + ".title"), notNull("titleReplacements", titleReplacements), true)))
                .lores(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getStringList(path + ".lores"), notNull("loresReplacements", loresReplacements), true)))
                .showAllAttributes(fileConfiguration.getBoolean(path + ".show all attributes"))
                .glow(fileConfiguration.getBoolean(path + ".glow"))
                .colors(fileConfiguration.getBoolean(path + ".animated") ? (fileConfiguration.getBoolean(path + ".random colors") ? Utils.randomColors(3) : fileConfiguration.getStringList(path + ".colors")) : Lists.newArrayList("NOT ANIMATED"))
                .animation(fileConfiguration.getString(path + ".animation"))
                .commands(buildItemCommandFromConfig(fileConfiguration, path))
                .frames(buildFramesFromConfig(fileConfiguration, path, loresReplacements, titleReplacements));
    }

    public static ItemBuilder buildSimpleItemFromConfig(@NotNull FileConfiguration fileConfiguration, @NotNull String path) {
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null) {
            return new ItemBuilder(Material.PAPER).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!"));
        }
        return new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial())
                .amount(fileConfiguration.getInt(path + ".amount"));
    }

    public static ItemBuilder buildSimpleItemFromConfig(@NotNull FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements, @NotNull ObjectMap<String, String> titleReplacements) {
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null || fileConfiguration.get(path + ".amount") == null) {
            return new ItemBuilder(Material.PAPER).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!"));
        }
        if (fileConfiguration.get(path + ".title") == null || fileConfiguration.get(path + ".lores") == null || fileConfiguration.get(path + ".enchantments") == null)
            return buildSimpleItemFromConfig(fileConfiguration, path);
        return new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial())
                .amount(fileConfiguration.getInt(path + ".amount"))
                .title(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getString(path + ".title"), notNull("titleReplacements", titleReplacements), true)))
                .lores(BukkitMinecraftUtils.colorize(Utils.placeHolder(fileConfiguration.getStringList(path + ".lores"), notNull("loresReplacements", loresReplacements), true)))
                .enchantment(fileConfiguration.getStringList(path + ".enchantments"))
                ;
    }

    private static @NotNull List<ItemCommand> buildItemCommandFromConfig(@NotNull FileConfiguration fileConfiguration, @NotNull String path) {
        List<ItemCommand> itemCommands = Lists.newArrayList();
        if (fileConfiguration.getConfigurationSection(path + ".commands") != null && !Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".commands")).getKeys(true).isEmpty()) {
            fileConfiguration.getStringList(path + ".commands.RIGHT");
            if (!fileConfiguration.getStringList(path + ".commands.RIGHT").isEmpty()) {
                ItemCommand itemCommand = new ItemCommand(ItemCommandType.RIGHT, fileConfiguration.getInt(path + ".commands cooldown.RIGHT"), fileConfiguration.getStringList(path + ".commands.RIGHT"));
                itemCommands.add(itemCommand);
            }
            fileConfiguration.getStringList(path + ".commands.LEFT");
            if (!fileConfiguration.getStringList(path + ".commands.LEFT").isEmpty()) {
                ItemCommand itemCommand = new ItemCommand(ItemCommandType.LEFT, fileConfiguration.getInt(path + ".commands cooldown.LEFT"), fileConfiguration.getStringList(path + ".commands.LEFT"));
                itemCommands.add(itemCommand);
            }
            fileConfiguration.getStringList(path + ".commands.MIDDLE");
            if (!fileConfiguration.getStringList(path + ".commands.MIDDLE").isEmpty()) {
                ItemCommand itemCommand = new ItemCommand(ItemCommandType.MIDDLE, fileConfiguration.getInt(path + ".commands cooldown.MIDDLE"), fileConfiguration.getStringList(path + ".commands.MIDDLE"));
                itemCommands.add(itemCommand);
            }
        }
        return itemCommands;
    }

    public static @NotNull List<ItemStack> buildFramesFromConfig(@NotNull FileConfiguration fileConfiguration, @NotNull String path, @NotNull ObjectMap<String, String> loresReplacements, @NotNull ObjectMap<String, String> titleReplacements) {
        List<ItemStack> itemStacks = Lists.newArrayList();
        notNull("fileConfiguration", fileConfiguration);
        if (fileConfiguration.get(notNull("path", path)) == null) {
            return Lists.newArrayList(new ItemBuilder(Material.ANVIL).title(BukkitMinecraftUtils.colorize("&c&l&nInvalid path!!")).build());
        }
        if (fileConfiguration.getConfigurationSection(path + ".frames") != null && !Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".frames")).getKeys(false).isEmpty()) {
            itemStacks.add(new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".item")).parseMaterial()).build());
            for (String b : Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".frames")).getKeys(false)) {
                itemStacks.add(new ItemBuilder(XMaterial.valueOf(fileConfiguration.getString(path + ".frames." + b + ".item")).parseMaterial()).build());
            }
        }
        return itemStacks;
    }

    public static @NotNull List<Action> buildActionsFromConfig(@NotNull ActionManager actionManager, @NotNull FileConfiguration fileConfiguration, @NotNull String path, @NotNull Action action) {
        List<Action> actions = Lists.newArrayList();
        if (fileConfiguration.get(path) == null) {
            return actions;
        }
        if (!fileConfiguration.isSet(path + ".actions")) {
            return actions;
        }
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(path + ".actions");
        if (configurationSection == null) {
            return actions;
        }

        if (!configurationSection.getKeys(false).isEmpty()) {
            for (String key : Objects.requireNonNull(fileConfiguration.getConfigurationSection(path + ".actions")).getKeys(false)) {
                actions.add(actionManager.addAction(
                                action,
                                ObjectMap.Pair.create(
                                        key,
                                        fileConfiguration.getStringList(path + ".actions." + key).stream()
                                                .map(str -> (Object) str)
                                                .toList()
                                )
                        )
                );
            }
        }

        return actions;
    }

    public ItemBuilder material(@NotNull XMaterial material) {
        this.material = material.parseMaterial();
        return this;
    }

    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder durability(short durability) {
        this.durability = durability;
        return this;
    }

    public ItemBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder lores(List<String> lores) {
        this.lores.addAll(lores);
        return this;
    }

    public ItemBuilder lores(String... lores) {
        this.lores.addAll(Arrays.asList(lores));
        return this;
    }

    public ItemBuilder lore(String line) {
        this.lores.add(line);
        return this;
    }

    public ItemBuilder enchantment(@NotNull XEnchantment enchantment, int level) {
        this.enchantment(enchantment.getEnchant(), level);
        return this;
    }

    public ItemBuilder enchantment(@NotNull XEnchantment enchantment) {
        this.enchantment(enchantment.getEnchant());
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        this.enchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder enchantment0(@NotNull ObjectMap<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            this.enchantment(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public ItemBuilder enchantment(@NotNull ObjectMap<XEnchantment, Integer> enchantments) {
        for (Map.Entry<XEnchantment, Integer> entry : enchantments.entrySet()) {
            this.enchantment(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public ItemBuilder enchantment(@NotNull List<String> enchantments) {
        for (String enchantment : enchantments) {
            this.enchantment(XEnchantment.valueOf(enchantment));
        }
        return this;
    }

    public ItemBuilder clearLores() {
        this.lores.clear();
        return this;
    }

    public ItemBuilder clearEnchantments() {
        this.enchantments.clear();
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder skull(String owner) {
        if (this.itemStack.getItemMeta() != null && this.itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
            //noinspection deprecation
            skullMeta.setOwner(owner);
            this.itemStack.setItemMeta(skullMeta);
        }

        return this;
    }

    public ItemBuilder colors(@NotNull List<String> colors) {
        if (colors.size() >= 2) {
            try {
                this.nbtItem.setString("colors", Utils.serializeObjectToString(colors));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    public ItemBuilder colors(String @NotNull ... colors) {
        colors(Arrays.asList(colors));
        return this;
    }

    public ItemBuilder commands(ItemCommand... itemCommands) {
        commands(Arrays.asList(itemCommands));
        return this;
    }

    public ItemBuilder commands(List<ItemCommand> itemCommands) {
        try {
            this.nbtItem.setString("commands", Utils.serializeObjectToString(itemCommands));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ItemBuilder animation(String animation) {
        this.nbtItem.setString("animation", animation);
        return this;
    }

    public ItemBuilder frames(ItemStack... frames) {
        return frames(Arrays.asList(frames));
    }

    public ItemBuilder frames(List<ItemStack> frames) {
        this.nbtItem.setString("frames", BukkitMinecraftUtils.itemStackListToBase64(frames));
        return this;
    }

    public ItemBuilder customNBT(String key, Object value) {
        try {
            this.nbtItem.setString(key, Utils.serializeObjectToString(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public ItemBuilder showAllAttributes(boolean show) {
        if (!show) {
            this.flags.addAll(Arrays.asList(ItemFlag.values()));
        } else {
            this.flags.removeAll(Arrays.asList(ItemFlag.values()));
        }

        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (glow)
            return glow();
        else
            return this;
    }

    public ItemBuilder glow() {
        return enchantment(Enchantment.ARROW_FIRE, 1).showAllAttributes(false);
    }

    public ItemStack build() {
        ItemStack itemStack = this.itemStack;
        if (this.material != null) {
            itemStack.setType(this.material);
        }

        for (Enchantment enchantment : this.enchantments.keySet()) {
            itemStack.addUnsafeEnchantment(enchantment, this.enchantments.get(enchantment));
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasLore()) {
            //noinspection deprecation
            meta.setLore(Lists.newArrayList());
        }
        if (this.unbreakable) {
            meta.setUnbreakable(true);
        }

        if (this.amount > 0) {
            itemStack.setAmount(this.amount);
        }

        if (this.durability != null) {
            //noinspection deprecation
            itemStack.setDurability(this.durability);
        }

        if (this.title != null) {
            //noinspection deprecation
            meta.setDisplayName(BukkitMinecraftUtils.colorize(this.title));
        }

        if (this.lores != null && this.lores.size() > 0) {
            //noinspection deprecation
            meta.setLore(BukkitMinecraftUtils.colorize(this.lores));
        }

        if (this.flags != null && this.flags.size() > 0) {
            meta.addItemFlags(this.flags.toArray(new ItemFlag[0]));
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemBuilder clone() throws CloneNotSupportedException {
        return (ItemBuilder) super.clone();
    }

    @Override
    public String toString() {
        return "ItemBuilder{" +
                "itemStack=" + itemStack +
                ", material=" + material +
                ", durability=" + durability +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", lores=" + lores +
                ", flags=" + flags +
                ", enchantments=" + enchantments +
                ", unbreakable=" + unbreakable +
                ", nbtItem=" + nbtItem +
                '}';
    }

    public enum ItemCommandType {
        RIGHT,
        LEFT,
        MIDDLE,
    }

    public static class ItemCommand implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private final ItemCommandType type;
        private final String[] commands;
        private final int cooldown;

        public ItemCommand(ItemCommandType type, int cooldown, @NotNull List<String> commands) {
            this.type = type;
            this.cooldown = cooldown;
            this.commands = commands.toArray(new String[0]);
        }

        public ItemCommand(ItemCommandType type, int cooldown, String... commands) {
            this.type = type;
            this.cooldown = cooldown;
            this.commands = commands;
        }

        public ItemCommandType getType() {
            return type;
        }

        public String[] getCommands() {
            return commands;
        }

        public int getCooldown() {
            return cooldown;
        }

        @Override
        public String toString() {
            return "ItemCommand{" +
                    "type=" + type +
                    ", commands=" + Arrays.toString(commands) +
                    ", cooldown=" + cooldown +
                    '}';
        }
    }
}