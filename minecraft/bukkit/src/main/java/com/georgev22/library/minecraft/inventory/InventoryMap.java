package com.georgev22.library.minecraft.inventory;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftVersion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


/**
 * A custom class extending HashMap to represent an inventory's contents with specific properties.
 *
 * <p>This class is designed to represent the contents of an inventory in a more structured manner.
 * It extends HashMap, where the key represents the slot index, and the value is the ItemStack in that slot.
 * Additionally, it includes properties for the inventory size and title.</p>
 *
 * <p>The title property may not be set in Minecraft versions 1.8 to 1.13.</p>
 *
 * @version 1.0
 */
public final class InventoryMap extends HashObjectMap<Integer, ItemStack> {
    /**
     * The size of the represented inventory.
     */
    @Getter
    private final int inventorySize;

    /**
     * The title of the represented inventory.
     * <p>May not be set in Minecraft versions 1.8 to 1.13.</p>
     */
    @Getter
    private String title;

    /**
     * Sets the title of the represented inventory.
     *
     * <p>May not be set in Minecraft versions 1.8 to 1.13.</p>
     *
     * @param title The title to set.
     * @throws IllegalArgumentException If attempting to set the title in unsupported versions.
     */
    public void setTitle(String title) {
        if (MinecraftVersion.getVersionNumber() < 14) {
            throw new IllegalArgumentException("Title cannot be changed in 1.8-1.13");
        }
        this.title = title;
    }

    /**
     * Creates a new instance of InventoryMap.
     *
     * <p>This constructor is not intended for direct use.</p>
     *
     * @throws UnsupportedOperationException If attempting to use this constructor directly.
     */
    InventoryMap() {
        throw new UnsupportedOperationException("Illegal use of constructor");
    }

    /**
     * Creates a new instance of InventoryMap with a specific size and title.
     *
     * @param inventorySize The size of the represented inventory.
     * @param title         The title of the represented inventory.
     */
    InventoryMap(int inventorySize, String title) {
        super();
        this.inventorySize = inventorySize;
        this.title = title;
    }

    /**
     * Creates a new instance of InventoryMap with a specific initial capacity, load factor, size, and title.
     *
     * @param initialCapacity The initial capacity of the HashMap.
     * @param loadFactor      The load factor of the HashMap.
     * @param inventorySize   The size of the represented inventory.
     * @param title           The title of the represented inventory.
     */
    InventoryMap(int initialCapacity, float loadFactor, int inventorySize, String title) {
        super(initialCapacity, loadFactor);
        this.inventorySize = inventorySize;
        this.title = title;
    }

    /**
     * Creates a new instance of InventoryMap with a specific initial capacity, size, and title.
     *
     * @param initialCapacity The initial capacity of the HashMap.
     * @param inventorySize   The size of the represented inventory.
     * @param title           The title of the represented inventory.
     */
    InventoryMap(int initialCapacity, int inventorySize, String title) {
        super(initialCapacity);
        this.inventorySize = inventorySize;
        this.title = title;
    }

    /**
     * Creates a new instance of InventoryMap as a copy of an existing InventoryMap with a different title.
     *
     * @param m     The InventoryMap to copy.
     * @param title The title of the represented inventory.
     */
    InventoryMap(InventoryMap m, String title) {
        super(m);
        inventorySize = m.getInventorySize();
        this.title = title;
    }

    /**
     * Converts the InventoryMap into a Bukkit Inventory with a specific owner.
     *
     * @param owner The owner of the created Bukkit Inventory.
     * @return The Bukkit Inventory representing the InventoryMap.
     */
    public @NotNull Inventory toInventory(InventoryHolder owner) {
        Inventory inv;
        if (title != null)
            inv = Bukkit.createInventory(owner, inventorySize, title);
        else
            inv = Bukkit.createInventory(owner, inventorySize);
        for (Entry<Integer, ItemStack> e : entrySet()) {
            inv.setItem(e.getKey(), e.getValue() == null ? null : e.getValue().clone());
        }
        return inv;
    }

    /**
     * Retrieves an array of ItemStacks representing the contents of the InventoryMap.
     *
     * @return An array of ItemStacks representing the contents of the InventoryMap.
     */
    public ItemStack @NotNull [] getContents() {
        ItemStack[] arr = new ItemStack[size()];
        int i = 0;
        for (ItemStack it : values()) {
            arr[i++] = it.clone();
        }
        return arr;
    }

    /**
     * Retrieves an array of ItemStacks representing the storage contents of the InventoryMap.
     *
     * @return An array of ItemStacks representing the storage contents of the InventoryMap.
     */
    public ItemStack @NotNull [] getStorageContents() {
        ItemStack[] arr = new ItemStack[inventorySize];
        for (int i = 0; i < inventorySize; i++) {
            ItemStack it = get(i);
            if (it == null) {
                arr[i] = new ItemStack(Material.AIR);
            } else {
                arr[i] = it.clone();
            }
        }
        return arr;
    }
}
