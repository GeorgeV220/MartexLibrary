package com.georgev22.library.minecraft.inventory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.georgev22.library.minecraft.inventory.ItemBuilder.ItemCommand;

import java.util.Arrays;

public class ItemCommandSerializer extends FieldSerializer<ItemCommand> {
        public ItemCommandSerializer(Kryo kryo) {
            super(kryo, ItemCommand.class);
        }

        @Override
        public void write(Kryo kryo, Output output, ItemCommand object) {
            super.write(kryo, output, object);
            output.writeInt(object.getType().ordinal());
            output.writeInt(object.getCooldown());
            kryo.writeObject(output, Arrays.asList(object.getCommands()), new DefaultArraySerializers.ObjectArraySerializer(kryo, String.class));
        }

        @Override
        public ItemCommand read(Kryo kryo, Input input, Class<? extends ItemCommand> type) {
            ItemCommand itemCommand = super.read(kryo, input, type);
            itemCommand.type = ItemBuilder.ItemCommandType.values()[input.readInt()];
            itemCommand.cooldown = input.readInt();
            itemCommand.commands = kryo.readObject(input, String[].class, new DefaultArraySerializers.ObjectArraySerializer(kryo, String.class));
            return itemCommand;
        }
    }