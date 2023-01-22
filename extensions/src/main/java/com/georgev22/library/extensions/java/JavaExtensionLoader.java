package com.georgev22.library.extensions.java;

import com.georgev22.library.exceptions.InvalidDescriptionException;
import com.georgev22.library.exceptions.InvalidExtensionException;
import com.georgev22.library.extensions.Extension;
import com.georgev22.library.extensions.ExtensionDescriptionFile;
import com.georgev22.library.extensions.ExtensionLoader;
import com.georgev22.library.extensions.ExtensionsImpl;
import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.UnmodifiableObjectMap;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Represents a Java extension loader, allowing extensions in the form of .jar
 */
public final class JavaExtensionLoader implements ExtensionLoader {
    private final Pattern[] fileFilters = new Pattern[]{Pattern.compile("\\.jar$")};
    private final List<ExtensionClassLoader> loaders = new CopyOnWriteArrayList<>();
    private final ObjectMap<String, Extension> extensionObjectMap = new ConcurrentObjectMap<>();

    private final ExtensionsImpl extensionsImpl;

    /**
     * This class was not meant to be constructed explicitly
     */
    public JavaExtensionLoader(ExtensionsImpl extensionsImpl) {
        this.extensionsImpl = extensionsImpl;
    }

    @Override
    @NotNull
    public JavaExtension loadExtension(@NotNull final File file) throws InvalidExtensionException {
        Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            throw new InvalidExtensionException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        final ExtensionDescriptionFile description;
        try {
            description = getExtensionDescription(file);
        } catch (InvalidDescriptionException ex) {
            throw new InvalidExtensionException(ex);
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, description.getName());
        final File oldDataFolder = new File(parentFile, description.getRawName());

        extensionsImpl.getLogger().log(Level.INFO, String.format(
                "Loading extension %s",
                description.getName()
        ));

        // Found old data folder
        if (dataFolder.equals(oldDataFolder)) {
            // They are equal -- nothing needs to be done!
        } else if (dataFolder.isDirectory() && oldDataFolder.isDirectory()) {
            extensionsImpl.getLogger().warning(String.format(
                    "While loading %s (%s) found old-data folder: `%s' next to the new one `%s'",
                    description.getFullName(),
                    file,
                    oldDataFolder,
                    dataFolder
            ));
        } else if (oldDataFolder.isDirectory() && !dataFolder.exists()) {
            if (!oldDataFolder.renameTo(dataFolder)) {
                throw new InvalidExtensionException("Unable to rename old data folder: `" + oldDataFolder + "' to: `" + dataFolder + "'");
            }
            extensionsImpl.getLogger().log(Level.INFO, String.format(
                    "While loading %s (%s) renamed data folder: `%s' to `%s'",
                    description.getFullName(),
                    file,
                    oldDataFolder,
                    dataFolder
            ));
        }

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidExtensionException(String.format(
                    "Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                    dataFolder,
                    description.getFullName(),
                    file
            ));
        }

        for (final String extensionName : description.getDepend()) {
            Extension current = extensionObjectMap.get(extensionName);

            if (current == null) {
                throw new InvalidExtensionException("Unknown dependency " + extensionName + ". Please download and install " + extensionName + " to run this extension.");
            }
        }

        if (extensionObjectMap.containsKey(description.getName())) {
            throw new InvalidExtensionException("Extension " + description.getName() + " is already loaded.");
        }

        final ExtensionClassLoader loader;
        try {
            loader = new ExtensionClassLoader(this, getClass().getClassLoader(), description, dataFolder, file, extensionsImpl);
        } catch (Throwable ex) {
            throw new InvalidExtensionException(ex);
        }

        loaders.add(loader);
        extensionObjectMap.append(loader.javaExtension.getName(), loader.javaExtension);
        return loader.javaExtension;
    }

    @Override
    @NotNull
    public ExtensionDescriptionFile getExtensionDescription(@NotNull File file) throws InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("extension.yml");

            if (entry == null) {
                throw new FileNotFoundException("Jar does not contain extension.yml");
            }

            stream = jar.getInputStream(entry);

            return new ExtensionDescriptionFile(stream);

        } catch (IOException | YAMLException ex) {
            throw new InvalidDescriptionException(ex);
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
    }

    @Override
    @NotNull
    public Pattern[] getExtensionFileFilters() {
        return fileFilters.clone();
    }

    @Override
    public void enableExtension(@NotNull final Extension extension) {
        Preconditions.checkArgument(extension instanceof JavaExtension, "Extension is not associated with this ExtensionLoader");
        if (!extension.isEnabled()) {
            extension.getLogger().info(String.format("Enabling %s", extension.getDescription().getFullName()));

            JavaExtension javaExtension = (JavaExtension) extension;

            ExtensionClassLoader extensionLoader = (ExtensionClassLoader) javaExtension.getClassLoader();

            if (!loaders.contains(extensionLoader)) {
                loaders.add(extensionLoader);
                extension.getLogger().log(Level.WARNING, "Enabled extension with unregistered ExtensionClassLoader " + extension.getDescription().getFullName());
            }

            try {
                javaExtension.setEnabled(true);
            } catch (Throwable ex) {
                extension.getLogger().log(Level.SEVERE, "Error occurred while enabling " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    @Override
    public void disableExtension(@NotNull Extension extension) {
        Preconditions.checkArgument(extension instanceof JavaExtension, "Extension is not associated with this ExtensionLoader");
        if (extension.isEnabled()) {
            extension.getLogger().info(String.format("Disabling %s", extension.getDescription().getFullName()));

            try {
                JavaExtension javaExtension = (JavaExtension) extension;
                javaExtension.setEnabled(false);
            } catch (Throwable ex) {
                extension.getLogger().log(Level.SEVERE, "Error occurred while disabling " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    @Override
    public void unloadExtension(@NotNull Extension extension) {
        Preconditions.checkArgument(extension instanceof JavaExtension, "Extension is not associated with this ExtensionLoader");
        if (extension.isEnabled()) {
            extension.getLogger().info(String.format("Unloading %s", extension.getDescription().getFullName()));

            JavaExtension javaExtension = (JavaExtension) extension;
            ClassLoader cloader = javaExtension.getClassLoader();

            try {
                javaExtension.setEnabled(false);
            } catch (Throwable ex) {
                extension.getLogger().log(Level.SEVERE, "Error occurred while unloading " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            if (cloader instanceof ExtensionClassLoader) {
                ExtensionClassLoader loader = (ExtensionClassLoader) cloader;
                loaders.remove(loader);
                extensionObjectMap.remove(extension.getName());
                try {
                    loader.close();
                } catch (IOException ex) {
                    //
                }
            }
        }
    }

    @Override
    @Contract(" -> new")
    public @NotNull @UnmodifiableView ObjectMap<String, Extension> getExtensions() {
        return new UnmodifiableObjectMap<>(extensionObjectMap);
    }
}