package com.georgev22.library.utilities;

import com.georgev22.library.utilities.exceptions.NoSuchConstructorException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

/**
 * Interface for a repository managing entities.
 *
 * @param <V> The type of the entity.
 */
public interface EntityRepository<V extends Entity> {

    /**
     * Saves the given entity.
     * Returns the saved entity or null if an error occurs.
     *
     * @param entity The entity to be saved.
     */
    V save(V entity);

    /**
     * Loads an entity based on the specified entity ID.
     * Returns the loaded entity or null if the entity does not exist or if an error occurs.
     *
     * @param entityId The ID of the entity to be loaded.
     */
    @Nullable V load(String entityId);

    /**
     * Retrieves the loaded entity with the specified ID.
     *
     * @param entityId The ID of the entity to be retrieved.
     * @return The loaded entity, or null if not found (implementation-dependent).
     */
    @Nullable V getEntity(String entityId);

    /**
     * Checks if an entity with the specified ID exists.
     *
     * @param entityId The ID of the entity to check for existence.
     * @return True if the entity exists, false otherwise.
     */
    boolean exists(String entityId, boolean checkDb, boolean forceLoad);

    /**
     * Deletes the entity with the specified ID.
     *
     * @param entityId The ID of the entity to be deleted.
     */
    void delete(String entityId);

    /**
     * Loads all entities from the database.
     */
    void loadAll();

    /**
     * Saves all loaded entities to the database.
     */
    void saveAll();

    /**
     * Checks for a constructor with a single vararg String parameter in the specified class.
     *
     * @param entityClass The class to check for the constructor.
     * @throws NoSuchConstructorException If no suitable constructor is found.
     */
    default void checkForConstructorWithSingleVarargString(@NotNull Class<V> entityClass) throws NoSuchConstructorException {
        Constructor<?>[] constructors = entityClass.getConstructors();

        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();
            if (parameters.length == 1 && parameters[0].getType().equals(String[].class) && parameters[0].isVarArgs()) {
                return;
            }
        }

        throw new NoSuchConstructorException("No constructor with a single vararg String parameter found in class " + entityClass.getSimpleName());
    }
}
