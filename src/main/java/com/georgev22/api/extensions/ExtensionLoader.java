package com.georgev22.api.extensions;

import com.georgev22.api.utilities.Utils;
import com.georgev22.api.yaml.file.FileConfiguration;
import com.georgev22.api.yaml.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public record ExtensionLoader(File dataFolder) {

    public void load(File file) throws Exception {
        Utils.Assertions.notNull("File cannot be null", file);

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("extension.yml");

            if (entry == null) {
                throw new FileNotFoundException("Jar does not contain extension.yml");
            }

            stream = jar.getInputStream(entry);

        } catch (IOException | YAMLException ex) {
            ex.printStackTrace();
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException ignored) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }
        if (stream != null) {
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            ExtensionClassLoader extensionClassLoader = new ExtensionClassLoader(getClass().getClassLoader(), new ExtensionDescriptionFile(fileConfiguration), getDataFolder(), file);
            extensionClassLoader.initialize(extensionClassLoader.extension);
        }

    }

    @NotNull
    private File getDataFolder() {
        File libs = new File(dataFolder, "extensions");
        libs.mkdirs();//logger.info("libraries folder created!");
        return libs;
    }

}
