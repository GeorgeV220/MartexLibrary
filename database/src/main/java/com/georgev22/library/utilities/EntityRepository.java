package com.georgev22.library.utilities;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.annotations.Column;
import com.georgev22.library.utilities.exceptions.NoSuchConstructorException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    CompletableFuture<V> save(V entity);

    /**
     * Loads an entity based on the specified entity ID.
     * Returns the loaded entity or null if the entity does not exist or if an error occurs.
     *
     * @param entityId The ID of the entity to be loaded.
     */
    CompletableFuture<V> load(@NotNull String entityId);

    /**
     * Retrieves the loaded entity with the specified ID.
     *
     * @param entityId The ID of the entity to be retrieved.
     * @return The loaded entity, or null if not found (implementation-dependent).
     */
    CompletableFuture<V> getEntity(@NotNull String entityId);

    /**
     * Checks if an entity with the specified ID exists.
     *
     * @param entityId The ID of the entity to check for existence.
     * @return True if the entity exists, false otherwise.
     */
    CompletableFuture<Boolean> exists(@NotNull String entityId, boolean checkDb, boolean forceLoad);

    /**
     * Deletes the entity with the specified ID.
     *
     * @param entityId The ID of the entity to be deleted.
     */
    CompletableFuture<Void> delete(@NotNull String entityId);

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

    default List<Method> getMethods(@NotNull Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredMethods()).filter(
                method -> method.getAnnotation(Column.class) != null
                        && method.getParameterCount() == 0
                        && method.getReturnType() != void.class
                        && !method.getName().startsWith("set")
                        && !method.getName().equalsIgnoreCase("_id")
        ).toList();
    }

    default List<Field> getFields(@NotNull Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields()).filter(
                field -> field.getAnnotation(Column.class) != null
                        && !field.getName().equalsIgnoreCase("_id")
        ).toList();
    }

    default ObjectMap<String, Object> getValuesMap(@NotNull V entity) {
        ObjectMap<String, Object> values = new HashObjectMap<>();

        for (Method method : getMethods(entity.getClass())) {
            Column columnAnnotation = method.getAnnotation(Column.class);

            if (columnAnnotation != null) {
                Object result;
                try {
                    result = method.invoke(entity);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    this.getLogger().log(Level.SEVERE, "[EntityRepository]:", e);
                    return null;
                }
                values.append(columnAnnotation.name(), result);
                entity.setValue(columnAnnotation.name(), result);
            }
        }

        for (Field field : getFields(entity.getClass())) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                Object result;
                try {
                    result = field.get(entity);
                } catch (IllegalAccessException e) {
                    this.getLogger().log(Level.SEVERE, "[EntityRepository]:", e);
                    return null;
                }
                values.append(columnAnnotation.name(), result);
                entity.setValue(columnAnnotation.name(), result);
            }
        }
        return values;
    }

    Logger getLogger();
}
