package com.georgev22.api.minecraft.inventory.utils.actions;

import com.georgev22.api.minecraft.exceptions.ActionRunException;
import org.bukkit.OfflinePlayer;

public abstract class Action {

    public abstract void runAction(OfflinePlayer offlinePlayer) throws ActionRunException;

}
