package com.georgev22.library.minecraft.colors;

import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Color {

    private final String colorCode;
    private final int r;
    private final int g;
    private final int b;
    private static java.awt.Color color;

    @Contract("_ -> new")
    public static @NotNull Color from(String colorCode) {
        return new Color(colorCode);
    }

    public static @NotNull Color from(int r, int g, int b) {
        color = new java.awt.Color(r, g, b);
        return from(Integer.toHexString(color.getRGB()).substring(2));
    }

    private Color(@NotNull String colorCode) {
        this.colorCode = colorCode.replace("#", "");
        color = new java.awt.Color(Integer.parseInt(this.colorCode, 16));
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
    }

    public String getColorCode() {
        return this.colorCode;
    }

    public int getRed() {
        return this.r;
    }

    public int getGreen() {
        return this.g;
    }

    public int getBlue() {
        return this.b;
    }

    public java.awt.Color getColor() {
        return color;
    }

    public String getAppliedTag() {
        boolean bool = BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion().isAboveOrEqual(BukkitMinecraftUtils.MinecraftVersion.V1_16_R1);
        return bool ? "§x" + Arrays.stream(this.colorCode.split("")).map((paramString) -> "§" + paramString).collect(Collectors.joining()) : MinecraftColor.getClosest(this).getAppliedTag();
    }

    public String getColorTag() {
        return "{#" + this.colorCode + "}";
    }

    public String getTag() {
        return "#" + this.colorCode;
    }

    public static int difference(@NotNull Color color1, @NotNull Color color2) {
        return Math.abs(color1.r - color2.r) + Math.abs(color1.g - color2.g) + Math.abs(color1.b - color2.b);
    }

    @Override
    public String toString() {
        return this.getAppliedTag();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color)) return false;
        Color color = (Color) o;
        return r == color.r && g == color.g && b == color.b && Objects.equals(colorCode, color.colorCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colorCode, r, g, b);
    }
}
