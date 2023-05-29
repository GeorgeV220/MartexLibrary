package com.georgev22.library.utilities;

import com.georgev22.library.database.Database;
import com.georgev22.library.database.mongo.MongoDB;
import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObservableObjectMap;
import com.mongodb.annotations.Beta;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
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
    private final Database database;
    private final MongoDB mongoDB;
    private final String collection;
    private final Type type;
    private final Class<? extends Entity> entityClazz;
    private final ObservableObjectMap<UUID, T> loadedEntities = new ObservableObjectMap<>();

    /**
     * Constructor for the EntityManager class
     *
     * @param type           the type of storage system to be used (FILE, SQL or MONGODB)
     * @param obj            the object to be used for storage (File for FILE, Connection for SQL and MongoDB for MONGODB)
     * @param collectionName the name of the collection to be used for MONGODB and SQL, null for other types
     */
    public EntityManager(@NotNull Type type, Object obj, @Nullable String collectionName, Class<? extends Entity> clazz) {
        this.type = type;
        this.collection = collectionName;
        switch (type) {
            case FILE -> {
                this.entitiesDirectory = (File) obj;
                this.database = null;
                this.mongoDB = null;
                if (!this.entitiesDirectory.exists()) {
                    this.entitiesDirectory.mkdirs();
                }
            }
            case SQL -> {
                this.entitiesDirectory = null;
                this.database = (Database) obj;
                this.mongoDB = null;
            }
            case MONGODB -> {
                this.entitiesDirectory = null;
                this.database = null;
                this.mongoDB = (MongoDB) obj;
            }
            default -> {
                this.entitiesDirectory = null;
                this.database = null;
                this.mongoDB = null;
            }
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
                            switch (type) {
                                case FILE -> {
                                    File file = new File(entitiesDirectory, entityId + ".entity");
                                    try {
                                        T entity = (T) Utils.deserializeObject(file.getAbsolutePath());
                                        loadedEntities.append(entityId, entity);
                                        return entity;
                                    } catch (IOException | ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case SQL -> {
                                    String query = "SELECT entity FROM " + collection + " WHERE entity_id = ?";
                                    try {
                                        T entity = (T) entityClazz.getDeclaredConstructor(UUID.class).newInstance(entityId);
                                        PreparedStatement statement = Objects.requireNonNull(database.getConnection()).prepareStatement(query);
                                        statement.setString(1, entityId.toString());
                                        ResultSet resultSet = statement.executeQuery();

                                        ResultSetMetaData metaData = resultSet.getMetaData();
                                        int columnCount = metaData.getColumnCount();

                                        for (int i = 1; i <= columnCount; i++) {
                                            String columnName = metaData.getColumnName(i);
                                            Object columnValue = Utils.deserializeObjectFromString(resultSet.getString(columnName));
                                            entity.addCustomData(columnName, columnValue);
                                        }
                                        loadedEntities.append(entityId, entity);
                                        return entity;
                                    } catch (InstantiationException | IllegalAccessException |
                                             InvocationTargetException | NoSuchMethodException | SQLException |
                                             IOException | ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case MONGODB -> {
                                    Document document = mongoDB.getCollection(collection).find(Filters.eq("entityId", entityId.toString())).first();
                                    if (document != null) {
                                        String serializedEntity = document.getString("entity");
                                        try {
                                            T entity = (T) Utils.deserializeObjectFromString(serializedEntity);
                                            loadedEntities.append(entityId, entity);
                                            return entity;
                                        } catch (IOException | ClassNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        throw new RuntimeException("No entity found with id: " + entityId);
                                    }
                                }
                                default -> {
                                    try {
                                        return (T) entityClazz.getDeclaredConstructor(UUID.class).newInstance(entityId);
                                    } catch (InstantiationException | IllegalAccessException |
                                             InvocationTargetException | NoSuchMethodException e) {
                                        throw new RuntimeException(e);
                                    }
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
            switch (type) {
                case FILE -> {
                    File file = new File(entitiesDirectory, entity.getId() + ".entity");
                    try {
                        Utils.serializeObject(entity, file.getAbsolutePath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case SQL -> exists(entity.getId()).thenAccept(result -> {
                    String query = result ? database.buildUpdateStatement(collection, entity.customData, "entity_id = ?") : database.buildInsertStatement(collection, entity.customData);
                    try {
                        PreparedStatement statement = Objects.requireNonNull(database.getConnection()).prepareStatement(query);
                        int i = 1;
                        for (Map.Entry<String, Object> entry : entity.customData.entrySet()) {
                            statement.setString(i, Utils.serializeObjectToString(entry.getValue()));
                            i++;
                        }
                        statement.setString(i, entity.getId().toString());
                        statement.executeUpdate();
                        statement.close();
                    } catch (SQLException | IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                case MONGODB -> {
                    MongoCollection<Document> mongoCollection = mongoDB.getCollection(collection);
                    try {
                        Document document = Document.parse(Utils.serializeObjectToString(entity));
                        mongoCollection.insertOne(document);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
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
        return save(entity)
                .thenApply(aVoid -> {
                    loadedEntities.append(entityId, entity);
                    return entity;
                });
    }

    /**
     * Determines if a {@link Entity} with the specified entity ID exists.
     *
     * @param entityId the {@link UUID} of the entity to check
     * @return a {@link CompletableFuture} that returns true if a {@link Entity} with the specified ID exists, false otherwise
     */
    public CompletableFuture<Boolean> exists(UUID entityId) {
        return CompletableFuture.supplyAsync(() -> {
            switch (type) {
                case FILE -> {
                    return new File(entitiesDirectory, entityId + ".entity").exists();
                }
                case SQL -> {
                    String query = "SELECT count(*) FROM " + collection + " WHERE entity_id = ?";
                    try {
                        PreparedStatement statement = Objects.requireNonNull(database.getConnection()).prepareStatement(query);
                        statement.setString(1, entityId.toString());
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            return resultSet.getInt(1) > 0;
                        } else {
                            throw new RuntimeException("No entity found with id: " + entityId);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                case MONGODB -> {
                    Document entity = mongoDB.getCollection(collection).find(Filters.eq("entityId", entityId)).first();
                    return entity != null;
                }
                default -> {
                    return false;
                }
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
        switch (type) {
            case FILE -> {
                File[] files = this.entitiesDirectory.listFiles((dir, name) -> name.endsWith(".entity"));
                if (files != null) {
                    Arrays.stream(files).forEach(file -> entityIDs.add(UUID.fromString(file.getName().replace(".entity", ""))));
                }
            }
            case SQL -> {
                String query = "SELECT entity_id FROM " + collection;
                try {
                    PreparedStatement preparedStatement = Objects.requireNonNull(database.getConnection()).prepareStatement(query);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        entityIDs.add(UUID.fromString(rs.getString("entity_id")));
                    }
                    rs.close();
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            case MONGODB -> {
                for (Document doc : mongoDB.getCollection(collection).find()) {
                    entityIDs.add((UUID) doc.get("entity_id"));
                }
            }
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
            this.customData.append("entity_id", entityId.toString());
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

    /**
     * Represents the type of storage to use for entity data.
     */
    public enum Type {
        /**
         * Use a directory of FILE files for storage.
         */
        FILE,

        /**
         * Use a SQL database for storage.
         */
        SQL,

        /**
         * Use a MongoDB database for storage.
         */
        MONGODB,
        ;

        public Type getType() {
            return this;
        }
    }
}
