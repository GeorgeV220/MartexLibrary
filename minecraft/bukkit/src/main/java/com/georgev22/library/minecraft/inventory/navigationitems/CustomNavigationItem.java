package com.georgev22.library.minecraft.inventory.navigationitems;

import com.georgev22.library.minecraft.inventory.NavigationType;
import com.georgev22.library.minecraft.inventory.handlers.PagedInventoryCustomNavigationHandler;
import org.bukkit.inventory.ItemStack;

public abstract class CustomNavigationItem extends NavigationItem {

    public CustomNavigationItem(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }

    @Override
    public final NavigationType getNavigationType() {
        return NavigationType.CUSTOM;
    }

    @Deprecated
    public abstract void handleClick(PagedInventoryCustomNavigationHandler handler);
}
