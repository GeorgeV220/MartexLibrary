package com.georgev22.library.utilities;

import com.georgev22.library.utilities.annotations.Column;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The {@code Entity} abstract class represents an entity in a database context.
 * Entities are expected to have an ID and support setting values for specific keys.
 */
public abstract class Entity {

    private final String _id;

    public Entity(String _id) {
        this._id = _id;
    }

    /**
     * Returns the ID of the entity.
     *
     * @return the ID of the entity
     */
    @Column(name = "_id", type = "VARCHAR(32)", unique = true)
    public String _id() {
        return this._id;
    }

    /**
     * Sets the value for a specified key in the entity.
     * The default implementation attempts to find and invoke the corresponding setter method based on the key.
     * The "_id" key is excluded from this process.
     *
     * <p>If a setter method is not found for the given key, the method attempts
     * to directly set the field value using reflection. If both attempts fail, a {@link RuntimeException} is thrown,
     * wrapping any encountered {@link IllegalAccessException} or {@link NoSuchFieldException}.
     *
     * @param key   the key for which the value should be set
     * @param value the value to set
     * @throws RuntimeException if setting the value encounters an error, including {@link IllegalAccessException}
     *                          or {@link NoSuchFieldException}
     */
    public void setValue(@NotNull String key, Object value) {
        if (key.equals("_id")) {
            return;
        }

        try {
            Method method = this.getClass().getDeclaredMethod("set" + key.substring(0, 1).toUpperCase() + key.substring(1), value.getClass());
            method.invoke(this, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            try {
                Field field = this.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(this, value);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException("No setter found for key '" + key + "'", ex);
            }
        }
    }
}
