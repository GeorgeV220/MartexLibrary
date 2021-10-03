/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.georgev22.api.utilities;

import sun.misc.Unsafe;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * Provides access to {@link URLClassLoader}#addURL.
 * Edited by GeorgeV22 (https://github.com/GeorgeV220) to work with {@link ClassLoader#getSystemClassLoader()}
 */
public abstract class URLClassLoaderAccess {

    /**
     * Creates a {@link URLClassLoaderAccess} for the given class loader.
     *
     * @param classLoader the class loader
     * @return the access object
     */
    public static URLClassLoaderAccess create(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            return URLClassLoaderAccess.create((URLClassLoader) classLoader);
        } else if (ReflectionClassLoader.isSupported()) {
            return new ReflectionClassLoader(classLoader);
        } else if (UnsafeClassLoader.isSupported()) {
            return new UnsafeClassLoader(classLoader);
        } else {
            return Noop.INSTANCE;
        }
    }

    /**
     * Creates a {@link URLClassLoaderAccess} for the given class loader.
     *
     * @param classLoader the class loader
     * @return the access object
     */
    private static URLClassLoaderAccess create(URLClassLoader classLoader) {
        if (ReflectionClassLoader.isSupported()) {
            return new ReflectionClassLoader(classLoader);
        } else if (UnsafeClassLoader.isSupported()) {
            return new UnsafeClassLoader(classLoader);
        } else {
            return Noop.INSTANCE;
        }
    }

    private final ClassLoader classLoader;

    protected URLClassLoaderAccess(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Adds the given URL to the class loader.
     *
     * @param url the URL to add
     */
    public abstract void addURL(@Nonnull URL url);

    /**
     * Accesses using reflection, not supported on Java 9+.
     */
    private static class ReflectionClassLoader extends URLClassLoaderAccess {
        private Method ADD_URL_METHOD;
        private Object UCP;
        private Collection<URL> unopenedURLsCollection;
        private Collection<URL> pathURLsCollection;

        private static boolean isSupported() {
            try {
                Object object = fetchField(ClassLoader.getSystemClassLoader().getClass().getSuperclass(), ClassLoader.getSystemClassLoader(), "ucp");
                return true;
            } catch (NoSuchFieldException | IllegalAccessException | InaccessibleObjectException e) {
                return false;
            }
        }

        ReflectionClassLoader(URLClassLoader classLoader) {
            super(classLoader);
            Method addUrlMethod;
            try {
                addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
            } catch (Exception e2) {
                addUrlMethod = null;
            }
            ADD_URL_METHOD = addUrlMethod;
        }

        ReflectionClassLoader(ClassLoader classLoader) {
            super(classLoader);
            Collection<URL> unopenedURLs;
            Collection<URL> pathURLs;
            Object ucp;
            try {
                ucp = fetchField(ClassLoader.getSystemClassLoader().getClass().getSuperclass(), ClassLoader.getSystemClassLoader(), "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
                unopenedURLsCollection = unopenedURLs;
                pathURLsCollection = pathURLs;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                unopenedURLsCollection = null;
                pathURLsCollection = null;
                e.printStackTrace();
            }

        }

        @Override
        public void addURL(@Nonnull URL url) {
            try {
                ADD_URL_METHOD.invoke(super.classLoader, url);
            } catch (Exception e) {
                unopenedURLsCollection.add(url);
                pathURLsCollection.add(url);
            }
        }

        private static Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException, IllegalAccessException {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        }
    }

    /**
     * Accesses using sun.misc.Unsafe, supported on Java 9+.
     *
     * @author Vaishnav Anil (https://github.com/slimjar/slimjar)
     */
    private static class UnsafeClassLoader extends URLClassLoaderAccess {
        private static final Unsafe UNSAFE;

        static {
            Unsafe unsafe;
            try {
                Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (Unsafe) unsafeField.get(null);
            } catch (Throwable t) {
                unsafe = null;
            }
            UNSAFE = unsafe;
        }

        private static boolean isSupported() {
            return UNSAFE != null;
        }

        private final Collection<URL> unopenedURLs;
        private final Collection<URL> pathURLs;

        UnsafeClassLoader(URLClassLoader classLoader) {
            super(classLoader);

            Collection<URL> unopenedURLs;
            Collection<URL> pathURLs;
            try {
                Object ucp = fetchField(URLClassLoader.class, classLoader, "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e) {
                unopenedURLs = null;
                pathURLs = null;
            }
            this.unopenedURLs = unopenedURLs;
            this.pathURLs = pathURLs;
        }

        UnsafeClassLoader(ClassLoader classLoader) {
            super(classLoader);
            Collection<URL> unopenedURLs;
            Collection<URL> pathURLs;
            try {
                Object ucp = fetchField(ClassLoader.getSystemClassLoader().getClass().getSuperclass(), ClassLoader.getSystemClassLoader(), "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e) {
                e.printStackTrace();
                unopenedURLs = null;
                pathURLs = null;
            }
            this.unopenedURLs = unopenedURLs;
            this.pathURLs = pathURLs;
        }

        private static Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException {
            Field field = clazz.getDeclaredField(name);
            long offset = UNSAFE.objectFieldOffset(field);
            return UNSAFE.getObject(object, offset);
        }

        @Override
        public void addURL(@Nonnull URL url) {
            this.unopenedURLs.add(url);
            this.pathURLs.add(url);
        }
    }

    private static class Noop extends URLClassLoaderAccess {
        private static final Noop INSTANCE = new Noop();

        private Noop() {
            super(null);
        }

        @Override
        public void addURL(@Nonnull URL url) {
            throw new UnsupportedOperationException();
        }
    }

}