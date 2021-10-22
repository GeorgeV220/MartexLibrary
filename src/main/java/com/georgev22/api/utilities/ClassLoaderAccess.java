package com.georgev22.api.utilities;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * Provides access to {@link ClassLoader} to add URLs on runtime.
 *
 * <p>Edited by GeorgeV22 (https://github.com/GeorgeV220) to work with {@link ClassLoader}</p>
 * <p></p>
 * Original class: https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/maven/URLClassLoaderAccess.java
 */
public class ClassLoaderAccess {
    private final Collection<URL> unopenedURLs;
    private final Collection<URL> pathURLs;


    /**
     * Creates a {@link ClassLoaderAccess} for the given URLClassLoader.
     *
     * @param classLoader the class loader
     */
    public ClassLoaderAccess(URLClassLoader classLoader) {
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
}
