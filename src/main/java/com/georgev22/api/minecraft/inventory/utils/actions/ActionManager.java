package com.georgev22.api.minecraft.inventory.utils.actions;

import com.georgev22.api.minecraft.MinecraftUtils;
import com.georgev22.api.minecraft.exceptions.ActionRunException;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ActionManager {

    public static void runActions(@NotNull OfflinePlayer offlinePlayer, @NotNull JavaPlugin plugin, boolean error, @NotNull List<Action> actions) {
        for (Action action : actions) {
            try {
                action.runAction(offlinePlayer);
            } catch (ActionRunException actionRunException) {
                if (error)
                    actionRunException.printStackTrace();
                else
                    MinecraftUtils.debug(plugin, actionRunException.getMessage());
                break;
            }
        }
    }

    private final String actionName;
    private final Object[] data;

    public ActionManager(String actionName, Object... data) {
        this.actionName = actionName;
        this.data = data;
    }

    public String getActionName() {
        return actionName;
    }

    public Object[] getData() {
        return data;
    }
}
