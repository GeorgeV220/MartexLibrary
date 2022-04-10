package com.georgev22.api.extensions;

import com.georgev22.api.extensions.exceptions.InvalidDescriptionException;
import com.georgev22.api.extensions.exceptions.InvalidExtensionException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Represents a extension loader, which handles direct access to specific types
 * of extensions
 */
public interface ExtensionLoader {

    /**
     * Loads the extension contained in the specified file
     *
     * @param file File to attempt to load
     * @return Extension that was contained in the specified file, or null if
     * unsuccessful
     */
    @NotNull Extension loadExtension(@NotNull File file) throws InvalidExtensionException;

    /**
     * Loads a ExtensionDescriptionFile from the specified file
     *
     * @param file File to attempt to load from
     * @return A new ExtensionDescriptionFile loaded from the extension.yml in the
     * specified file
     */
    @NotNull ExtensionDescriptionFile getExtensionDescription(@NotNull File file) throws InvalidDescriptionException;

    /**
     * Returns a list of all filename filters expected by this ExtensionLoader
     *
     * @return The filters
     */
    @NotNull Pattern[] getExtensionFileFilters();

    /**
     * Enables the specified extension
     * <p>
     * Attempting to enable a extension that is already enabled will have no
     * effect
     *
     * @param extension Extension to enable
     */
    void enableExtension(@NotNull Extension extension);

    /**
     * Disables the specified extension
     * <p>
     * Attempting to disable a extension that is not enabled will have no effect
     *
     * @param extension Extension to disable
     */
    void disableExtension(@NotNull Extension extension);
}