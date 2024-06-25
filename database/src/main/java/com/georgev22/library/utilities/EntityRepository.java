package com.georgev22.library.utilities;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.utilities.annotations.Column;
import com.georgev22.library.utilities.exceptions.NoSuchConstructorException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.math.BigInteger;
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
     * Returns a CompletableFuture containing the saved entity, or completing with null if an error occurs.
     *
     * @param entity The entity to be saved.
     * @return a CompletableFuture containing the saved entity, or completing with null if an error occurs
     */
    CompletableFuture<V> save(V entity);

    /**
     * Loads an entity based on the specified entity ID.
     * Returns a CompletableFuture containing the loaded entity, or completing with null if the entity does not exist or if an error occurs.
     *
     * @param entityId The ID of the entity to be loaded.
     * @return a CompletableFuture containing the loaded entity, or completing with null if the entity does not exist or if an error occurs
     */
    CompletableFuture<V> load(@NotNull String entityId);

    /**
     * Retrieves the loaded entity with the specified ID.
     * Returns a CompletableFuture containing the loaded entity, or completing with null if not found (implementation-dependent).
     *
     * @param entityId The ID of the entity to be retrieved.
     * @return a CompletableFuture containing the loaded entity, or completing with null if not found
     */
    CompletableFuture<V> getEntity(@NotNull String entityId);

    /**
     * Checks if an entity with the specified ID exists.
     * Returns a CompletableFuture containing true if the entity exists, false otherwise.
     *
     * @param entityId  The ID of the entity to check for existence.
     * @param checkDb   Whether to check the database for the entity's existence.
     * @param forceLoad Whether to force loading the entity from the database.
     * @return a CompletableFuture containing true if the entity exists, false otherwise
     */
    CompletableFuture<Boolean> exists(@NotNull String entityId, boolean checkDb, boolean forceLoad);

    /**
     * Deletes the entity with the specified ID.
     * Returns a CompletableFuture that completes when the deletion is done.
     *
     * @param entityId The ID of the entity to be deleted.
     * @return a CompletableFuture that completes when the deletion is done
     */
    CompletableFuture<Void> delete(@NotNull String entityId);

    /**
     * Loads all entities from the database.
     * Returns a CompletableFuture containing the number of loaded entities.
     *
     * @return a CompletableFuture containing the number of loaded entities
     */
    CompletableFuture<BigInteger> loadAll();

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
    default void checkForConstructorWithSingleString(@NotNull Class<V> entityClass) throws NoSuchConstructorException {
        Constructor<?>[] constructors = entityClass.getConstructors();

        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();
            if (parameters.length == 1 && parameters[0].getType().equals(String.class)) {
                return;
            }
        }

        throw new NoSuchConstructorException("No constructor with a single vararg String parameter found in class " + entityClass.getSimpleName());
    }

    /**
     * Retrieves a list of methods in the specified class that are annotated with {@link Column},
     * have no parameters, return a non-void type, do not start with "set", and are not named "_id".
     *
     * @param entityClass The class to retrieve the methods from.
     * @return a list of methods that match the criteria
     */
    default List<Method> getMethods(@NotNull Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredMethods())
                .filter(
                        method -> method.getAnnotation(Column.class) != null
                                && method.getParameterCount() == 0
                                && method.getReturnType() != void.class
                                && !method.getName().startsWith("set")
                                && !method.getName().equalsIgnoreCase("_id")
                ).filter(method -> !Modifier.isStatic(method.getModifiers()))
                .peek(method -> {
                    if (!Modifier.isPublic(method.getModifiers())) {
                        method.setAccessible(true);
                    }
                })
                .toList();
    }

    /**
     * Retrieves a list of fields in the specified class that are annotated with {@link Column}
     * and are not named "_id".
     *
     * @param entityClass The class to retrieve the fields from.
     * @return a list of fields that match the criteria
     */
    default List<Field> getFields(@NotNull Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(
                        field -> field.getAnnotation(Column.class) != null
                                && !field.getName().equalsIgnoreCase("_id")
                ).filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> {
                    if (!Modifier.isPublic(field.getModifiers())) {
                        field.setAccessible(true);
                    }
                }).toList();
    }

    /**
     * Creates an ObjectMap containing the values of the specified entity's annotated fields and methods.
     *
     * @param entity The entity to extract values from.
     * @return an ObjectMap containing the values of the entity's annotated fields and methods
     */
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

    /**
     * Gets the logger associated with this repository.
     *
     * @return The logger.
     */
    Logger getLogger();

    /**
     * Returns an observable map of all loaded entities.
     *
     * @return an ObservableObjectMap containing all loaded entities
     */
    ObservableObjectMap<String, V> getLoadedEntities();
}
