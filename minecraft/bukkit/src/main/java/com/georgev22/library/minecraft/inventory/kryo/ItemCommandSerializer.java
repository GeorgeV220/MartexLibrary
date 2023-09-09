package com.georgev22.library.minecraft.inventory.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.georgev22.library.minecraft.inventory.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemCommandSerializer extends Serializer<ItemBuilder.ItemCommand> {
    @Override
    public void write(@NotNull Kryo kryo, Output output, ItemBuilder.@NotNull ItemCommand object) {
        kryo.writeObject(output, object.getType());
        output.writeInt(object.getCooldown());
        kryo.writeObject(output, object.getCommands());
    }

    @Override
    public ItemBuilder.ItemCommand read(@NotNull Kryo kryo, Input input, Class<? extends ItemBuilder.ItemCommand> type) {
        ItemBuilder.ItemCommand itemCommand = new ItemBuilder.ItemCommand();
        itemCommand.setType(kryo.readObject(input, ItemBuilder.ItemCommandType.class));
        itemCommand.setCooldown(input.readInt());
        itemCommand.setCommands(kryo.readObject(input, ArrayList.class));
        return itemCommand;
    }
}
