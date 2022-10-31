package com.georgev22.api.extensions;

import com.georgev22.api.extensions.scheduler.Scheduler;

import java.util.logging.Logger;

public class ExtensionManager {

    private final JavaExtensionLoader extensionLoader;

    private final static Scheduler scheduler = new Scheduler();
    
    public ExtensionManager(Logger logger) {
        extensionLoader = new JavaExtensionLoader(logger);
    }

    public JavaExtensionLoader getExtensionLoader() {
        return extensionLoader;
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }
}
