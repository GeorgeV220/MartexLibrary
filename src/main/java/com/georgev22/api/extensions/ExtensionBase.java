package com.georgev22.api.extensions;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a base {@link Extension}
 * <p>
 * Extend this class if your plugin is not a {@link
 * com.georgev22.api.extensions.java.JavaExtension}
 */
public abstract class ExtensionBase implements Extension {

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Extension)) {
            return false;
        }
        return getName().equals(((Extension) obj).getName());
    }

    @Override
    @NotNull
    public final String getName() {
        return getDescription().getName();
    }
}
