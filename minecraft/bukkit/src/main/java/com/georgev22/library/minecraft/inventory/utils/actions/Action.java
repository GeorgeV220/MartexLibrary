package com.georgev22.library.minecraft.inventory.utils.actions;

import com.georgev22.library.exceptions.ActionRunException;
import com.georgev22.library.maps.ObjectMap;
import org.bukkit.OfflinePlayer;

import java.util.List;

public interface Action {

    void runAction(OfflinePlayer offlinePlayer) throws ActionRunException;

    String name();

    List<ObjectMap.Pair<String, List<Object>>> data();

}
