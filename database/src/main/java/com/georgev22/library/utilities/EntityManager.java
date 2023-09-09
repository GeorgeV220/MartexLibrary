package com.georgev22.library.utilities;

import com.georgev22.library.maps.ObjectMap;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The {@code EntityManager} interface provides methods for managing entities of type {@code T}.
 * <p>
 * It defines operations such as loading, saving, deleting, creating, and retrieving entities.
 * <p>
 * Implementations of this interface handle the persistence and retrieval of entities in a data store.
 * <p>
 * <p>
 * The interface requires the type parameter {@code T} to extend the {@code Entity} interface.
 * <p>
 * Entities are identified by {@link UUID} values.
 * <p>
 * <p>
 * This interface provides both synchronous and asynchronous methods for interacting with entities.
 * <p>
 * Asynchronous methods return {@link CompletableFuture} objects that can be used to handle the results
 * <p>
 * of the corresponding operations in an asynchronous manner.
 * <p>
 * <p>
 * The {@code EntityManager} interface also includes methods for saving and loading multiple entities at once,
 * <p>
 * as well as retrieving the map of currently loaded entities.
 *
 * @param <T> the type of entities managed by this {@code EntityManager}
 */
public interface EntityManager<T extends Entity> {

    /**
     * Loads the {@link Entity} with the specified ID
     *
     * @param entityId the {@link UUID} of the entity to be loaded
     * @return a {@link CompletableFuture} containing the loaded {@link Entity} object
     */
    CompletableFuture<T> load(UUID entityId);

    /**
     * Saves the specified {@link Entity}.
     *
     * @param entity the {@link Entity} to save
     * @return a {@link CompletableFuture} that completes when the {@link Entity} is saved
     */
    CompletableFuture<Void> save(T entity);

    /**
     * Deletes the specified entity.
     *
     * @param entity the {@link Entity} to delete
     * @return a {@link CompletableFuture} that completes when the {@link Entity} is deleted
     */
    CompletableFuture<Void> delete(T entity);

    /**
     * Creates a new {@link Entity} with the specified entity ID.
     *
     * @param entityId the {@link UUID} of the entity to create
     * @return a {@link CompletableFuture} that returns the newly created {@link Entity}
     */
    CompletableFuture<T> createEntity(UUID entityId);

    /**
     * Determines if a {@link Entity} with the specified entity ID exists.
     *
     * @param entityId the {@link UUID} of the entity to check
     * @return a {@link CompletableFuture} that returns true if a {@link Entity} with the specified ID exists, false otherwise
     */
    CompletableFuture<Boolean> exists(UUID entityId);

    /**
     * Retrieves the {@link Entity} with the given {@link UUID}.
     * <p>
     * If the entity is already loaded, it is returned immediately.
     * If not, it is loaded
     * asynchronously and returned in a {@link CompletableFuture}.
     *
     * @param entityId the {@link UUID} of the entity to retrieve
     * @return a {@link CompletableFuture} that will contain the {@link Entity} with the given id
     */
    CompletableFuture<T> getEntity(UUID entityId);

    /**
     * Saves all the loaded {@link Entity}s in the {@link #getLoadedEntities()} map.
     * For each {@link Entity} in the map,
     * this method calls the {@link #save(T)} method to persist the {@link Entity}.
     */
    void saveAll();

    /**
     * Loads all the entities by retrieving their IDs and invoking the {@link #load(UUID)} method.
     */
    void loadAll();

    /**
     * Retrieves the current map of loaded entities.
     *
     * @return the map of loaded entities with UUID as the key and EntityImpl object as the value
     */
    ObjectMap<UUID, T> getLoadedEntities();

}
