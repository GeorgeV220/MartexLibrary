package com.georgev22.api.extensions.java;

import com.georgev22.api.exceptions.InvalidExtensionException;
import com.georgev22.api.extensions.ExtensionBase;
import com.georgev22.api.extensions.ExtensionDescriptionFile;
import com.georgev22.api.extensions.ExtensionLoader;
import com.georgev22.api.extensions.ExtensionsImpl;
import com.georgev22.api.maps.HashObjectMap;
import com.georgev22.api.maps.ObjectMap;
import com.georgev22.api.maps.UnmodifiableObjectMap;
import com.georgev22.api.utilities.Utils;
import com.georgev22.api.yaml.file.FileConfiguration;
import com.georgev22.api.yaml.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaExtension extends ExtensionBase {
    private boolean isEnabled = false;
    private ExtensionLoader loader = null;
    private File file = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private FileConfiguration newConfig = null;
    private File configFile = null;
    private ExtensionsImpl extensionsImpl = null;
    private ExtensionDescriptionFile extensionDescriptionFile;

    private ObjectMap<Class<?>, Class<?>> classClassObjectMap;

    private ObjectMap<Class<?>, Constructor<?>> classConstructorObjectMap;

    public JavaExtension() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ExtensionClassLoader)) {
            throw new IllegalStateException("JavaExtension requires " + ExtensionClassLoader.class.getName());
        }
        ((ExtensionClassLoader) classLoader).initialize(this);
    }

    protected JavaExtension(@NotNull ExtensionLoader loader, @NotNull final File dataFolder, @NotNull ExtensionDescriptionFile extensionDescriptionFile, @NotNull final File file, @NotNull ExtensionsImpl extensionsImpl) {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof ExtensionClassLoader) {
            throw new IllegalStateException("Cannot use initialization constructor at runtime");
        }
        this.extensionDescriptionFile = extensionDescriptionFile;
        init(loader, dataFolder, extensionDescriptionFile, file, classLoader, extensionsImpl);
    }

    /**
     * Returns the folder that the javaExtension data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder.
     */
    @NotNull
    public final File getDataFolder() {
        return dataFolder;
    }


    /**
     * Returns a value indicating whether this javaExtension is currently
     * enabled
     *
     * @return true if this javaExtension is enabled, otherwise false
     */
    public final boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Returns the file which contains this javaExtension
     *
     * @return File containing this javaExtension
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
    public final Reader getTextResource(@NotNull String file) {
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

    /**
     * Gets the associated extensionLoader responsible for this extension
     *
     * @return extensionLoader that controls this extension
     */
    @Override
    public @NotNull ExtensionLoader getExtensionLoader() {
        return loader;
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            extensionsImpl.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    public void saveResource(@NotNull String resourcePath, boolean replace) {
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
                extensionsImpl.getLogger().warning("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            extensionsImpl.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Nullable
    public InputStream getResource(@NotNull String filename) {
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
    public final ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the enabled state of this javaExtension
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

    final void init(@NotNull ExtensionLoader loader, @NotNull File dataFolder, @NotNull ExtensionDescriptionFile extensionDescriptionFile, @NotNull File file, @NotNull ClassLoader classLoader, @NotNull ExtensionsImpl extensionsImpl) {
        this.loader = loader;
        this.file = file;
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;
        this.configFile = new File(dataFolder, "config.yml");
        this.extensionsImpl = extensionsImpl;
        this.extensionDescriptionFile = extensionDescriptionFile;
        this.classClassObjectMap = new HashObjectMap<>();
        this.classConstructorObjectMap = new HashObjectMap<>();
        onLoad();
    }

    public void onLoad() {
    }

    public void onDisable() {
    }

    public void onEnable() {
    }

    @NotNull
    public Logger getLogger() {
        return extensionsImpl.getLogger();
    }

    public @NotNull ExtensionsImpl getServer() {
        return extensionsImpl;
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
     * @throws IllegalStateException    if clazz was not provided by an extension,
     *                                  for example, if called with
     *                                  <code>Extension.getExtension(Extension.class)</code>
     * @throws IllegalStateException    if called from the static initializer for
     *                                  given Extension
     * @throws ClassCastException       if extension that provided the class does not
     *                                  extend the class
     */
    @NotNull
    public static <T extends JavaExtension> T getExtension(@NotNull Class<T> clazz) {
        Utils.Assertions.notNull("Null class cannot have an extension", clazz);
        if (!JavaExtension.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " does not extend " + JavaExtension.class);
        }
        final ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof ExtensionClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not initialized by " + ExtensionClassLoader.class);
        }
        JavaExtension javaExtension = ((ExtensionClassLoader) cl).javaExtension;
        if (javaExtension == null) {
            throw new IllegalStateException("Cannot get extension for " + clazz + " from a static initializer");
        }
        return clazz.cast(javaExtension);
    }

    /**
     * This method provides fast access to the extension that has provided the
     * given class.
     *
     * @param clazz a class belonging to an extension
     * @return the extension that provided the class
     * @throws IllegalArgumentException if the class is not provided by an
     *                                  Extension
     * @throws IllegalArgumentException if class is null
     * @throws IllegalStateException    if called from the static initializer for
     *                                  given Extension
     */
    @NotNull
    public static JavaExtension getProvidingExtension(@NotNull Class<?> clazz) {
        Utils.Assertions.notNull("Null class cannot have an extension", clazz);
        final ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof ExtensionClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not provided by " + ExtensionClassLoader.class);
        }
        JavaExtension javaExtension = ((ExtensionClassLoader) cl).javaExtension;
        if (javaExtension == null) {
            throw new IllegalStateException("Cannot get extension for " + clazz + " from a static initializer");
        }
        return javaExtension;
    }

    @Deprecated(forRemoval = true, since = "7.1.1")
    public void registerInterface(@NotNull Class<?> clazz, Class<?> clazz2) throws InvalidExtensionException {
        if (clazz.isAssignableFrom(clazz2))
            classClassObjectMap.append(clazz, clazz2);
        else throw new InvalidExtensionException("Class " + clazz + " is not assigned from " + clazz2);
    }

    public void registerClassForClass(@NotNull Class<?> clazz, @NotNull Class<?> clazz2) throws InvalidExtensionException {
        if (clazz == null)
            throw new InvalidExtensionException("Class " + clazz + " cannot be null");
        else if (clazz2 == null)
            throw new InvalidExtensionException("Class " + clazz2 + " cannot be null");
        else if (clazz.isAssignableFrom(clazz2))
            classClassObjectMap.append(clazz, clazz2);
        else throw new InvalidExtensionException("Class " + clazz + " is not assigned from " + clazz2);
    }

    public void registerClassesForClass(@NotNull Class<?> classInter, Class<?> @NotNull ... classes) throws InvalidExtensionException {
        for (Class<?> clazz : classes) {
            registerClassForClass(clazz, classInter);
        }
    }

    public void registerClassWithConstructor(@NotNull Class<?> clazz, @NotNull Constructor<?> constructor) throws NoSuchMethodException, InvalidExtensionException {
        if (clazz.getDeclaredConstructor(constructor.getParameterTypes()) != null) {
            throw new InvalidExtensionException("Class " + clazz + " does not have the constructor " + constructor);
        }
        classConstructorObjectMap.append(clazz, constructor);
    }

    @UnmodifiableView
    public ObjectMap<Class<?>, Class<?>> getRegisteredClassForClassMap() {
        return new UnmodifiableObjectMap<>(classClassObjectMap);
    }


    public ObjectMap<Class<?>, Constructor<?>> getRegisteredClassWithConstructorMap() {
        return classConstructorObjectMap;
    }

    public @NotNull ExtensionDescriptionFile getDescription() {
        return extensionDescriptionFile;
    }
}