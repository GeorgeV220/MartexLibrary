package com.georgev22.library.minecraft.inventory.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.inventory.utils.actions.Action;
import org.jetbrains.annotations.NotNull;

public class ActionSerializer extends Serializer<Action> {
    @Override
    public void write(@NotNull Kryo kryo, Output output, @NotNull Action object) {
        kryo.writeObject(output, object.getClass().getName());
        kryo.writeObject(output, object.getData());
    }

    @Override
    public Action read(Kryo kryo, Input input, Class<? extends Action> type) {
        try {
            String className = kryo.readObject(input, String.class);
            Class<?> actionClass = Class.forName(className);
            Action action = (Action) actionClass.getDeclaredConstructor().newInstance();
            ObjectMap<String, Object> data = kryo.readObject(input, ConcurrentObjectMap.class);
            action.getData().append(data);
            return action;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Action class.", e);
        }
    }
}