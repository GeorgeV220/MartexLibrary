package com.georgev22.library.utilities;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * Provides access to {@link ClassLoader} to add URLs on runtime.
 *
 * <p>Edited by <a href="https://github.com/GeorgeV220">GeorgeV22</a> to work with {@link ClassLoader}</p>
 * <p></p>
 * Original class: <a href="https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/maven/URLClassLoaderAccess.java">https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/maven/URLClassLoaderAccess.java</a>
 */
public class ClassLoaderAccess {
    private final Collection<URL> unopenedURLs;
    private final Collection<URL> pathURLs;

    private final ClassLoader classLoader;


    /**
     * Creates a {@link ClassLoaderAccess} for the given URLClassLoader.
     *
     * @param classLoader the class loader
     */
    public ClassLoaderAccess(URLClassLoader classLoader) {
        this.classLoader = classLoader;
        Collection<URL> unopenedURLs;
        Collection<URL> pathURLs;
        try {
            Object ucp = Utils.Reflection.fetchField(classLoader.getClass(), classLoader, "ucp");
            unopenedURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "unopenedUrls");
            pathURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "path");
        } catch (Throwable e) {
            try {
                Object ucp = Utils.Reflection.fetchField(classLoader.getClass(), classLoader, "ucp");
                unopenedURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "urls");
                pathURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e1) {
                unopenedURLs = null;
                pathURLs = null;
                e.printStackTrace();
                e1.printStackTrace();
            }
        }
        this.unopenedURLs = unopenedURLs;
        this.pathURLs = pathURLs;
    }

    /**
     * Creates a {@link ClassLoaderAccess} for the given ClassLoader.
     *
     * @param classLoader the class loader
     */
    public ClassLoaderAccess(ClassLoader classLoader) {
        this.classLoader = classLoader;
        Collection<URL> unopenedURLs;
        Collection<URL> pathURLs;
        try {
            Object ucp = Utils.Reflection.fetchField(classLoader.getClass().getSuperclass(), classLoader, "ucp");
            unopenedURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "unopenedUrls");
            pathURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "path");
        } catch (Throwable e) {
            try {
                Object ucp = Utils.Reflection.fetchField(classLoader.getClass().getSuperclass(), classLoader, "ucp");
                unopenedURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "urls");
                pathURLs = (Collection<URL>) Utils.Reflection.fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e1) {
                unopenedURLs = null;
                pathURLs = null;
                e.printStackTrace();
                e1.printStackTrace();
            }
        }
        this.unopenedURLs = unopenedURLs;
        this.pathURLs = pathURLs;
    }

    /**
     * Adds the given URL to the class loader.
     *
     * @param url the URL to add
     */
    public void add(@NotNull URL url) {
        this.unopenedURLs.add(url);
        this.pathURLs.add(url);
    }

    /**
     * Removes the given URL from the class loader.
     *
     * @param url the URL to remove
     */
    public void remove(@NotNull URL url) {
        this.unopenedURLs.remove(url);
        this.pathURLs.remove(url);
    }

    public Collection<URL> getPathURLs() {
        return pathURLs;
    }

    public Collection<URL> getUnopenedURLs() {
        return unopenedURLs;
    }

    @Override
    public String toString() {
        return "ClassLoaderAccess{" +
                "unopenedURLs=" + unopenedURLs +
                ", pathURLs=" + pathURLs +
                ", classLoader=" + classLoader.getClass().getPackage().getName() + "." + classLoader.getName() +
                '}';
    }
}
