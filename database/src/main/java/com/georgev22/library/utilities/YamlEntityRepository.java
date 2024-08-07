package com.georgev22.library.utilities;

import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.yaml.file.YamlConfiguration;
import com.georgev22.library.yaml.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A repository manager for entities using YAML files for storage.
 *
 * @param <V> The type of the entity.
 */
public class YamlEntityRepository<V extends Entity> implements EntityRepository<V> {

    private final ObservableObjectMap<String, V> loadedEntities = new ObservableObjectMap<>();
    private final File dataFolder;
    private final Logger logger;
    private final Class<V> entityClass;

    /**
     * Constructs a YAML entity repository.
     *
     * @param dataFolder  The folder where YAML files will be stored.
     * @param logger      The logger for handling log messages.
     * @param entityClass The class type of the entity managed by this repository.
     */
    public YamlEntityRepository(File dataFolder, Logger logger, Class<V> entityClass) {
        if (!ConfigurationSerializable.class.isAssignableFrom(entityClass)) {
            throw new RuntimeException("Entity is not a ConfigurationSerializable");
        }
        this.dataFolder = dataFolder;
        this.logger = logger;
        this.entityClass = entityClass;
        if (!this.dataFolder.exists()) {
            if (this.dataFolder.mkdirs()) {
                this.logger.log(Level.INFO, "[EntityRepository]: Created data folder: " + this.dataFolder.getAbsolutePath());
            }
        }
    }

    /**
     * Saves the entity to a YAML file.
     *
     * @param entity The entity to be saved.
     * @return The saved entity, or null if an error occurred.
     */
    @Override
    public CompletableFuture<V> save(@NotNull V entity) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(dataFolder, entity._id() + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("entity", entity);

            try {
                config.save(file);
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
                return null;
            }

            return entity;
        });
    }

    /**
     * Loads the entity from a YAML file.
     *
     * @param entityId The ID of the entity to be loaded.
     * @return The loaded entity, or null if the entity does not exist or an error occurred.
     */
    @Override
    public CompletableFuture<V> load(@NotNull String entityId) {
        if (loadedEntities.containsKey(entityId)) {
            return CompletableFuture.completedFuture(loadedEntities.get(entityId));
        }
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(dataFolder, entityId + ".yml");
            if (!file.exists()) {
                return null;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            V entity = config.getObject("entity", this.entityClass);
            if (entity == null) {
                this.logger.log(Level.SEVERE, "[EntityRepository] Error loading entity from file: " + file.getPath());
                return null;
            }
            this.loadedEntities.put(entityId, entity);
            return entity;
        });
    }

    /**
     * Retrieves the loaded entity with the specified entity ID.
     *
     * @param entityId The ID of the entity to be retrieved.
     * @return The loaded entity, or null if not found.
     */
    @Override
    public CompletableFuture<V> getEntity(@NotNull String entityId) {
        if (loadedEntities.containsKey(entityId)) {
            return CompletableFuture.completedFuture(loadedEntities.get(entityId));
        }
        return this.load(entityId);
    }

    /**
     * Checks if an entity with the specified ID is already loaded.
     *
     * @param entityId  The ID of the entity to check for existence.
     * @param checkDb   Check in the folder for the entity.
     * @param forceLoad Force load the entity
     * @return True if the entity is loaded, false otherwise.
     */
    @Override
    public CompletableFuture<Boolean> exists(@NotNull String entityId, boolean checkDb, boolean forceLoad) {
        return CompletableFuture.supplyAsync(() -> {
            if (loadedEntities.containsKey(entityId)) {
                return true;
            }

            if (checkDb) {
                File file = new File(dataFolder, entityId + ".yml");
                return forceLoad ? this.load(entityId) != null : file.exists();
            }
            return false;
        });
    }

    /**
     * Deletes the entity with the specified ID.
     *
     * @param entityId The ID of the entity to be deleted.
     */
    @Override
    public CompletableFuture<Void> delete(@NotNull String entityId) {
        return CompletableFuture.runAsync(() -> {
            File file = new File(dataFolder, entityId + ".yml");
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            this.loadedEntities.remove(entityId);
        });
    }

    /**
     * Loads all entities from the data folder.
     */
    @Override
    public CompletableFuture<BigInteger> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files == null) {
                return CompletableFuture.completedFuture(BigInteger.ZERO);
            }

            AtomicReference<BigInteger> atomicCount = new AtomicReference<>(BigInteger.ZERO);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (File file : files) {
                String entityId = file.getName().replace(".yml", "");
                CompletableFuture<Void> future = load(entityId).thenAccept(v -> {
                    if (v != null) {
                        atomicCount.updateAndGet(current -> current.add(BigInteger.ONE));
                    }
                });
                futures.add(future);
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            return allOf.thenApply(v -> atomicCount.get());
        }).thenCompose(countFuture -> countFuture);
    }

    /**
     * Saves all entities to the data folder.
     */
    @Override
    public void saveAll() {
        for (V entity : this.loadedEntities.values()) {
            save(entity);
        }
    }

    /**
     * Gets the data folder where YAML files are stored.
     *
     * @return The data folder.
     */
    public File getDataFolder() {
        return dataFolder;
    }

    /**
     * Gets the logger associated with this repository.
     *
     * @return The logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns an observable map of all loaded entities.
     *
     * @return an ObservableObjectMap containing all loaded entities
     */
    @Override
    public ObservableObjectMap<String, V> getLoadedEntities() {
        return this.loadedEntities;
    }
}
