package com.georgev22.library.utilities;

import com.georgev22.library.database.mongo.MongoDB;
import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.maps.utilities.ObjectMapSerializerDeserializer;
import com.google.common.annotations.Beta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link EntityManager} class is responsible for managing {@link Entity} objects in a persistence storage.
 * It supports multiple storage types including MySQL, SQLite, PostgreSQL, MongoDB, and JSON.
 * The class provides methods for checking if a {@link Entity} exists,
 * loading a {@link Entity}, and creating a {@link Entity}.
 *
 * @author <a href="https://github.com/GeorgeV220">GeorgeV220</a>
 */
public class EntityManager {

    private GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson;
    private final File entitiesDirectory;
    private final Connection connection;
    private final MongoDB mongoDB;
    private final String collection;
    private final Type type;
    private final ObservableObjectMap<UUID, Entity> loadedEntities = new ObservableObjectMap<>();

    /**
     * Constructor for the EntityManager class
     *
     * @param type           the type of storage system to be used (JSON, SQL or MONGODB)
     * @param obj            the object to be used for storage (File for JSON, Connection for SQL and MongoDB for MONGODB)
     * @param collectionName the name of the collection to be used for MONGODB and SQL, null for other types
     */
    public EntityManager(@NotNull Type type, Object obj, @Nullable String collectionName) {
        this.type = type;
        this.collection = collectionName;
        switch (type) {
            case JSON -> {
                this.entitiesDirectory = (File) obj;
                this.connection = null;
                this.mongoDB = null;
                if (!this.entitiesDirectory.exists()) {
                    this.entitiesDirectory.mkdirs();
                }
            }
            case SQL -> {
                this.entitiesDirectory = null;
                this.connection = (Connection) obj;
                this.mongoDB = null;
            }
            case MONGODB -> {
                this.entitiesDirectory = null;
                this.connection = null;
                this.mongoDB = (MongoDB) obj;
            }
            default -> {
                this.entitiesDirectory = null;
                this.connection = null;
                this.mongoDB = null;
            }
        }
    }

    /**
     * Adds a type adapter for ObjectMap to the GsonBuilder. Deprecated; use {@link #registerTypeAdaptersByClass} or {@link #registerTypeAdaptersByTypeToken} instead.
     * <p>
     * This method will not be removed, but it is recommended to use the newer methods for registering type adapters.
     *
     * @return this EntityManager
     * @deprecated Use {@link #registerTypeAdaptersByClass(ObjectMap.PairDocument)} or {@link #registerTypeAdaptersByTypeToken(ObjectMap.PairDocument)} instead.
     */
    @Deprecated
    public EntityManager registerObjectMapSerializer() {
        gsonBuilder.registerTypeAdapter(ObjectMap.class, new ObjectMapSerializerDeserializer());
        return this;
    }

    /**
     * Register type adapters for the specified classes using the GsonBuilder.
     *
     * @param pairs a PairDocument containing the Class and type adapter pairs to register
     * @return this EntityManager
     */
    public EntityManager registerTypeAdaptersByClass(@NotNull ObjectMap.PairDocument<Class<?>, Object> pairs) {
        for (ObjectMap.Pair<Class<?>, Object> pair : pairs.objectPairs()) {
            gsonBuilder.registerTypeAdapter(pair.key(), pair.value());
        }
        return this;
    }

    /**
     * Register type adapters for the specified TypeTokens using the GsonBuilder.
     *
     * @param pairs a PairDocument containing the TypeToken and type adapter pairs to register
     * @return this EntityManager
     */
    public EntityManager registerTypeAdaptersByTypeToken(@NotNull ObjectMap.PairDocument<TypeToken<?>, Object> pairs) {
        for (ObjectMap.Pair<TypeToken<?>, Object> pair : pairs.objectPairs()) {
            gsonBuilder.registerTypeAdapter(pair.key().getType(), pair.value());
        }
        return this;
    }

    /**
     * Builds a new Gson instance with the registered type adapters and pretty printing enabled.
     *
     * @return the new Gson instance
     */
    public Gson getGson() {
        return gsonBuilder.setPrettyPrinting().create();
    }

    /**
     * Loads the {@link Entity} with the specified ID
     *
     * @param entityId the {@link UUID} of the entity to be loaded
     * @return a {@link CompletableFuture} containing the loaded {@link Entity} object
     */
    public CompletableFuture<Entity> load(UUID entityId) {
        return exists(entityId)
                .thenCompose(exists -> {
                    if (exists) {
                        return CompletableFuture.supplyAsync(() -> {
                            switch (type) {
                                case JSON -> {
                                    try (FileReader reader = new FileReader(new File(entitiesDirectory, entityId + ".json"))) {
                                        Entity entity = getGson().fromJson(reader, Entity.class);
                                        loadedEntities.put(entityId, entity);
                                        return entity;
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case SQL -> {
                                    String query = "SELECT entity_json FROM " + collection + " WHERE entity_id = ?";
                                    try {
                                        PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
                                        statement.setString(1, entityId.toString());
                                        ResultSet resultSet = statement.executeQuery();
                                        if (resultSet.next()) {
                                            String entityJson = resultSet.getString("entity_json");
                                            statement.close();
                                            Entity entity = getGson().fromJson(entityJson, Entity.class);
                                            loadedEntities.put(entityId, entity);
                                            return entity;
                                        } else {
                                            throw new RuntimeException("No entity found with id: " + entityId);
                                        }
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case MONGODB -> {
                                    Document document = mongoDB.getCollection(collection).find(Filters.eq("entityId", entityId.toString())).first();
                                    if (document != null) {
                                        Entity entity = getGson().fromJson(document.toJson(), Entity.class);
                                        loadedEntities.put(entityId, entity);
                                        return entity;
                                    } else {
                                        throw new RuntimeException("No entity found with id: " + entityId);
                                    }
                                }
                                default -> {
                                    return new Entity(entityId);
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
                case JSON -> {
                    try (FileWriter writer = new FileWriter(new File(entitiesDirectory, entity.getId() + ".json"))) {
                        getGson().toJson(entity, writer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case SQL -> exists(entity.getId()).thenAccept(result -> {
                    String query = result ? "UPDATE " + collection + " SET entity_json = ? WHERE entity_id =  ?;" : "INSERT INTO " + collection + " (entity_id, entity_json) VALUES (?, ?)";
                    try {
                        PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
                        String entityJson = getGson().toJson(entity);
                        statement.setString(1, entityJson);
                        statement.setString(2, entity.getId().toString());
                        statement.executeUpdate();
                        statement.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                case MONGODB -> {
                    MongoCollection<Document> mongoCollection = mongoDB.getCollection(collection);
                    Document document = Document.parse(getGson().toJson(entity));
                    mongoCollection.insertOne(document);
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
    public CompletableFuture<Entity> createEntity(UUID entityId) {
        Entity entity = new Entity(entityId);
        return save(entity)
                .thenApply(aVoid -> {
                    loadedEntities.put(entityId, entity);
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
                case JSON -> {
                    return new File(entitiesDirectory, entityId + ".json").exists();
                }
                case SQL -> {
                    return executeSQLQuery(entityId);
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

    private @NotNull Boolean executeSQLQuery(@NotNull UUID entityId) {
        String query = "SELECT count(*) FROM " + collection + " WHERE entity_id = ?";
        try {
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
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
    public CompletableFuture<Entity> getEntity(UUID entityId) {
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
            case JSON -> {
                File[] files = this.entitiesDirectory.listFiles((dir, name) -> name.endsWith(".json"));
                if (files != null) {
                    Arrays.stream(files).forEach(file -> entityIDs.add(UUID.fromString(file.getName().replace(".json", ""))));
                }
            }
            case SQL -> {
                String query = "SELECT entity_id FROM " + collection;
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    ResultSet rs = preparedStatement.executeQuery(query);
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
    public ObservableObjectMap<UUID, Entity> getLoadedEntities() {
        return loadedEntities;
    }

    /**
     * A class representing an entity in the system.
     */
    public static class Entity {
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

    /**
     * Represents the type of storage to use for entity data.
     */
    public enum Type {
        /**
         * Use a directory of JSON files for storage.
         */
        JSON,

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
