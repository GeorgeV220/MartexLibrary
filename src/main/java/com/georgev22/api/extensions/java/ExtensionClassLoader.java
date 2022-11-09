package com.georgev22.api.extensions.java;

import com.georgev22.api.extensions.Extension;
import com.georgev22.api.extensions.ExtensionDescriptionFile;
import com.georgev22.api.extensions.ExtensionLoader;
import com.georgev22.api.extensions.ExtensionsImpl;
import com.georgev22.api.maps.ConcurrentObjectMap;
import com.georgev22.api.maps.ObjectMap;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * A ClassLoader for extensions, to allow shared classes across multiple extensions
 */
public final class ExtensionClassLoader extends URLClassLoader {
    private final ObjectMap<String, Class<?>> classes = new ConcurrentObjectMap<>();
    private final File dataFolder;
    private final File file;
    private final ExtensionDescriptionFile extensionDescriptionFile;
    private final JarFile jar;
    final JavaExtension javaExtension;
    private JavaExtension javaExtensionInit;
    private final ExtensionLoader loader;
    private IllegalStateException extensionState;
    private final ExtensionsImpl extensionsImpl;
    private final Set<String> seenIllegalAccess = Collections.newSetFromMap(new ConcurrentObjectMap<>());

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ExtensionClassLoader(@NotNull final JavaExtensionLoader loader, @Nullable final ClassLoader parent, @NotNull ExtensionDescriptionFile extensionDescriptionFile, @NotNull final File dataFolder, @NotNull final File file, @NotNull ExtensionsImpl extensionsImpl) throws Exception {
        super(new URL[]{file.toURI().toURL()}, parent);
        Preconditions.checkArgument(loader != null, "Loader cannot be null");
        this.loader = loader;
        this.dataFolder = dataFolder;
        this.file = file;
        this.jar = new JarFile(file);
        this.extensionDescriptionFile = extensionDescriptionFile;
        this.extensionsImpl = extensionsImpl;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(extensionDescriptionFile.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new Exception("Cannot find main class `" + extensionDescriptionFile.getMain() + "'", ex);
            }

            Class<? extends JavaExtension> extensionClass;
            try {
                extensionClass = jarClass.asSubclass(JavaExtension.class);
            } catch (ClassCastException ex) {

                throw new Exception("main class `" + extensionDescriptionFile.getMain() + "' does not extend " + Extension.class.getName() + " (" + jarClass.getSuperclass().getName() + ")", ex);
            }

            javaExtension = extensionClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new Exception("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new Exception("Abnormal extension type", ex);
        }
    }

    @Override
    public URL getResource(String name) {
        return findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            jar.close();
        }
    }

    @NotNull
    Collection<Class<?>> getClasses() {
        return classes.values();
    }

    synchronized void initialize(@NotNull JavaExtension javaExtension) {
        Validate.notNull(javaExtension, "Initializing JavaExtension cannot be null");
        Validate.isTrue(javaExtension.getClass().getClassLoader() == this, "Cannot initialize JavaExtension outside of this class loader");
        if (this.javaExtension != null || this.javaExtensionInit != null) {
            throw new IllegalArgumentException("JavaExtension already initialized!", extensionState);
        }

        extensionState = new IllegalStateException("Initial initialization");
        this.javaExtensionInit = javaExtension;

        javaExtension.init(loader, dataFolder, extensionDescriptionFile, file, this, extensionsImpl);
    }
}
