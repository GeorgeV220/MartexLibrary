package com.georgev22.library.minecraft.inventory.handlers;

import com.georgev22.library.minecraft.inventory.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PagedInventoryCloseHandler extends PagedInventoryHandler {

    public abstract void handle(CloseHandler closeHandler);

    public static class CloseHandler extends Handler {

        public CloseHandler(IPagedInventory iPagedInventory, InventoryView inventoryView, Player player) {
            super(iPagedInventory, inventoryView, player);
        }

    }

}
