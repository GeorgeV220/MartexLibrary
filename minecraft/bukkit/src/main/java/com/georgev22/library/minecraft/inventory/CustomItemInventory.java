package com.georgev22.library.minecraft.inventory;

import com.georgev22.library.maps.ObjectMap;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CustomItemInventory {

    private final String inventoryName;
    private final ObjectMap<Integer, ItemStack> objectMap;
    private final int inventorySize;

    public CustomItemInventory(String inventoryName, ObjectMap<Integer, ItemStack> objectMap, int inventorySize) {
        this.inventoryName = inventoryName;
        this.objectMap = objectMap;
        this.inventorySize = inventorySize;
    }

    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
        for (Map.Entry<Integer, ItemStack> itemStackEntry : objectMap.entrySet()) {
            inventory.setItem(itemStackEntry.getKey(), itemStackEntry.getValue());
        }
        return inventory;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public Integer getInventorySize() {
        return inventorySize;
    }
}
