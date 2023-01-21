package com.georgev22.library.extensions;


import com.georgev22.library.extensions.scheduler.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public interface ExtensionsImpl {

    /**
     * Returns the primary logger associated with this server instance.
     *
     * @return Logger associated with this server
     */
    @NotNull Logger getLogger();

    /**
     * Gets the extension manager for interfacing with extensions.
     *
     * @return an extension manager for this ExtensionsImpl instance
     */
    @NotNull ExtensionManager getExtensionManager();

    /**
     * Gets the scheduler for managing scheduled events.
     *
     * @return a scheduling service for this server
     */
    @NotNull Scheduler getScheduler();

    /**
     * Gets the name of this server implementation.
     *
     * @return name of this server implementation
     */
    @NotNull String getName();

    /**
     * Gets the version string of this server implementation.
     *
     * @return version of this server implementation
     */
    @NotNull String getVersion();
}
