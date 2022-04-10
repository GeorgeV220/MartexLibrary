package com.georgev22.api.extensions;

import com.georgev22.api.utilities.Utils;
import com.georgev22.api.yaml.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public record ExtensionLoader(File dataFolder, Logger logger) {

    public void load() throws Exception {
        File[] jarFiles = getDataFolder().listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                Utils.Assertions.notNull("File cannot be null", jarFile);

                try (JarFile jar = new JarFile(jarFile)) {
                    JarEntry entry = jar.getJarEntry("extension.yml");

                    if (entry == null) {
                        throw new FileNotFoundException("Jar does not contain extension.yml");
                    }

                    InputStream stream = jar.getInputStream(entry);
                    ExtensionClassLoader extensionClassLoader = new ExtensionClassLoader(getClass().getClassLoader(), new ExtensionDescriptionFile(YamlConfiguration.loadConfiguration(new InputStreamReader(stream))), getDataFolder(), jarFile, logger);
                    extensionClassLoader.initialize(extensionClassLoader.extension);
                } catch (IOException | YAMLException ex) {
                    ex.printStackTrace();
                }
            }
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
