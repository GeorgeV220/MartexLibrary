package com.georgev22.library.minecraft.inventory.utils.actions;

import com.georgev22.library.exceptions.ActionRunException;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActionManager {

    private List<Action> actions = new ArrayList<>();

    public ActionManager() {
    }

    public ActionManager(Action action) {
        this.actions.add(action);
    }

    public ActionManager(List<Action> actions) {
        this.actions.addAll(actions);
    }

    public ActionManager(@NotNull Action action, ObjectMap.Pair<String, List<Object>> data) {
        action.data().add(data);
        this.actions.add(action);
    }


    public void runActions(@NotNull OfflinePlayer offlinePlayer, @NotNull JavaPlugin plugin) {
        for (Action action : actions) {
            try {
                action.runAction(offlinePlayer);
            } catch (ActionRunException actionRunException) {
                BukkitMinecraftUtils.debug(plugin, actionRunException.getMessage());
                break;
            }
        }
    }

    public Action addAction(Action action) {
        this.actions.add(action);
        return action;
    }

    public List<Action> addAction(List<Action> actions) {
        this.actions.addAll(actions);
        return actions;
    }

    public Action addAction(@NotNull Action action, ObjectMap.Pair<String, List<Object>> data) {
        action.data().add(data);
        this.actions.add(action);
        return action;
    }

}
