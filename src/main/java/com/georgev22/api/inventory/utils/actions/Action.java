package com.georgev22.api.inventory.utils.actions;

import com.georgev22.api.utilities.exceptions.ActionRunException;
import org.bukkit.OfflinePlayer;

public abstract class Action {

    public abstract void runAction(OfflinePlayer offlinePlayer) throws ActionRunException;

}
