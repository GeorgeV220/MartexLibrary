package com.georgev22.api.extensions;

import com.georgev22.api.utilities.Utils;
import com.georgev22.api.yaml.file.FileConfiguration;
import com.georgev22.api.yaml.file.YamlConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public abstract class Extension {
    private boolean isEnabled = false;
    private File file = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private FileConfiguration newConfig = null;
    private File configFile = null;
    private Logger logger = null;
    private ExtensionDescriptionFile extensionDescriptionFile;

    public Extension() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ExtensionClassLoader)) {
            throw new IllegalStateException("Extension requires " + ExtensionClassLoader.class.getName());
        }
        ((ExtensionClassLoader) classLoader).initialize(this);
    }

    protected Extension(@NotNull final File dataFolder, @NotNull ExtensionDescriptionFile extensionDescriptionFile, @NotNull final File file) {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof ExtensionClassLoader) {
            throw new IllegalStateException("Cannot use initialization constructor at runtime");
        }
        this.extensionDescriptionFile = extensionDescriptionFile;
        init(dataFolder, extensionDescriptionFile, file, classLoader);
    }

    /**
     * Returns the folder that the extension data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder.
     */
    @NotNull
    public final File getDataFolder() {
        return dataFolder;
    }


    /**
     * Returns a value indicating whether or not this extension is currently
     * enabled
     *
     * @return true if this extension is enabled, otherwise false
     */
    public final boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Returns the file which contains this extension
     *
     * @return File containing this extension
     */
    @NotNull
    protected File getFile() {
        return file;
    }

    @NotNull
    public FileConfiguration getConfig() {
        if (newConfig == null) {
            reloadConfig();
        }
        return newConfig;
    }

    /**
     * Provides a reader for a text file located inside the jar.
     * <p>
     * The returned reader will read text with the UTF-8 charset.
     *
     * @param file the filename of the resource to load
     * @return null if {@link #getResource(String)} returns null
     * @throws IllegalArgumentException if file is null
     * @see ClassLoader#getResourceAsStream(String)
     */
    @Nullable
    protected final Reader getTextResource(@NotNull String file) {
        final InputStream in = getResource(file);

        return in == null ? null : new InputStreamReader(in, StandardCharsets.UTF_8);
    }

    public void reloadConfig() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }

        newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logger.error("Could not save config to " + configFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.error("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Nullable
    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Returns the ClassLoader which holds this extension
     *
     * @return ClassLoader holding this extension
     */
    @NotNull
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the enabled state of this extension
     *
     * @param enabled true if enabled, otherwise false
     */
    protected final void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;

            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    final void init(@NotNull File dataFolder, @NotNull ExtensionDescriptionFile extensionDescriptionFile, @NotNull File file, @NotNull ClassLoader classLoader) {
        this.file = file;
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;
        this.configFile = new File(dataFolder, "config.yml");
        Configurator.setRootLevel(Level.ALL);
        this.logger = LogManager.getLogger(getName());
        this.extensionDescriptionFile = extensionDescriptionFile;
        try {
            ExtensionManager.load(this);
            ExtensionManager.enable(this);
        } catch (Exception exception) {
            exception.printStackTrace();
            ExtensionManager.disable(this);
        }
    }

    public void onLoad() {
    }

    public void onDisable() {
    }

    public void onEnable() {
    }

    @NotNull
    public Logger getLogger() {
        return logger;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }

    /**
     * This method provides fast access to the extension that has {@link
     * #getProvidingExtension(Class) provided} the given extension class, which is
     * usually the extension that implemented it.
     * <p>
     * An exception to this would be if extension's jar that contained the class
     * does not extend the class, where the intended extension would have
     * resided in a different jar / classloader.
     *
     * @param <T>   a class that extends Extension
     * @param clazz the class desired
     * @return the extension that provides and implements said class
     * @throws IllegalArgumentException if clazz is null
     * @throws IllegalArgumentException if clazz does not extend {@link
     *                                  Extension}
     * @throws IllegalStateException    if clazz was not provided by a extension,
     *                                  for example, if called with
     *                                  <code>Extension.getExtension(Extension.class)</code>
     * @throws IllegalStateException    if called from the static initializer for
     *                                  given Extension
     * @throws ClassCastException       if extension that provided the class does not
     *                                  extend the class
     */
    @NotNull
    public static <T extends Extension> T getExtension(@NotNull Class<T> clazz) {
        Utils.Assertions.notNull("Null class cannot have an extension", clazz);
        if (!Extension.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " does not extend " + Extension.class);
        }
        final ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof ExtensionClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not initialized by " + ExtensionClassLoader.class);
        }
        Extension extension = ((ExtensionClassLoader) cl).extension;
        if (extension == null) {
            throw new IllegalStateException("Cannot get extension for " + clazz + " from a static initializer");
        }
        return clazz.cast(extension);
    }

    /**
     * This method provides fast access to the extension that has provided the
     * given class.
     *
     * @param clazz a class belonging to a extension
     * @return the extension that provided the class
     * @throws IllegalArgumentException if the class is not provided by a
     *                                  Extension
     * @throws IllegalArgumentException if class is null
     * @throws IllegalStateException    if called from the static initializer for
     *                                  given Extension
     */
    @NotNull
    public static Extension getProvidingExtension(@NotNull Class<?> clazz) {
        Utils.Assertions.notNull("Null class cannot have an extension", clazz);
        final ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof ExtensionClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not provided by " + ExtensionClassLoader.class);
        }
        Extension extension = ((ExtensionClassLoader) cl).extension;
        if (extension == null) {
            throw new IllegalStateException("Cannot get extension for " + clazz + " from a static initializer");
        }
        return extension;
    }

    public abstract String getName();
}