package com.georgev22.library.extensions.java;

import com.georgev22.library.extensions.*;
import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

/**
 * A ClassLoader for extensions, to allow shared classes across multiple extensions
 */
public final class ExtensionClassLoader extends URLClassLoader {
    private final ObjectMap<String, Class<?>> classes = new ConcurrentObjectMap<>();
    private final File dataFolder;
    private final File file;
    private final ExtensionDescriptionFile extensionDescriptionFile;
    private final JarFile jar;
    private final Manifest manifest;
    private final URL url;
    final JavaExtension javaExtension;
    private JavaExtension javaExtensionInit;
    private final JavaExtensionLoader loader;
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
        this.manifest = jar.getManifest();
        this.url = file.toURI().toURL();
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

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    Class<?> loadClass0(@NotNull String name, boolean resolve, boolean checkGlobal) throws ClassNotFoundException {
        try {
            Class<?> result = super.loadClass(name, resolve);

            if (checkGlobal || result.getClassLoader() == this) {
                return result;
            }
        } catch (ClassNotFoundException ignored) {
        }

        if (checkGlobal) {

            Class<?> result = loader.getClassByName(name, resolve, extensionDescriptionFile);

            if (result != null) {

                if (result.getClassLoader() instanceof ExtensionClassLoader) {
                    ExtensionDescriptionFile provider = ((ExtensionClassLoader) result.getClassLoader()).extensionDescriptionFile;

                    if (provider != extensionDescriptionFile
                            && !seenIllegalAccess.contains(provider.getName())
                            && !((SimpleExtensionManager) loader.extensionsImpl.getExtensionManager()).isTransitiveDepend(extensionDescriptionFile, provider)) {

                        seenIllegalAccess.add(provider.getName());
                        if (javaExtension != null) {
                            javaExtension.getLogger().log(Level.WARNING, "Loaded class {0} from {1} which is not a depend or softdepend of this extension.", new Object[]{name, provider.getFullName()});
                        } else {
                            // In case the bad access occurs on construction
                            loader.extensionsImpl.getLogger().log(Level.WARNING, "[{0}] Loaded class {1} from {2} which is not a depend or softdepend of this extension.", new Object[]{extensionDescriptionFile.getName(), name, provider.getFullName()});
                        }
                    }
                }

                return result;
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            String path = name.replace('.', '/').concat(".class");
            JarEntry entry = jar.getJarEntry(path);

            if (entry != null) {
                byte[] classBytes;

                try (InputStream is = jar.getInputStream(entry)) {
                    classBytes = ByteStreams.toByteArray(is);
                } catch (IOException ex) {
                    throw new ClassNotFoundException(name, ex);
                }

                int dot = name.lastIndexOf('.');
                if (dot != -1) {
                    String pkgName = name.substring(0, dot);
                    if (getPackage(pkgName) == null) {
                        try {
                            if (manifest != null) {
                                definePackage(pkgName, manifest, url);
                            } else {
                                definePackage(pkgName, null, null, null, null, null, null, null);
                            }
                        } catch (IllegalArgumentException ex) {
                            if (getPackage(pkgName) == null) {
                                throw new IllegalStateException("Cannot find package " + pkgName);
                            }
                        }
                    }
                }

                CodeSigner[] signers = entry.getCodeSigners();
                CodeSource source = new CodeSource(url, signers);

                result = defineClass(name, classBytes, 0, classBytes.length, source);
            }

            if (result == null) {
                result = super.findClass(name);
            }

            classes.put(name, result);
        }

        return result;
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
