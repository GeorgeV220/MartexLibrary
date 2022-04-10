package com.georgev22.api.yaml;

import com.georgev22.api.utilities.Utils;

import java.util.Map;

/**
 * This is a {@link Configuration} implementation that does not save or load
 * from any source, and stores all values in memory only.
 * This is useful for temporary Configurations for providing defaults.
 */
public class MemoryConfiguration extends MemorySection implements Configuration {
    protected Configuration defaults;
    protected MemoryConfigurationOptions options;

    /**
     * Creates an empty {@link MemoryConfiguration} with no default values.
     */
    public MemoryConfiguration() {
    }

    /**
     * Creates an empty {@link MemoryConfiguration} using the specified {@link
     * Configuration} as a source for all default values.
     *
     * @param defaults Default value provider
     * @throws IllegalArgumentException Thrown if defaults is null
     */
    public MemoryConfiguration(Configuration defaults) {
        this.defaults = defaults;
    }

    @Override
    public void addDefault(String path, Object value) {
        Utils.Assertions.notNull("Path may not be null", path);

        if (defaults == null) {
            defaults = new MemoryConfiguration();
        }

        defaults.set(path, value);
    }

    public void addDefaults(Map<String, Object> defaults) {
        Utils.Assertions.notNull("Defaults may not be null", defaults);

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            addDefault(entry.getKey(), entry.getValue());
        }
    }

    public void addDefaults(Configuration defaults) {
        Utils.Assertions.notNull("Defaults may not be null", defaults);

        addDefaults(defaults.getValues(true));
    }

    public void setDefaults(Configuration defaults) {
        Utils.Assertions.notNull("Defaults may not be null", defaults);

        this.defaults = defaults;
    }

    public Configuration getDefaults() {
        return defaults;
    }

    @Override
    public ConfigurationSection getParent() {
        return null;
    }

    public MemoryConfigurationOptions options() {
        if (options == null) {
            options = new MemoryConfigurationOptions(this);
        }

        return options;
    }
}
