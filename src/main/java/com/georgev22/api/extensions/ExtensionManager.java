package com.georgev22.api.extensions;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

public class ExtensionManager {

    private static final List<Extension> extensionList = Lists.newArrayList();
    private static final Logger logger = LogManager.getLogger("ExtensionManager");

    static {
        Configurator.setRootLevel(Level.ALL);
    }

    public static void load(Extension extension) {
        if (extensionList.contains(extension)) {
            logger.error("Extension " + extension.getName() + " is already loaded.");
            return;
        }
        extensionList.add(extension);
        extension.onLoad();
        logger.info("Extension " + extension.getName() + " successfully loaded.");
    }

    public static void enable(Extension extension) {
        if (!extensionList.contains(extension)) {
            logger.error("Extension " + extension.getName() + " is not loaded.");
            return;
        }
        extension.onEnable();
        logger.info("Extension " + extension.getName() + " successfully enabled.");
    }

    public static void disable(Extension extension) {
        if (!extensionList.contains(extension)) {
            logger.error("Extension " + extension.getName() + " is not loaded.");
            return;
        }
        extension.onDisable();
        logger.error("Extension " + extension.getName() + " successfully disabled.");
    }

    public static void unload(Extension extension) {
        if (!extensionList.contains(extension)) {
            logger.error("Extension " + extension.getName() + " is not loaded.");
            return;
        }
        extension.onDisable();
        logger.error("Extension " + extension.getName() + " successfully unloaded.");
    }

    @Contract(pure = true)
    public static @NotNull @UnmodifiableView List<Extension> getExtensions() {
        return Collections.unmodifiableList(extensionList);
    }
}
