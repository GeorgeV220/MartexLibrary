package com.georgev22.library.utilities;

import com.georgev22.library.database.DatabaseWrapper;
import com.georgev22.library.database.DatabaseWrapper.DatabaseObject;
import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObjectMap.Pair;
import com.georgev22.library.maps.ObservableObjectMap;
import com.mongodb.annotations.Beta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link EntityManager} class is responsible for managing {@link Entity} objects in a persistence storage.
 * It supports multiple storage types including MySQL, SQLite, PostgreSQL, MongoDB, and FILE.
 * The class provides methods for checking if a {@link Entity} exists,
 * loading a {@link Entity}, and creating a {@link Entity}.
 *
 * @author <a href="https://github.com/GeorgeV220">GeorgeV220</a>
 */
public class EntityManager<T extends EntityManager.Entity> {
    private final File entitiesDirectory;
    private final DatabaseWrapper database;
    private final String collection;
    private final Class<? extends Entity> entityClazz;
    private final ObservableObjectMap<UUID, T> loadedEntities = new ObservableObjectMap<>();

    /**
     * Constructor for the EntityManager class
     *
     * @param obj            the object to be used for storage (DatabaseWrapper or File)
     * @param collectionName the name of the collection to be used for MONGODB and SQL, null for other types
     */
    public EntityManager(Object obj, @Nullable String collectionName, Class<? extends Entity> clazz) {
        this.collection = collectionName;
        if (obj instanceof File folder) {
            this.entitiesDirectory = folder;
            this.database = null;
            if (!this.entitiesDirectory.exists()) {
                this.entitiesDirectory.mkdirs();
            }
        } else if (obj instanceof DatabaseWrapper databaseWrapper) {
            this.entitiesDirectory = null;
            this.database = databaseWrapper;
        } else {
            this.entitiesDirectory = null;
            this.database = null;
        }
        this.entityClazz = clazz;
    }

    /**
     * Loads the {@link Entity} with the specified ID
     *
     * @param entityId the {@link UUID} of the entity to be loaded
     * @return a {@link CompletableFuture} containing the loaded {@link Entity} object
     */
    public CompletableFuture<T> load(UUID entityId) {
        return exists(entityId)
                .thenCompose(exists -> {
                    if (exists) {
                        return CompletableFuture.supplyAsync(() -> {
                            if (entitiesDirectory != null) {
                                File file = new File(entitiesDirectory, entityId + ".entity");
                                try {
                                    T entity = (T) Utils.deserializeObject(file.getAbsolutePath());
                                    loadedEntities.append(entityId, entity);
                                    return entity;
                                } catch (IOException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (database != null) {
                                Pair<String, List<DatabaseObject>> retrievedData = database.retrieveData(collection, Pair.create("entity_id", entityId.toString()));
                                T entity;
                                try {
                                    entity = (T) entityClazz.getDeclaredConstructor(UUID.class).newInstance(entityId);
                                } catch (InstantiationException | IllegalAccessException |
                                         InvocationTargetException | NoSuchMethodException e) {
                                    throw new RuntimeException(e);
                                }
                                retrievedData.value().forEach(databaseObject -> {
                                    ObjectMap<String, Object> databaseObjectData = databaseObject.data();
                                    databaseObjectData.forEach(entity::addCustomData);
                                });
                                return entity;
                            } else {
                                try {
                                    return (T) entityClazz.getDeclaredConstructor(UUID.class).newInstance(entityId);
                                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                         NoSuchMethodException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    } else {
                        return createEntity(entityId);
                    }
                });
    }

    /**
     * Saves the specified {@link Entity}.
     *
     * @param entity the {@link Entity} to save
     * @return a {@link CompletableFuture} that completes when the {@link Entity} is saved
     */
    public CompletableFuture<Void> save(Entity entity) {
        return CompletableFuture.runAsync(() -> {
            if (entitiesDirectory != null) {
                File file = new File(entitiesDirectory, entity.getId() + ".entity");
                try {
                    Utils.serializeObject(entity, file.getAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (database != null) {
                exists(entity.getId()).thenAccept(result -> {
                    ObjectMap<String, Object> entityData = new HashObjectMap<>(entity.customData);
                    if (result) {
                        database.updateData(collection, Pair.create("entity_id", entity.getId().toString()), Pair.create("$set", entityData.removeEntry("entity_id")), null);
                    } else {
                        database.addData(collection, Pair.create(entity.entityId.toString(), entityData));
                    }
                });
            }
        });
    }

    /**
     * Creates a new {@link Entity} with the specified entity ID.
     *
     * @param entityId the {@link UUID} of the entity to create
     * @return a {@link CompletableFuture} that returns the newly created {@link Entity}
     */
    public CompletableFuture<T> createEntity(UUID entityId) {
        T entity;
        try {
            entity = (T) entityClazz.getDeclaredConstructor(UUID.class).newInstance(entityId);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(loadedEntities.append(entityId, entity).get(entityId));
    }

    /**
     * Determines if a {@link Entity} with the specified entity ID exists.
     *
     * @param entityId the {@link UUID} of the entity to check
     * @return a {@link CompletableFuture} that returns true if a {@link Entity} with the specified ID exists, false otherwise
     */
    public CompletableFuture<Boolean> exists(UUID entityId) {
        return CompletableFuture.supplyAsync(() -> {
            if (entitiesDirectory != null) {
                return new File(entitiesDirectory, entityId + ".entity").exists();
            } else if (database != null) {
                return database.exists(collection, Pair.create("entity_id", entityId), null);
            } else {
                return false;
            }
        });
    }

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
    public CompletableFuture<T> getEntity(UUID entityId) {
        if (loadedEntities.containsKey(entityId)) {
            return CompletableFuture.completedFuture(loadedEntities.get(entityId));
        }

        return load(entityId);
    }

    /**
     * Saves all the loaded {@link Entity}s in the {@link #loadedEntities} map.
     * For each {@link Entity} in the map,
     * this method calls the {@link #save(Entity)} method to persist the {@link Entity}.
     */
    public void saveAll() {
        loadedEntities.forEach((uuid, entity) -> save(entity));
    }

    @Beta
    public void loadAll() {
        List<UUID> entityIDs = new ArrayList<>();
        if (entitiesDirectory != null) {
            File[] files = this.entitiesDirectory.listFiles((dir, name) -> name.endsWith(".entity"));
            if (files != null) {
                Arrays.stream(files).forEach(file -> entityIDs.add(UUID.fromString(file.getName().replace(".entity", ""))));
            }
        } else if (database != null) {
            Pair<String, List<DatabaseObject>> data = database.retrieveData(collection, Pair.create("entity_id", null));
            data.value().forEach(databaseObject -> {
                entityIDs.add(UUID.fromString(String.valueOf(databaseObject.data().get("entity_id"))));
            });
        }
        entityIDs.forEach(this::load);
    }

    /**
     * Retrieves the current map of loaded entities.
     *
     * @return the map of loaded entities with UUID as the key and Entity object as the value
     */
    public ObservableObjectMap<UUID, T> getLoadedEntities() {
        return loadedEntities;
    }

    /**
     * A class representing an entity in the system.
     */
    public static class Entity implements Serializable {
        private final UUID entityId;
        private ObjectMap<String, Object> customData;

        /**
         * Constructs a new entity with a random UUID.
         */
        public Entity() {
            this(UUID.randomUUID());
        }

        /**
         * Constructs a new entity with the specified UUID and name.
         *
         * @param entityId the UUID of the entity
         */
        public Entity(UUID entityId) {
            this.entityId = entityId;
            this.customData = new ConcurrentObjectMap<>();
        }

        /**
         * Returns the entityId of this `Entity` object.
         *
         * @return the entityId of this `Entity` object.
         */
        public UUID getId() {
            return entityId;
        }

        /**
         * Adds a key-value pair to the custom data map.
         *
         * @param key   the key of the data
         * @param value the value of the data
         */
        public Entity addCustomData(String key, Object value) {
            customData.append(key, value);
            return this;
        }

        /**
         * Adds a key-value pair to the custom data map if the key does not already exist.
         *
         * @param key   the key of the data
         * @param value the value of the data
         */
        public Entity addCustomDataIfNotExists(String key, Object value) {
            return !customData.containsKey(key) ? addCustomData(key, value) : this;
        }

        /**
         * Returns the value of the custom data for the specified key.
         *
         * @param key the key of the data
         * @return the value of the custom data for the specified key
         */
        public <T> T getCustomData(String key) {
            return (T) customData.get(key);
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "entityId=" + entityId +
                    ", customData=" + customData +
                    '}';
        }
    }
}
