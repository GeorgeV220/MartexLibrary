package com.georgev22.library.utilities;

import com.georgev22.library.maps.ObservableObjectMap;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
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
