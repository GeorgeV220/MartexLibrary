package com.georgev22.library.minecraft.inventory.utils.actions;

import com.georgev22.library.exceptions.ActionRunException;
import org.bukkit.OfflinePlayer;

public abstract class Action {

    public abstract void runAction(OfflinePlayer offlinePlayer) throws ActionRunException;

}
