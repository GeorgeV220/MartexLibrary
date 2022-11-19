package com.georgev22.library.extensions;

import com.georgev22.library.exceptions.InvalidDescriptionException;
import com.georgev22.library.exceptions.InvalidExtensionException;
import com.georgev22.library.exceptions.UnknownDependencyException;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.google.common.base.Preconditions;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleExtensionManager implements ExtensionManager {

    private final ExtensionsImpl extensionsImpl;
    private final Map<Pattern, ExtensionLoader> fileAssociations = new HashMap<>();
    private final List<Extension> extensions = new ArrayList<>();
    private final Map<String, Extension> lookupNames = new HashMap<>();
    private MutableGraph<String> dependencyGraph = GraphBuilder.directed().build();
    private final ObjectMap<String, Object> instances = new HashObjectMap<>();

    public SimpleExtensionManager(@NotNull ExtensionsImpl instance) {
        extensionsImpl = instance;
    }

    /**
     * Registers the specified extension loader
     *
     * @param loader Class name of the ExtensionLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a
     *                                  valid ExtensionLoader
     */
    @Override
    public void registerInterface(@NotNull Class<? extends ExtensionLoader> loader) throws IllegalArgumentException {
        ExtensionLoader instance;

        if (ExtensionLoader.class.isAssignableFrom(loader)) {
            Constructor<? extends ExtensionLoader> constructor;

            try {
                constructor = loader.getConstructor(ExtensionsImpl.class);
                instance = constructor.newInstance(extensionsImpl);
            } catch (NoSuchMethodException ex) {
                String className = loader.getName();

                throw new IllegalArgumentException(String.format("Class %s does not have a public %s(ExtensionsImpl) constructor", className, className), ex);
            } catch (Exception ex) {
                throw new IllegalArgumentException(String.format("Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName()), ex);
            }
        } else {
            throw new IllegalArgumentException(String.format("Class %s does not implement interface ExtensionLoader", loader.getName()));
        }

        Pattern[] patterns = instance.getExtensionFileFilters();

        synchronized (this) {
            for (Pattern pattern : patterns) {
                fileAssociations.put(pattern, instance);
            }
        }
    }

    /**
     * Registers the specified class instance
     *
     * @param instance Instance to be registered
     */
    @Override
    public <T> void registerClassInstance(@NotNull String name, @NotNull T instance) {
        instances.append(name, instance);
    }

    /**
     * Get registered class instances
     *
     * @return an {@link ObjectMap} with the registered class instances.
     */
    @Override
    public ObjectMap<String, Object> getClassInstances() {
        return instances;
    }

    /**
     * Loads the extensions contained within the specified directory
     *
     * @param directory Directory to check for extensions
     * @return A list of all extensions loaded
     */
    @Override
    @NotNull
    public Extension[] loadExtensions(@NotNull File directory) {
        Preconditions.checkArgument(directory != null, "Directory cannot be null");
        Preconditions.checkArgument(directory.isDirectory(), "Directory must be a directory");

        List<Extension> result = new ArrayList<>();
        Set<Pattern> filters = fileAssociations.keySet();

        Map<String, File> extensions = new HashMap<>();
        Set<String> loadedExtensions = new HashSet<>();
        Map<String, String> extensionsProvided = new HashMap<>();
        Map<String, Collection<String>> dependencies = new HashMap<>();
        Map<String, Collection<String>> softDependencies = new HashMap<>();

        // This is where it figures out all possible extensions
        for (File file : directory.listFiles()) {
            ExtensionLoader loader = null;
            for (Pattern filter : filters) {
                Matcher match = filter.matcher(file.getName());
                if (match.find()) {
                    loader = fileAssociations.get(filter);
                }
            }

            if (loader == null) continue;

            ExtensionDescriptionFile description;
            try {
                description = loader.getExtensionDescription(file);
                String name = description.getName();
                if (description.rawName.indexOf(' ') != -1) {
                    extensionsImpl.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': uses the space-character (0x20) in its name");
                    continue;
                }
            } catch (InvalidDescriptionException ex) {
                extensionsImpl.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                continue;
            }

            File replacedFile = extensions.put(description.getName(), file);
            if (replacedFile != null) {
                extensionsImpl.getLogger().severe(String.format(
                        "Ambiguous extension name `%s' for files `%s' and `%s' in `%s'",
                        description.getName(),
                        file.getPath(),
                        replacedFile.getPath(),
                        directory.getPath()
                ));
            }

            String removedProvided = extensionsProvided.remove(description.getName());
            if (removedProvided != null) {
                extensionsImpl.getLogger().warning(String.format(
                        "Ambiguous extension name `%s'. It is also provided by `%s'",
                        description.getName(),
                        removedProvided
                ));
            }

            for (String provided : description.getProvides()) {
                File extensionFile = extensions.get(provided);
                if (extensionFile != null) {
                    extensionsImpl.getLogger().warning(String.format(
                            "`%s provides `%s' while this is also the name of `%s' in `%s'",
                            file.getPath(),
                            provided,
                            extensionFile.getPath(),
                            directory.getPath()
                    ));
                } else {
                    String replacedExtension = extensionsProvided.put(provided, description.getName());
                    if (replacedExtension != null) {
                        extensionsImpl.getLogger().warning(String.format(
                                "`%s' is provided by both `%s' and `%s'",
                                provided,
                                description.getName(),
                                replacedExtension
                        ));
                    }
                }
            }

            Collection<String> softDependencySet = description.getSoftDepend();
            if (softDependencySet != null && !softDependencySet.isEmpty()) {
                if (softDependencies.containsKey(description.getName())) {
                    // Duplicates do not matter, they will be removed together if applicable
                    softDependencies.get(description.getName()).addAll(softDependencySet);
                } else {
                    softDependencies.put(description.getName(), new LinkedList<>(softDependencySet));
                }

                for (String depend : softDependencySet) {
                    dependencyGraph.putEdge(description.getName(), depend);
                }
            }

            Collection<String> dependencySet = description.getDepend();
            if (dependencySet != null && !dependencySet.isEmpty()) {
                dependencies.put(description.getName(), new LinkedList<>(dependencySet));

                for (String depend : dependencySet) {
                    dependencyGraph.putEdge(description.getName(), depend);
                }
            }

            Collection<String> loadBeforeSet = description.getLoadBefore();
            if (loadBeforeSet != null && !loadBeforeSet.isEmpty()) {
                for (String loadBeforeTarget : loadBeforeSet) {
                    if (softDependencies.containsKey(loadBeforeTarget)) {
                        softDependencies.get(loadBeforeTarget).add(description.getName());
                    } else {
                        // softDependencies is never iterated, so 'ghost' extensions aren't an issue
                        Collection<String> shortSoftDependency = new LinkedList<>();
                        shortSoftDependency.add(description.getName());
                        softDependencies.put(loadBeforeTarget, shortSoftDependency);
                    }

                    dependencyGraph.putEdge(loadBeforeTarget, description.getName());
                }
            }
        }

        while (!extensions.isEmpty()) {
            boolean missingDependency = true;
            Iterator<Map.Entry<String, File>> extensionIterator = extensions.entrySet().iterator();

            while (extensionIterator.hasNext()) {
                Map.Entry<String, File> entry = extensionIterator.next();
                String extension = entry.getKey();

                if (dependencies.containsKey(extension)) {
                    Iterator<String> dependencyIterator = dependencies.get(extension).iterator();

                    while (dependencyIterator.hasNext()) {
                        String dependency = dependencyIterator.next();

                        // Dependency loaded
                        if (loadedExtensions.contains(dependency)) {
                            dependencyIterator.remove();

                            // We have a dependency not found
                        } else if (!extensions.containsKey(dependency) && !extensionsProvided.containsKey(dependency)) {
                            missingDependency = false;
                            extensionIterator.remove();
                            softDependencies.remove(extension);
                            dependencies.remove(extension);

                            extensionsImpl.getLogger().log(
                                    Level.SEVERE,
                                    "Could not load '" + entry.getValue().getPath() + "' in folder '" + directory.getPath() + "'",
                                    new UnknownDependencyException("Unknown dependency " + dependency + ". Please download and install " + dependency + " to run this extension."));
                            break;
                        }
                    }

                    if (dependencies.containsKey(extension) && dependencies.get(extension).isEmpty()) {
                        dependencies.remove(extension);
                    }
                }
                if (softDependencies.containsKey(extension)) {

                    // Soft depend is no longer around
                    softDependencies.get(extension).removeIf(softDependency -> !extensions.containsKey(softDependency) && !extensionsProvided.containsKey(softDependency));

                    if (softDependencies.get(extension).isEmpty()) {
                        softDependencies.remove(extension);
                    }
                }
                if (!(dependencies.containsKey(extension) || softDependencies.containsKey(extension)) && extensions.containsKey(extension)) {
                    // We're clear to load, no more soft or hard dependencies left
                    File file = extensions.get(extension);
                    extensionIterator.remove();
                    missingDependency = false;

                    try {
                        Extension loadedExtension = loadExtension(file);
                        if (loadedExtension != null) {
                            result.add(loadedExtension);
                            loadedExtensions.add(loadedExtension.getName());
                            loadedExtensions.addAll(loadedExtension.getDescription().getProvides());
                        } else {
                            extensionsImpl.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'");
                        }
                    } catch (InvalidExtensionException ex) {
                        extensionsImpl.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                    }
                }
            }

            if (missingDependency) {
                // We now iterate over extensions until something loads
                // This loop will ignore soft dependencies
                extensionIterator = extensions.entrySet().iterator();

                while (extensionIterator.hasNext()) {
                    Map.Entry<String, File> entry = extensionIterator.next();
                    String extension = entry.getKey();

                    if (!dependencies.containsKey(extension)) {
                        softDependencies.remove(extension);
                        missingDependency = false;
                        File file = entry.getValue();
                        extensionIterator.remove();

                        try {
                            Extension loadedExtension = loadExtension(file);
                            if (loadedExtension != null) {
                                result.add(loadedExtension);
                                loadedExtensions.add(loadedExtension.getName());
                                loadedExtensions.addAll(loadedExtension.getDescription().getProvides());
                            } else {
                                extensionsImpl.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'");
                            }
                            break;
                        } catch (InvalidExtensionException ex) {
                            extensionsImpl.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                        }
                    }
                }
                // We have no extensions left without a depend
                if (missingDependency) {
                    softDependencies.clear();
                    dependencies.clear();
                    Iterator<File> failedExtensionIterator = extensions.values().iterator();

                    while (failedExtensionIterator.hasNext()) {
                        File file = failedExtensionIterator.next();
                        failedExtensionIterator.remove();
                        extensionsImpl.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': circular dependency detected");
                    }
                }
            }
        }

        return result.toArray(new Extension[0]);
    }

    /**
     * Loads the extension in the specified file
     * <p>
     * File must be valid according to the current enabled Extension interfaces
     *
     * @param file File containing the extension to load
     * @return The Extension loaded, or null if it was invalid
     * @throws InvalidExtensionException  Thrown when the specified file is not a
     *                                    valid extension
     * @throws UnknownDependencyException If a required dependency could not
     *                                    be found
     */
    @Override
    @Nullable
    public synchronized Extension loadExtension(@NotNull File file) throws InvalidExtensionException, UnknownDependencyException {
        Preconditions.checkArgument(file != null, "File cannot be null");

        Set<Pattern> filters = fileAssociations.keySet();
        Extension result = null;

        for (Pattern filter : filters) {
            String name = file.getName();
            Matcher match = filter.matcher(name);

            if (match.find()) {
                ExtensionLoader loader = fileAssociations.get(filter);

                result = loader.loadExtension(file);
            }
        }

        if (result != null) {
            extensions.add(result);
            lookupNames.put(result.getDescription().getName(), result);
            for (String provided : result.getDescription().getProvides()) {
                lookupNames.putIfAbsent(provided, result);
            }
        }

        return result;
    }

    /**
     * This method copies one file to another location
     *
     * @param inFile  the source filename
     * @param outFile the target filename
     * @return true on success
     */
    private boolean copy(@NotNull File inFile, @NotNull File outFile) {
        if (!inFile.exists()) {
            return false;
        }

        FileChannel in = null;
        FileChannel out = null;

        try {
            in = new FileInputStream(inFile).getChannel();
            out = new FileOutputStream(outFile).getChannel();

            long pos = 0;
            long size = in.size();

            while (pos < size) {
                pos += in.transferTo(pos, 10 * 1024 * 1024, out);
            }
        } catch (IOException ioe) {
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                return false;
            }
        }

        return true;

    }

    /**
     * Checks if the given extension is loaded and returns it when applicable
     * <p>
     * Please note that the name of the extension is case-sensitive
     *
     * @param name Name of the extension to check
     * @return Extension if it exists, otherwise null
     */
    @Override
    @Nullable
    public synchronized Extension getExtension(@NotNull String name) {
        return lookupNames.get(name.replace(' ', '_'));
    }

    /**
     * Gets a list of all currently loaded extensions
     *
     * @return Array of Extensions
     */
    @Override
    @NotNull
    public synchronized Extension[] getExtensions() {
        return extensions.toArray(new Extension[0]);
    }

    /**
     * Checks if the given extension is enabled or not
     * <p>
     * Please note that the name of the extension is case-sensitive.
     *
     * @param name Name of the extension to check
     * @return true if the extension is enabled, otherwise false
     */
    @Override
    public boolean isExtensionEnabled(@NotNull String name) {
        Extension extension = getExtension(name);

        return isExtensionEnabled(extension);
    }

    /**
     * Checks if the given extension is enabled or not
     *
     * @param extension Extension to check
     * @return true if the extension is enabled, otherwise false
     */
    @Override
    public boolean isExtensionEnabled(@Nullable Extension extension) {
        if ((extension != null) && (extensions.contains(extension))) {
            return extension.isEnabled();
        } else {
            return false;
        }
    }

    /**
     * Enables the specified extension
     * <p>
     * Attempting to enable an extension that is already enabled will have no
     * effect
     *
     * @param extension Extension to enable
     */
    @Override
    public void enableExtension(@NotNull final Extension extension) {
        if (!extension.isEnabled()) {
            try {
                extension.getExtensionLoader().enableExtension(extension);
            } catch (Throwable ex) {
                extensionsImpl.getLogger().log(Level.SEVERE, "Error occurred (in the extension loader) while enabling " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    /**
     * Disables all the loaded extensions
     */
    @Override
    public void disableExtensions() {
        Extension[] extensions = getExtensions();
        for (int i = extensions.length - 1; i >= 0; i--) {
            disableExtension(extensions[i]);
        }
    }

    /**
     * Unloads all the extensions
     * <p>
     * Attempting to unload an extension that is already unloaded will have no
     * effect
     */
    @Override
    public void unloadExtensions() {
        Extension[] extensions = getExtensions();
        for (int i = extensions.length - 1; i >= 0; i--) {
            unloadExtension(extensions[i]);
        }
    }

    /**
     * Disables the specified extension
     * <p>
     * Attempting to disable an extension that is not enabled will have no effect
     *
     * @param extension Extension to disable
     */
    @Override
    public void disableExtension(@NotNull final Extension extension) {
        if (extension.isEnabled()) {
            try {
                extension.getExtensionLoader().unloadExtension(extension);
            } catch (Throwable ex) {
                extensionsImpl.getLogger().log(Level.SEVERE, "Error occurred (in the extension loader) while disabling " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                extensionsImpl.getScheduler().cancelTasks(extension);
            } catch (Throwable ex) {
                extensionsImpl.getLogger().log(Level.SEVERE, "Error occurred (in the extension loader) while cancelling tasks for " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    /**
     * Unloads the specified extension
     * <p>
     * Attempting to unload an extension that is already unloaded will have no
     * effect
     *
     * @param extension Extension to unload
     */
    @Override
    public void unloadExtension(@NotNull final Extension extension) {
        if (extension.isEnabled()) {
            try {
                extension.getExtensionLoader().unloadExtension(extension);
            } catch (Throwable ex) {
                extensionsImpl.getLogger().log(Level.SEVERE, "Error occurred (in the extension loader) while unloading " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                extensionsImpl.getScheduler().cancelTasks(extension);
            } catch (Throwable ex) {
                extensionsImpl.getLogger().log(Level.SEVERE, "Error occurred (in the extension loader) while cancelling tasks for " + extension.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    /**
     * Disables and removes all extensions
     */
    @Override
    public void clearExtensions() {
        synchronized (this) {
            disableExtensions();
            extensions.clear();
            lookupNames.clear();
            dependencyGraph = GraphBuilder.directed().build();
            fileAssociations.clear();
        }
    }

    public boolean isTransitiveDepend(@NotNull ExtensionDescriptionFile extension, @NotNull ExtensionDescriptionFile depend) {
        Preconditions.checkArgument(extension != null, "extension");
        Preconditions.checkArgument(depend != null, "depend");

        if (dependencyGraph.nodes().contains(extension.getName())) {
            Set<String> reachableNodes = Graphs.reachableNodes(dependencyGraph, extension.getName());
            if (reachableNodes.contains(depend.getName())) {
                return true;
            }
            for (String provided : depend.getProvides()) {
                if (reachableNodes.contains(provided)) {
                    return true;
                }
            }
        }
        return false;
    }
}
