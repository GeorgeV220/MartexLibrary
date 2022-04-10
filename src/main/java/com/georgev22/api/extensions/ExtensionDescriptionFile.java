package com.georgev22.api.extensions;

import com.georgev22.api.maps.ObjectMap;
import com.georgev22.api.utilities.Utils;
import com.georgev22.api.yaml.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ExtensionDescriptionFile(FileConfiguration fileConfiguration) {

    public String getMain() {
        return fileConfiguration.getString("main");
    }

    public List<String> getAuthors() {
        return fileConfiguration.getStringList("authors");
    }

    @Contract(" -> new")
    public @NotNull ObjectMap<String, String> getLibraries() {
        return Utils.stringListToObjectMap(fileConfiguration.getStringList("libraries"), String.class);
    }

}
