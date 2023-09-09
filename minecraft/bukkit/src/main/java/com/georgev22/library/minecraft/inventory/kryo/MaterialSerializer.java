package com.georgev22.library.minecraft.inventory.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class MaterialSerializer extends Serializer<Material> {

    @Override
    public void write(Kryo kryo, @NotNull Output output, @NotNull Material material) {
        output.writeString(material.name());
    }

    @Override
    public Material read(Kryo kryo, @NotNull Input input, Class<? extends Material> type) {
        String materialName = input.readString();
        return Material.valueOf(materialName);
    }
}