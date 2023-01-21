package com.georgev22.library.extensions;

import com.georgev22.library.extensions.scheduler.Scheduler;
import org.jetbrains.annotations.NotNull;

public class Extensions {

    private static ExtensionsImpl extensionsImpl;

    /**
     * Static class cannot be initialized.
     */
    private Extensions() {
    }

    /**
     * Gets the current {@link ExtensionsImpl} singleton
     *
     * @return ExtensionsImpl instance being ran
     */
    @NotNull
    public static ExtensionsImpl getExtensionsImpl() {
        return extensionsImpl;
    }

    /**
     * Attempts to set the {@link ExtensionsImpl} singleton.
     * <p>
     * This cannot be done if the ExtensionsImpl is already set.
     *
     * @param extensionsImpl ExtensionsImpl instance
     */
    public static void setExtensionsImpl(@NotNull ExtensionsImpl extensionsImpl) {
        if (Extensions.extensionsImpl != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton ExtensionsImpl");
        }

        Extensions.extensionsImpl = extensionsImpl;
        extensionsImpl.getLogger().info("This extensionsImpl is running " + getName() + " version " + getVersion());
    }

    /**
     * Gets the extension manager for interfacing with extensions.
     *
     * @return an extension manager for this ExtensionsImpl instance
     */
    @NotNull
    public static ExtensionManager getExtensionManager() {
        return extensionsImpl.getExtensionManager();
    }

    /**
     * Gets the name of this extensionsImpl implementation.
     *
     * @return name of this extensionsImpl implementation
     */
    @NotNull
    public static String getName() {
        return extensionsImpl.getName();
    }

    /**
     * Gets the version string of this extensionsImpl implementation.
     *
     * @return version of this extensionsImpl implementation
     */
    @NotNull
    public static String getVersion() {
        return extensionsImpl.getVersion();
    }

    /**
     * Gets the scheduler for managing scheduled events.
     *
     * @return a scheduling service
     */
    @NotNull
    public static Scheduler getScheduler() {
        return extensionsImpl.getScheduler();
    }

}
