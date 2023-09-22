package com.georgev22.library.minecraft.inventory;

import com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftReflection;
import com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftVersion;
import com.georgev22.library.minecraft.exceptions.DeserializationException;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Utility class for serializing and deserializing ItemStacks and Inventories.
 *
 * <p>This class provides methods to convert ItemStacks and Inventories to and from serialized
 * forms that can be stored or transmitted, such as in databases or configuration files.
 * It uses NBT serialization for ItemStacks and custom serialization for Inventories.</p>
 *
 * <p>This class relies on reflection to work with different Minecraft versions and may not
 * be compatible with all versions.</p>
 *
 * @author fren_gor, GeorgeV22
 * @version 1.0
 */
public final class ItemStackSerializer {

    private static Constructor<?> nbtTagCompoundConstructor, nmsItemStackConstructor;
    private static Method aIn, aOut, createStack, asBukkitCopy, asNMSCopy, save, getTitle;
    private static final byte INV_VERSION = 0x01;

    static {
        Class<?> nbtTagCompoundClass;
        try {
            nbtTagCompoundClass = MinecraftReflection.getNMSClass("NBTTagCompound", "net.minecraft.nbt.NBTTagCompound");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class<?> nmsItemStackClass;
        try {
            nmsItemStackClass = MinecraftReflection.getNMSClass("ItemStack", "net.minecraft.world.item.ItemStack");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class<?> nbtCompressedStreamToolsClass;
        try {
            nbtCompressedStreamToolsClass = MinecraftReflection.getNMSClass("NBTCompressedStreamTools", "net.minecraft.nbt.NBTCompressedStreamTools");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class<?> craftItemStackClass;
        try {
            craftItemStackClass = MinecraftReflection.getOBCClass("inventory.CraftItemStack");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            nbtTagCompoundConstructor = nbtTagCompoundClass.getDeclaredConstructor();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        try {
            aIn = nbtCompressedStreamToolsClass.getMethod("a", MinecraftVersion.getVersionNumber() < 16 || (MinecraftVersion.getVersionNumber() == 16 && MinecraftVersion.getReleaseNumber() == 1) ? DataInputStream.class : DataInput.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        try {
            aOut = nbtCompressedStreamToolsClass.getMethod("a", nbtTagCompoundClass, DataOutput.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        try {
            if (MinecraftVersion.getVersionNumber() < 11) {
                createStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass);
            } else {
                nmsItemStackConstructor = nmsItemStackClass.getDeclaredConstructor(nbtTagCompoundClass);
                nmsItemStackConstructor.setAccessible(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            asBukkitCopy = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        try {
            asNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        try {
            save = nmsItemStackClass.getMethod(MinecraftVersion.getVersionNumber() >= 18 ? "b" : "save", nbtTagCompoundClass);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (MinecraftVersion.getVersionNumber() < 14) {
            try {
                getTitle = Inventory.class.getDeclaredMethod("getTitle");
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Deserialize a serialized ItemStack from a string.
     *
     * @param data The serialized string representation of the ItemStack.
     * @return The deserialized ItemStack, or null if deserialization fails.
     */
    @Contract("null -> new")
    public static @Nullable ItemStack deserializeItemStack(String data) {
        if (data == null || data.isEmpty()) {
            return new ItemStack(Material.AIR);
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            Object nbtTagCompound = aIn.invoke(null, dataInputStream);
            Object craftItemStack = craftNMSItemStack(nbtTagCompound);
            return (ItemStack) asBukkitCopy.invoke(null, craftItemStack);
        } catch (ReflectiveOperationException | IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error:", e);
            return null;
        }

    }

    /**
     * Deserialize an array of serialized ItemStacks from an array of strings.
     *
     * @param data The array of serialized string representations of ItemStacks.
     * @return An array of deserialized ItemStacks.
     */
    public static ItemStack @NotNull [] deserializeItemStack(String[] data) {
        Validate.notNull(data, "Data cannot be null");
        ItemStack[] arr = new ItemStack[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = deserializeItemStack(data[i]);
        }
        return arr;
    }

    /**
     * Deserialize a list of serialized ItemStacks from a list of strings.
     *
     * @param data The list of serialized string representations of ItemStacks.
     * @return A list of deserialized ItemStacks.
     */
    public static @NotNull List<ItemStack> deserializeItemStack(List<String> data) {
        Validate.notNull(data, "Data cannot be null");
        List<ItemStack> l = new ArrayList<>(data.size());
        for (String s : data) {
            l.add(deserializeItemStack(s));
        }
        return l;
    }

    /**
     * Serialize an ItemStack to a string.
     *
     * @param item The ItemStack to serialize.
     * @return The serialized string representation of the ItemStack.
     */
    public static @NotNull String serializeItemStack(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "";
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(outputStream)) {
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = asNMSCopy.invoke(null, item);
            save.invoke(nmsItemStack, nbtTagCompound);
            aOut.invoke(null, nbtTagCompound, dataOutput);
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (ReflectiveOperationException | IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error:", e);
            return "";
        }

    }

    /**
     * Serialize an array of ItemStacks to an array of strings.
     *
     * @param items The array of ItemStacks to serialize.
     * @return An array of serialized string representations of ItemStacks.
     */
    public static String @NotNull [] serializeItemStack(ItemStack[] items) {
        Validate.notNull(items, "Items cannot be null");
        String[] arr = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            arr[i] = serializeItemStack(items[i]);
        }
        return arr;
    }

    /**
     * Serialize a list of ItemStacks to a list of strings.
     *
     * @param items The list of ItemStacks to serialize.
     * @return A list of serialized string representations of ItemStacks.
     */
    public static @NotNull List<String> serializeItemStack(List<ItemStack> items) {
        Validate.notNull(items, "Items cannot be null");
        List<String> l = new ArrayList<>(items.size());
        for (ItemStack s : items) {
            l.add(serializeItemStack(s));
        }
        return l;
    }

    /**
     * Serialize an Inventory to a string.
     *
     * @param inv The Inventory to serialize.
     * @return The serialized string representation of the Inventory.
     */
    public static @Nullable String serializeInventory(Inventory inv) {
        Validate.notNull(inv, "Inventory cannot be null");
        Validate.isTrue(inv.getType() == InventoryType.CHEST,
                "Illegal inventory type " + inv.getType() + "(expected CHEST).");

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(outputStream)) {
            dataOutput.writeByte(INV_VERSION);

            dataOutput.writeByte(inv.getSize());
            if (MinecraftVersion.getVersionNumber() < 14) {
                dataOutput.writeBoolean(true);
                dataOutput.writeUTF((String) getTitle.invoke(inv));
            } else {
                dataOutput.writeBoolean(false);
            }

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack it = inv.getItem(i);
                if (it == null || it.getType() == Material.AIR)
                    continue;
                dataOutput.writeByte(i);
                dataOutput.writeUTF(serializeItemStack(it));
            }

            dataOutput.writeByte(-1);

            /*
             * Version - 1 byte
             * Size - 1 byte
             * NextIsPresent - Boolean
             * Title - String (Present only if the previous is true)
             * Array:
             *   SlotIndex - 1 byte
             *   ItemStack - String
             * -1 (Array End) - 1 byte
             */
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (ReflectiveOperationException | IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error:", e);
            return null;
        }
    }

    /**
     * Deserialize an InventoryMap from a serialized string.
     *
     * @param data The serialized string representation of the InventoryMap.
     * @return The deserialized InventoryMap, or null if deserialization fails.
     */
    public static @Nullable InventoryMap deserializeInventory(String data) {
        Validate.notNull(data, "Data cannot be null");
        Validate.isTrue(!data.isEmpty(), "Data cannot be empty");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            int version = dataInputStream.readByte();

            if (version != INV_VERSION)
                throw new DeserializationException("Invalid inventory version \"" + version
                        + "\". The only supported version is the  \"" + INV_VERSION + "\".");

            int size = dataInputStream.readByte();
            boolean present = dataInputStream.readBoolean();
            String title = present ? dataInputStream.readUTF() : null;

            InventoryMap map = new InventoryMap(size, size, title);

            while (true) {
                int slot = dataInputStream.readByte();
                if (slot == -1 || slot >= size)
                    break;
                ItemStack it = deserializeItemStack(dataInputStream.readUTF());

                map.put(slot, it);
            }

            return map;
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error:", e);
            return null;
        }
    }

    /**
     * Deserialize an object into an ItemStack.
     *
     * <p>This method attempts to deserialize the given object into an ItemStack. It supports
     * deserialization from ItemStacks and serialized strings representing ItemStacks.
     *
     * @param obj The object to deserialize.
     * @return The deserialized ItemStack.
     * @throws DeserializationException If the object cannot be deserialized into an ItemStack.
     */
    public static ItemStack deserializeObject(Object obj) throws DeserializationException {
        if (obj instanceof ItemStack) {
            return (ItemStack) obj;
        } else if (obj instanceof String) {
            return deserializeItemStack((String) obj);
        } else
            throw new DeserializationException("Couldn't deserialize object");
    }

    private static Object craftNMSItemStack(Object nbtTagCompound) throws ReflectiveOperationException {
        if (MinecraftVersion.getVersionNumber() < 11) {
            return createStack.invoke(null, nbtTagCompound);
        } else {
            return nmsItemStackConstructor.newInstance(nbtTagCompound);
        }
    }
}