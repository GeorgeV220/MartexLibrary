package com.georgev22.api.database.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Objects;

public class MongoDB {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private MongoCollection<Document> collection;

    /**
     * @param host         Host of the MongoDB (must contain port) format: localhost:27077
     * @param port         MongoDB port
     * @param username     MongoDB username
     * @param password     User password
     * @param databaseName database name
     */
    public MongoDB(String host, int port, String username, String password, String databaseName) {
        mongoClient = MongoClients.create("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=" + databaseName);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    /**
     * @param host           Host of the MongoDB (must contain port) format: localhost:27077
     * @param port           MongoDB port
     * @param username       MongoDB username
     * @param password       User password
     * @param databaseName   database name
     * @param collectionName collection name
     * @deprecated for removal on feature release
     */
    @Deprecated(since = "3.0.0", forRemoval = true)
    public MongoDB(String host, int port, String username, String password, String databaseName, String collectionName) {
        mongoClient = MongoClients.create("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=" + databaseName);
        mongoDatabase = mongoClient.getDatabase(databaseName);
        collection = mongoDatabase.getCollection(collectionName);
    }

    /**
     * Gets a collection.
     *
     * @param collectionName the name of the collection to return
     * @return the collection
     * @throws IllegalArgumentException if collectionName is invalid
     * @see com.mongodb.MongoNamespace#checkCollectionNameValidity(String)
     * @deprecated for removal on feature release
     */
    @Deprecated
    public MongoCollection<Document> getCollection(String collectionName) {
        return mongoDatabase.getCollection(collectionName);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    /**
     * Gets the collection that was given during the initialization of the class
     *
     * @return the collection
     * @throws IllegalArgumentException if collectionName is invalid
     * @see com.mongodb.MongoNamespace#checkCollectionNameValidity(String)
     */
    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MongoDB mongoDB)) return false;
        return Objects.equals(mongoClient, mongoDB.mongoClient) && Objects.equals(mongoDatabase, mongoDB.mongoDatabase) && Objects.equals(collection, mongoDB.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoClient, mongoDatabase, collection);
    }

    @Override
    public String toString() {
        return "MongoDB{" +
                "mongoClient=" + mongoClient +
                ", mongoDatabase=" + mongoDatabase +
                '}';
    }
}
