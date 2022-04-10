package com.georgev22.api.extensions;

import com.georgev22.api.utilities.Utils;
import com.georgev22.api.yaml.file.FileConfiguration;
import com.georgev22.api.yaml.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public record ExtensionLoader(File dataFolder, Logger logger, File jarFile) {

    public void load() throws Exception {
        Utils.Assertions.notNull("File cannot be null", jarFile);

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(jarFile);
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
            ExtensionClassLoader extensionClassLoader = new ExtensionClassLoader(getClass().getClassLoader(), new ExtensionDescriptionFile(fileConfiguration), getDataFolder(), jarFile, logger);
            extensionClassLoader.initialize(extensionClassLoader.extension);
        }

    }

    @NotNull
    private File getDataFolder() {
        File libs = new File(dataFolder, "extensions");
        if (libs.mkdirs()) {
            logger.info("extensions folder created!");
        }
        return libs;
    }

}
