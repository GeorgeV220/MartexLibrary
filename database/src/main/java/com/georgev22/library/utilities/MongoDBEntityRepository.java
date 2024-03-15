package com.georgev22.library.utilities;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.exceptions.NoSuchConstructorException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple repository manager for MongoDB entities requiring setters for each field and a constructor without varargs.
 *
 * @param <V> The type of the entity.
 */
public class MongoDBEntityRepository<V extends Entity> implements EntityRepository<V> {

    private final ObjectMap<String, V> loadedEntities = new HashObjectMap<>();
    private final MongoDatabase mongoDatabase;
    private final Logger logger;
    private final Class<V> entityClass;
    private final String collectionName;

    /**
     * Constructs a MongoDBEntityRepository with the specified MongoDB database, logger, and entity class.
     *
     * @param mongoDatabase The MongoDB database to be used.
     * @param logger        The logger for handling log messages.
     * @param entityClass   The class type of the entity managed by this repository.
     */
    public MongoDBEntityRepository(MongoDatabase mongoDatabase, Logger logger, Class<V> entityClass) {
        this(mongoDatabase, logger, entityClass, entityClass.getSimpleName());
    }

    /**
     * Constructs a MongoDBEntityRepository with the specified MongoDB database, logger, and entity class.
     *
     * @param mongoDatabase  The MongoDB database to be used.
     * @param logger         The logger for handling log messages.
     * @param entityClass    The class type of the entity managed by this repository.
     * @param collectionName The name of the collection in the database.
     */
    public MongoDBEntityRepository(MongoDatabase mongoDatabase, Logger logger, Class<V> entityClass, String collectionName) {
        this.mongoDatabase = mongoDatabase;
        this.logger = logger;
        this.entityClass = entityClass;
        this.collectionName = collectionName;
    }

    /**
     * Saves the given entity to the MongoDB database. Generates and executes a MongoDB document based on entity changes.
     *
     * @param entity The entity to be saved.
     */
    @Override
    public V save(V entity) {
        Document document = new Document(getValuesMap(entity));

        if (exists(entity._id(), true, false)) {
            MongoCollection<Document> collection = mongoDatabase.getCollection(this.collectionName);
            collection.replaceOne(new Document("_id", entity._id()), document);
        } else {
            MongoCollection<Document> collection = mongoDatabase.getCollection(this.collectionName);
            collection.insertOne(document);
        }
        this.loadedEntities.append(entity._id(), entity);
        return entity;
    }

    /**
     * Loads an entity from the MongoDB database based on the specified entity ID.
     *
     * @param entityId The ID of the entity to be loaded.
     */
    @Override
    public V load(String entityId) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(this.collectionName);
        Document document = collection.find(new Document("_id", entityId)).first();

        if (document != null) {
            try {
                this.checkForConstructorWithSingleVarargString(this.entityClass);
                V entity = this.entityClass.getConstructor(String.class).newInstance(entityId);

                for (Map.Entry<String, Object> key : document.entrySet()) {
                    entity.setValue(key.getKey(), key.getValue());
                }

                loadedEntities.append(entityId, entity);
                return entity;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchConstructorException e) {
                this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            }
        }
        return null;
    }

    /**
     * Retrieves the loaded entity with the specified entity ID.
     *
     * @param entityId The ID of the entity to be retrieved.
     * @return The loaded entity, or null if not found.
     */
    @Override
    public V getEntity(String entityId) {
        return loadedEntities.get(entityId);
    }

    /**
     * Checks if an entity with the specified ID is already loaded.
     * If not in the cache, checks the database and loads it into the cache if found.
     *
     * @param entityId  The ID of the entity to check for existence
     * @param checkDb   Check if the entity exists in the database
     * @param forceLoad Force load the entity
     * @return True if the entity is loaded, false otherwise.
     */
    @Override
    public boolean exists(String entityId, boolean checkDb, boolean forceLoad) {
        if (loadedEntities.containsKey(entityId)) {
            return true;
        }

        if (checkDb) {
            MongoCollection<Document> collection = mongoDatabase.getCollection(this.collectionName);
            Document document = collection.find(new Document("_id", entityId)).first();

            return forceLoad ? this.load(entityId) != null : document != null;
        }

        return false;
    }


    /**
     * Deletes the entity with the specified ID from the MongoDB database and removes it from the loaded entities.
     *
     * @param entityId The ID of the entity to be deleted.
     */
    @Override
    public void delete(String entityId) {
        if (!exists(entityId, true, false)) {
            this.logger.log(Level.WARNING, "[EntityRepository]: Entity with ID " + entityId + " does not exist.");
            return;
        }

        MongoCollection<Document> collection = mongoDatabase.getCollection(this.collectionName);
        collection.deleteOne(new Document("_id", entityId));
        loadedEntities.remove(entityId);
    }

    /**
     * Loads all entities from the database.
     */
    @Override
    public void loadAll() {
        FindIterable<Document> documents = mongoDatabase.getCollection(this.collectionName).find();

        for (Document document : documents) {
            Object idValue = document.get("_id");

            if (idValue != null) {
                this.load(idValue.toString());
            }
        }
    }

    /**
     * Saves all loaded entities to the database.
     */
    @Override
    public void saveAll() {
        for (V entity : loadedEntities.values()) {
            this.save(entity);
        }
    }

    /**
     * Gets the MongoDB database associated with this repository.
     *
     * @return The MongoDB database.
     */
    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * Gets the logger associated with this repository.
     *
     * @return The logger.
     */
    public Logger getLogger() {
        return logger;
    }
}
