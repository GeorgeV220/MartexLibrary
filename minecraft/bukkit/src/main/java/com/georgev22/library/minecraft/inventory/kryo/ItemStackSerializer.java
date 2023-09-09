package com.georgev22.library.minecraft.inventory.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemStackSerializer extends Serializer<ItemStack> {

    @Override
    public void write(@NotNull Kryo kryo, Output output, @NotNull ItemStack itemStack) {
        kryo.writeObject(output, itemStack.getType().name());
        kryo.writeObject(output, itemStack.getAmount());

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            kryo.writeObject(output, damageable.getDamage());
        } else {
            kryo.writeObject(output, 0);
        }

        kryo.writeObject(output, itemStack.getEnchantments());
        kryo.writeObject(output, itemStack.getItemMeta());

        NBTContainer nbtContainer = NBTItem.convertItemtoNBT(itemStack);
        kryo.writeObject(output, nbtContainer.toString());
    }

    @Override
    public ItemStack read(@NotNull Kryo kryo, Input input, Class<? extends ItemStack> type) {
        String materialName = kryo.readObject(input, String.class);
        int amount = kryo.readObject(input, int.class);
        int durability = kryo.readObject(input, int.class);
        Map<Enchantment, Integer> enchantments = kryo.readObject(input, HashMap.class);
        ItemMeta itemMeta = kryo.readObject(input, ItemMeta.class);

        String serializedNBT = kryo.readObject(input, String.class);
        NBTContainer nbtContainer = new NBTContainer(serializedNBT);
        ItemStack itemStack = NBTItem.convertNBTtoItem(nbtContainer);
        itemStack.setType(Material.valueOf(materialName));
        itemStack.setAmount(amount);

        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(durability);
        }

        itemStack.addUnsafeEnchantments(enchantments);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}

