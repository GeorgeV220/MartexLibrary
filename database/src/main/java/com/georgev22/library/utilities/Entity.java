package com.georgev22.library.utilities;

import com.georgev22.library.maps.ConcurrentObjectMap;

import java.io.Serializable;
import java.util.UUID;

/**
 * The {@code Entity} interface represents an entity with a unique identifier.
 * It provides methods for managing custom data associated with the entity.
 * Custom data can be added, retrieved, and accessed using key-value pairs.
 */
public interface Entity extends Serializable {

    /**
     * Returns the unique identifier of the entity.
     *
     * @return the {@link UUID} representing the entity's ID
     */
    UUID getId();

    /**
     * Adds custom data to the entity with the specified key and value.
     *
     * @param key   the key of the custom data
     * @param value the value of the custom data
     * @return the updated entity with the added custom data
     */
    default Entity addCustomData(String key, Object value) {
        this.getCustomData().append(key, value);
        return this;
    }

    /**
     * Adds custom data to the entity with the specified key and value if the key does not already exist.
     *
     * @param key   the key of the custom data
     * @param value the value of the custom data
     * @return the updated entity with the added custom data (if the key did not already exist)
     */
    default Entity addCustomDataIfNotExists(String key, Object value) {
        this.getCustomData().appendIfTrue(key, value, this.getCustomData().containsKey(key));
        return this;
    }

    /**
     * Retrieves the value of the custom data associated with the specified key.
     *
     * @param key the key of the custom data
     * @param <T> the type of the value to retrieve
     * @return the value associated with the specified key, or {@code null} if the key does not exist
     */
    default <T> T getCustomData(String key) {
        return (T) getCustomData().get(key);
    }

    /**
     * Retrieves the map of custom data associated with the entity.
     *
     * @return the {@link ConcurrentObjectMap} containing the custom data of the entity
     */
    ConcurrentObjectMap<String, Object> getCustomData();

}
