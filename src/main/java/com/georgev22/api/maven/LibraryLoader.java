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

package com.georgev22.api.maven;

import com.georgev22.api.utilities.ClassLoaderAccess;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.georgev22.api.utilities.Utils.Assertions.notNull;

/**
 * Resolves {@link MavenLibrary} annotations for a class, and loads the dependency
 * into the given ClassLoader.
 */
@NotNull
public final class LibraryLoader {

    private final Class<?> clazz;
    private static ClassLoaderAccess classLoaderAccess;
    private final Logger logger;

    private final File dataFolder;

    private final List<Dependency> dependencyList = new ArrayList<>();

    public <T> LibraryLoader(@NotNull Class<T> clazz, @NotNull URLClassLoader classLoader, @NotNull File dataFolder, @NotNull Logger logger) {
        this.clazz = clazz;
        classLoaderAccess = new ClassLoaderAccess(classLoader);
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    public <T> LibraryLoader(@NotNull Class<T> clazz, @NotNull ClassLoader classLoader, @NotNull File dataFolder, @NotNull Logger logger) {
        this.clazz = clazz;
        classLoaderAccess = new ClassLoaderAccess(classLoader);
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    public <T> LibraryLoader(@NotNull Class<T> clazz, @NotNull URLClassLoader classLoader, @NotNull File dataFolder) {
        this.clazz = clazz;
        classLoaderAccess = new ClassLoaderAccess(classLoader);
        this.logger = Logger.getLogger(clazz.getSimpleName());
        this.dataFolder = dataFolder;
    }

    public <T> LibraryLoader(@NotNull Class<T> clazz, @NotNull ClassLoader classLoader, @NotNull File dataFolder) {
        this.clazz = clazz;
        classLoaderAccess = new ClassLoaderAccess(classLoader);
        this.logger = Logger.getLogger(clazz.getSimpleName());
        this.dataFolder = dataFolder;
    }

    public <T> LibraryLoader(@NotNull Class<T> clazz, @NotNull File dataFolder) {
        this.clazz = clazz;
        classLoaderAccess = new ClassLoaderAccess(clazz.getClassLoader());
        this.logger = Logger.getLogger(clazz.getSimpleName());
        this.dataFolder = dataFolder;
    }

    /**
     * Resolves all {@link MavenLibrary} annotations on the Class.
     */
    public void loadAll() {
        if (clazz == null) {
            throw new RuntimeException("Class is null!");
        }
        loadAll(clazz);
    }

    /**
     * Resolves all {@link MavenLibrary} annotations on the given object.
     *
     * @param object the object to load libraries for.
     */
    public void loadAll(@NotNull Object object) {
        loadAll(object.getClass());
    }

    /**
     * Resolves all {@link MavenLibrary} annotations on the given class.
     *
     * @param clazz the class to load libraries for.
     */
    public <T> void loadAll(@NotNull Class<T> clazz) {
        MavenLibrary[] libs = clazz.getDeclaredAnnotationsByType(MavenLibrary.class);

        for (MavenLibrary lib : libs) {
            load(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().url());
        }
    }

    public void load(String groupId, String artifactId, String version, String repoUrl) {
        load(new Dependency(groupId, artifactId, version, repoUrl));
    }

    /**
     * Load a dependency to the given ClassLoader
     *
     * @param d Dependency object.
     */
    public void load(@NotNull Dependency d) {
        if (dependencyList.contains(d)) {
            logger.warning(String.format("Dependency %s:%s:%s is already loaded!", d.groupId(), d.artifactId(), d.version()));
            return;
        }
        logger.info(String.format("Loading dependency %s:%s:%s from %s", d.groupId(), d.artifactId(), d.version(), d.repoUrl()));

        for (File file : Objects.requireNonNull(getLibFolder().listFiles((dir, name) -> name.endsWith(".jar")))) {
            String[] fileandversion = file.getName().replace(".jar", "").split("_");
            String dependencyName = fileandversion[0];
            String version = fileandversion[1];
            if (d.artifactId().equalsIgnoreCase(dependencyName)) {
                if (!d.version().equalsIgnoreCase(version)) {
                    logger.info("A different version of the dependency exists. Attempting to delete...");
                    if (file.delete()) {
                        logger.info("Dependency '" + dependencyName + "' with version '" + version + "' has been deleted!\nA new version will be downloaded.");
                    }
                }
            }
        }

        String name = d.artifactId() + "_" + d.version();

        File saveLocation = new File(getLibFolder(), name + ".jar");
        if (!saveLocation.exists()) {

            try {
                logger.info("Dependency '" + name + "' does not exist in the libraries folder. Attempting to download...");
                URL url = d.url();

                try (InputStream is = url.openStream()) {
                    Files.copy(is, saveLocation.toPath());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            logger.info("Dependency '" + name + "' successfully downloaded.");
        }

        if (!saveLocation.exists()) {
            throw new RuntimeException("Unable to download '" + d + "' dependency.");
        }

        try {
            classLoaderAccess.add(saveLocation.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to load '" + saveLocation + "' dependency.", e);
        }

        logger.info("Loaded dependency '" + name + "' successfully.");
        dependencyList.add(d);
    }

    @NotNull
    private File getLibFolder() {
        File libs = new File(dataFolder, "libraries");
        if (libs.mkdirs()) {
            logger.info("libraries folder created!");
        }
        return libs;
    }

    @Nullable
    public static ClassLoaderAccess getURLClassLoaderAccess() {
        return classLoaderAccess;
    }

    @NotNull
    public record Dependency(String groupId, String artifactId, String version, String repoUrl) {

        public Dependency(String groupId, String artifactId, String version, String repoUrl) {
            this.groupId = notNull("groupId", groupId);
            this.artifactId = notNull("artifactId", artifactId);
            this.version = notNull("version", version);
            this.repoUrl = notNull("repoUrl", repoUrl);
        }

        @Contract(" -> new")
        public @NotNull URL url() throws MalformedURLException {
            String repo = this.repoUrl;
            if (!repo.endsWith("/")) {
                repo += "/";
            }
            repo += "%s/%s/%s/%s-%s.jar";

            String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
            return new URL(url);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Dependency that)) return false;
            return groupId.equals(that.groupId) && artifactId.equals(that.artifactId) && version.equals(that.version) && repoUrl.equals(that.repoUrl);
        }

        @Override
        public @NotNull String toString() {
            return "LibraryLoader.Dependency(" +
                    "groupId=" + this.groupId() + ", " +
                    "artifactId=" + this.artifactId() + ", " +
                    "version=" + this.version() + ", " +
                    "repoUrl=" + this.repoUrl() + ")";
        }

        /**
         * Coverts {@link Dependency#toString()} to {@link Dependency} instance.
         *
         * @param string String to transform.
         * @return a new Dependency instance.
         */
        @Contract("_ -> new")
        public static @NotNull Dependency fromString(@NotNull String string) {
            String[] arguments = string.split(Pattern.quote("="));
            return new Dependency(arguments[0], arguments[1], arguments[2], arguments[3]);
        }
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "LibraryLoader{" +
                "clazz=" + clazz +
                ", dataFolder=" + dataFolder +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LibraryLoader that)) return false;
        return clazz.equals(that.clazz) && logger.equals(that.logger) && dataFolder.equals(that.dataFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, logger, dataFolder);
    }
}