package com.georgev22.api.extensions;

import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Set;

/**
 * Represents a concept that a plugin is aware of.
 * <p>
 * The internal representation may be singleton, or be a parameterized
 * instance, but must be immutable.
 */
public interface ExtensionAwareness {
    /**
     * Each entry here represents a particular plugin's awareness. These can
     * be checked by using {@link PluginDescriptionFile#getAwareness()}.{@link
     * Set#contains(Object) contains(flag)}.
     */
    enum Flags implements ExtensionAwareness {
        /**
         * This specifies that all (text) resources stored in a plugin's jar
         * use UTF-8 encoding.
         *
         * @deprecated all plugins are now assumed to be UTF-8 aware.
         */
        @Deprecated
        UTF8,
        ;
    }
}
