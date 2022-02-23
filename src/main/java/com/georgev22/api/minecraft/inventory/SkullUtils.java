package com.georgev22.api.minecraft.inventory;

import com.georgev22.api.utilities.Utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

public class SkullUtils {

    private final GameProfile gameProfile;

    public SkullUtils(String hash) {
        this.gameProfile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("textures", new Property("textures", hash));
    }

    public void applyTextures(@NotNull ItemStack itemStack) {
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        Class<?> c_skullMeta = skullMeta.getClass();
        try {
            Utils.Reflection.setFieldValue(c_skullMeta, skullMeta, "profile", gameProfile);
        } catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Contract("_, _ -> param1")
    public static @NotNull ItemStack applyTexture(@NotNull ItemStack itemStack, String texture) {
        if (itemStack.getItemMeta() instanceof SkullMeta) {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            PropertyMap propertyMap = gameProfile.getProperties();
            propertyMap.put("textures", new Property("textures", texture));
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            Class<?> c_skullMeta = skullMeta.getClass();
            try {
                Utils.Reflection.setFieldValue(c_skullMeta, skullMeta, "profile", gameProfile);
            } catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return itemStack;
    }

    public static Collection<Property> getTextures(@NotNull ItemStack itemStack) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        GameProfile profile = (GameProfile) Utils.Reflection.fetchField(skullMeta.getClass(), skullMeta, "profile");
        return profile.getProperties().get("textures");
    }
}