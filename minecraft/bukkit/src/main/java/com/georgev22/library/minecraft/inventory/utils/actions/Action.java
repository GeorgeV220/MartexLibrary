package com.georgev22.library.minecraft.inventory.utils.actions;

import com.georgev22.library.maps.ConcurrentObjectMap;

public interface Action {
    void runAction();

    ConcurrentObjectMap<String, Object> getData();
}
