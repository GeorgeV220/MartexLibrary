package com.georgev22.api.extensions;

import com.georgev22.api.exceptions.InvalidDescriptionException;
import com.georgev22.api.exceptions.InvalidExtensionException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface ExtensionManager {

    /**
     * Registers the specified extension loader
     *
     * @param loader Class name of the ExtensionLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a
     *                                  valid ExtensionLoader
     */
    void registerInterface(@NotNull Class<? extends ExtensionLoader> loader) throws IllegalArgumentException;

    /**
     * Checks if the given extension is loaded and returns it when applicable
     * <p>
     * Please note that the name of the extension is case-sensitive
     *
     * @param name Name of the extension to check
     * @return Extension if it exists, otherwise null
     */
    @Nullable Extension getExtension(@NotNull String name);

    /**
     * Gets a list of all currently loaded extensions
     *
     * @return Array of Extensions
     */
    @NotNull Extension[] getExtensions();

    /**
     * Checks if the given extension is enabled or not
     * <p>
     * Please note that the name of the extension is case-sensitive.
     *
     * @param name Name of the extension to check
     * @return true if the extension is enabled, otherwise false
     */
    boolean isExtensionEnabled(@NotNull String name);

    /**
     * Checks if the given extension is enabled or not
     *
     * @param extension Extension to check
     * @return true if the extension is enabled, otherwise false
     */
    @Contract("null -> false")
    boolean isExtensionEnabled(@Nullable Extension extension);

    /**
     * Loads the extension in the specified file
     * <p>
     * File must be valid according to the current enabled Extension interfaces
     *
     * @param file File containing the extension to load
     * @return The Extension loaded, or null if it was invalid
     * @throws InvalidExtensionException   Thrown when the specified file is not a
     *                                     valid extension
     * @throws InvalidDescriptionException Thrown when the specified file
     *                                     contains an invalid description
     */
    @Nullable Extension loadExtension(@NotNull File file) throws InvalidExtensionException, InvalidDescriptionException;

    /**
     * Loads the extensions contained within the specified directory
     *
     * @param directory Directory to check for extensions
     * @return A list of all extensions loaded
     */
    @NotNull Extension[] loadExtensions(@NotNull File directory);

    /**
     * Disables all the loaded extensions
     */
    void disableExtensions();

    /**
     * Unloads the specified extension
     * <p>
     * Attempting to unload an extension that is already unloaded will have no
     * effect
     *
     * @param extension Extension to unload
     */
    void unloadExtension(@NotNull Extension extension);

    /**
     * Disables and removes all extensions
     */
    void clearExtensions();

    /**
     * Enables the specified extension
     * <p>
     * Attempting to enable an extension that is already enabled will have no
     * effect
     *
     * @param extension Extension to enable
     */
    void enableExtension(@NotNull Extension extension);

    /**
     * Unloads all the extensions
     * <p>
     * Attempting to unload an extension that is already unloaded will have no
     * effect
     */
    void unloadExtensions();

    /**
     * Disables the specified extension
     * <p>
     * Attempting to disable an extension that is not enabled will have no effect
     *
     * @param extension Extension to disable
     */
    void disableExtension(@NotNull Extension extension);

}
