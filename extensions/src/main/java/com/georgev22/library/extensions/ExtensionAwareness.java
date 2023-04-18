package com.georgev22.library.extensions;

import java.util.Set;

/**
 * Represents a concept that an extension is aware of.
 * <p>
 * The internal representation may be singleton, or be a parameterized
 * instance, but must be immutable.
 */
public interface ExtensionAwareness {
    /**
     * Each entry here represents a particular extension's awareness. These can
     * be checked by using {@link ExtensionDescriptionFile#getAwareness()}.{@link
     * Set#contains(Object) contains(flag)}.
     */
    enum Flags implements ExtensionAwareness {
        /**
         * This specifies that all (text) resources stored in an extension's jar
         * use UTF-8 encoding.
         *
         * @deprecated all extensions are now assumed to be UTF-8 aware.
         */
        @Deprecated
        UTF8,
    }
}
