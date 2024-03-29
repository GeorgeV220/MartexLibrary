package com.georgev22.library.minecraft.inventory;

import com.georgev22.library.minecraft.inventory.navigationitems.NavigationItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class PagedInventoryAPI {

    private final InventoryRegistrar registrar;

    public PagedInventoryAPI(Plugin plugin) {
        this.registrar = new InventoryRegistrar(plugin);
        Bukkit.getPluginManager().registerEvents(new PagedInventoryListener(plugin, registrar), plugin);
    }

    public IPagedInventory createPagedInventory(NavigationRow navigationRow, boolean animated) {
        return new PagedInventory(registrar, navigationRow, animated);
    }

    /**
     * Create a new {@link IPagedInventory}
     *
     * @param navigation The navigation for the paged inventory. Must contain next, previous, and close buttons
     * @return The newly created {@link IPagedInventory}
     */
    public IPagedInventory createPagedInventory(Map<Integer, NavigationItem> navigation, boolean animated) {
        return new PagedInventory(registrar, navigation, animated);
    }

    /**
     * Gets the inventory registrar for this API instance
     *
     * @return The {@link InventoryRegistrar}
     */
    public InventoryRegistrar getRegistrar() {
        return registrar;
    }

}
