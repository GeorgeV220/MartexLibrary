package com.georgev22.library.yaml.configmanager;

import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.library.yaml.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public final class CFG {

    private final static Set<CFG> cachedFiles = new HashSet<>();

    public static void reloadFiles() {
        cachedFiles.forEach(CFG::reloadFile);
    }

    /* The file's name (without the .yml) */
    private final String fileName;

    /* The yml file configuration. */
    private FileConfiguration fileConfiguration;
    /* The file. */
    private File file;

    private final Logger logger;

    private final File dataFolder;

    private final Class<?> clazz;

    private final boolean saveResource;

    public CFG(final String string, final File dataFolder, final boolean saveResource, final Logger logger, Class<?> clazz) throws Exception {
        this.fileName = string + ".yml";
        this.dataFolder = dataFolder;
        this.saveResource = saveResource;
        this.logger = logger;
        this.clazz = clazz;
        this.setup();
        cachedFiles.add(this);
    }

    /**
     * Attempts to load the file.
     *
     * @see #reloadFile()
     */
    public void setup() throws Exception {
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                logger.info("Folder " + dataFolder.getName() + " has been created!");
            }
        }

        this.file = new File(dataFolder, this.fileName);

        if (!this.file.exists()) {
            try {
                if (this.file.createNewFile()) {
                    logger.info("File " + this.file.getName() + " has been created!");
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            if (saveResource) {
                Utils.saveResource(this.fileName, true, this.dataFolder, this.clazz);
            }
        }

        this.reloadFile();
    }

    /**
     * Saves the file configuration.
     *
     * @see FileConfiguration#save(File)
     * @see #getFileConfiguration()
     */
    public void saveFile() {
        try {
            this.getFileConfiguration().save(this.file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the file.
     *
     * @see YamlConfiguration#loadConfiguration(File)
     * @see #file
     */
    public void reloadFile() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * @return the file - The {@link FileConfiguration}.
     */
    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    /**
     * Get the file
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileConfiguration, file, saveResource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CFG cfg = (CFG) o;
        return saveResource == cfg.saveResource && Objects.equals(fileName, cfg.fileName) && Objects.equals(fileConfiguration, cfg.fileConfiguration) && Objects.equals(file, cfg.file);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "CFG{" +
                "fileName='" + fileName + '\'' +
                ", fileConfiguration=" + fileConfiguration +
                ", file=" + file +
                ", saveResource=" + saveResource +
                '}';
    }
}