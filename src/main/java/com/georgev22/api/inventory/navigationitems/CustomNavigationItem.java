package com.georgev22.api.inventory.navigationitems;

import com.georgev22.api.inventory.NavigationType;
import com.georgev22.api.inventory.handlers.PagedInventoryCustomNavigationHandler;
import org.bukkit.inventory.ItemStack;

public abstract class CustomNavigationItem extends NavigationItem {

    public CustomNavigationItem(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }

    @Override
    public final NavigationType getNavigationType() {
        return NavigationType.CUSTOM;
    }

    public abstract void handleClick(PagedInventoryCustomNavigationHandler handler);
}
