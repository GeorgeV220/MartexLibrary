package com.georgev22.api.minecraft.inventory.handlers;

import com.georgev22.api.minecraft.inventory.IPagedInventory;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PagedInventoryCustomNavigationHandler extends PagedInventoryClickHandler.ClickHandler {

    public PagedInventoryCustomNavigationHandler(IPagedInventory iPagedInventory, InventoryClickEvent event) {
        super(iPagedInventory, event);
    }

}
