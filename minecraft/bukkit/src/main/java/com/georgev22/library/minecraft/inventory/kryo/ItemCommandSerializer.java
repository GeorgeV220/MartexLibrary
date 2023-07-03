package com.georgev22.library.minecraft.inventory.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.georgev22.library.minecraft.inventory.ItemBuilder;

import java.util.List;

public class ItemCommandSerializer extends Serializer<ItemBuilder.ItemCommand> {
    @Override
    public void write(Kryo kryo, Output output, ItemBuilder.ItemCommand object) {
        kryo.writeObject(output, object.getType());
        output.writeInt(object.getCooldown());
        kryo.writeObject(output, object.getCommands());
    }

    @Override
    public ItemBuilder.ItemCommand read(Kryo kryo, Input input, Class<? extends ItemBuilder.ItemCommand> type) {
        ItemBuilder.ItemCommand itemCommand = new ItemBuilder.ItemCommand();
        itemCommand.setType(kryo.readObject(input, ItemBuilder.ItemCommandType.class));
        itemCommand.setCooldown(input.readInt());
        itemCommand.setCommands(kryo.readObject(input, List.class));
        return itemCommand;
    }
}
