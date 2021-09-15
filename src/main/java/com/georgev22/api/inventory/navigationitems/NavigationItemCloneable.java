package com.georgev22.api.inventory.navigationitems;

import org.bukkit.inventory.ItemStack;

public abstract class NavigationItemCloneable extends NavigationItem implements Cloneable {

    NavigationItemCloneable(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }

    @Override
    public abstract NavigationItem clone();

}
