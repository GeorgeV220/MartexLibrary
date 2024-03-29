package com.georgev22.library.minecraft.inventory.navigationitems;

import com.georgev22.library.minecraft.inventory.NavigationType;
import org.bukkit.inventory.ItemStack;

public final class PreviousNavigationItem extends NavigationItemCloneable {

    public PreviousNavigationItem(ItemStack itemStack) {
        this(itemStack, 0);
    }

    public PreviousNavigationItem(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.PREVIOUS;
    }

    @Override
    public NavigationItem clone() {
        return new PreviousNavigationItem(getItemStack().clone());
    }
}
