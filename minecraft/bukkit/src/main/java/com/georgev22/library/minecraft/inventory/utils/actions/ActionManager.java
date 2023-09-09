package com.georgev22.library.minecraft.inventory.utils.actions;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;

public class ActionManager {
    private ObjectMap<String, Action> actions;

    public ActionManager() {
        actions = new HashObjectMap<>();
    }

    public void registerAction(String name, Action action) {
        actions.put(name, action);
    }

    public void executeAction(String name) {
        Action action = actions.get(name);
        if (action != null) {
            action.runAction();
        }
    }
}