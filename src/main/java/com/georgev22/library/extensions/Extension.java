package com.georgev22.library.extensions;

import com.georgev22.library.yaml.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface Extension {
    /**
     * Returns the folder that the extension data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder
     */
    @NotNull File getDataFolder();

    /**
     * Returns the extension.yaml file containing the details for this extension
     *
     * @return Contents of the extension.yaml file
     */
    @NotNull ExtensionDescriptionFile getDescription();

    /**
     * Gets a {@link FileConfiguration} for this extension, read through
     * "config.yml"
     * <p>
     * If there is a default config.yml embedded in this extension, it will be
     * provided as a default for this Configuration.
     *
     * @return extension configuration
     */
    @NotNull FileConfiguration getConfig();

    /**
     * Gets an embedded resource in this extension
     *
     * @param filename Filename of the resource
     * @return File if found, otherwise null
     */
    @Nullable InputStream getResource(@NotNull String filename);

    /**
     * Saves the {@link FileConfiguration} retrievable by {@link #getConfig()}.
     */
    void saveConfig();

    /**
     * Saves the raw contents of the default config.yml file to the location
     * retrievable by {@link #getConfig()}.
     * <p>
     * This should fail silently if the config.yml already exists.
     */
    void saveDefaultConfig();

    /**
     * Saves the raw contents of any resource embedded with a extension's .jar
     * file assuming it can be found using {@link #getResource(String)}.
     * <p>
     * The resource is saved into the extension's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param resourcePath the embedded resource path to look for within the
     *                     extension's .jar file. (No preceding slash).
     * @param replace      if true, the embedded resource will overwrite the
     *                     contents of an existing file.
     * @throws IllegalArgumentException if the resource path is null, empty,
     *                                  or points to a nonexistent resource.
     */
    void saveResource(@NotNull String resourcePath, boolean replace);

    /**
     * Discards any data in {@link #getConfig()} and reloads from disk.
     */
    void reloadConfig();

    /**
     * Gets the associated extensionLoader responsible for this extension
     *
     * @return extensionLoader that controls this extension
     */
    @NotNull ExtensionLoader getExtensionLoader();

    /**
     * Returns a value indicating whether this extension is currently
     * enabled
     *
     * @return true if this extension is enabled, otherwise false
     */
    boolean isEnabled();

    /**
     * Called when this extension is disabled
     */
    void onDisable();

    /**
     * Called after a extension is loaded but before it has been enabled.
     * <p>
     * When multiple extensions are loaded, the onLoad() for all extensions is
     * called before any onEnable() is called.
     */
    void onLoad();

    /**
     * Called when this extension is enabled
     */
    void onEnable();

    /**
     * Returns the extension logger associated with this server's logger. The
     * returned logger automatically tags all log messages with the extension's
     * name.
     *
     * @return Logger associated with this extension
     */
    @NotNull Logger getLogger();

    @NotNull ExtensionsImpl getServer();

    /**
     * Returns the name of the extension.
     * <p>
     * This should return the bare name of the extension and should be used for
     * comparison.
     *
     * @return name of the extension
     */
    @NotNull String getName();
}
