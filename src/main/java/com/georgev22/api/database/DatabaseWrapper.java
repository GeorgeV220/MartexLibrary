package com.georgev22.api.database;

import com.georgev22.api.database.mongo.MongoDB;
import com.georgev22.api.database.sql.mysql.MySQL;
import com.georgev22.api.database.sql.postgresql.PostgreSQL;
import com.georgev22.api.database.sql.sqlite.SQLite;
import com.georgev22.api.exceptions.DatabaseConnectionException;
import com.georgev22.api.exceptions.DatabaseException;
import com.georgev22.api.maps.HashObjectMap;
import com.georgev22.api.maps.ObjectMap;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static com.georgev22.api.utilities.Utils.Assertions.notNull;

public class DatabaseWrapper {

    private final DatabaseType type;

    private final String[] data;

    private Connection connection;

    private Database database;

    private @Nullable MongoClient mongoClient = null;
    private @Nullable MongoDatabase mongoDatabase = null;

    public DatabaseWrapper(DatabaseType type, String... data) {
        this.type = type;
        this.data = data;
    }

    public DatabaseType getType() {
        return type;
    }

    public String[] getData() {
        return data;
    }

    /**
     * Attempts to establish a connection to the given database.
     *
     * @return a connection to the database.
     */
    public DatabaseWrapper connect() {
        switch (type) {
            case MYSQL -> {
                try {
                    database = new MySQL(data[0], Integer.parseInt(data[1]), data[2], data[3], Optional.ofNullable(data[4]));
                    connection = database.openConnection();
                } catch (SQLException | ClassNotFoundException exception) {
                    throw new DatabaseConnectionException("Unable to connect to the " + type.getName() + " Database", exception);
                }
            }
            case SQLITE -> {
                try {
                    database = new SQLite(new File(data[0]), data[1]);
                    connection = database.openConnection();
                } catch (SQLException | ClassNotFoundException exception) {
                    throw new DatabaseConnectionException("Unable to connect to the " + type.getName() + " Database", exception);
                }
            }
            case PROSTGRESQL -> {
                try {
                    database = new PostgreSQL(data[0], Integer.parseInt(data[1]), data[2], data[3], Optional.ofNullable(data[4]));
                    connection = database.openConnection();
                } catch (SQLException | ClassNotFoundException exception) {
                    throw new DatabaseConnectionException("Unable to connect to the " + type.getName() + " Database", exception);
                }
            }

            case MONGO -> {
                mongoClient = new MongoDB(data[0], Integer.parseInt(data[1]), data[2], data[3], data[4]).getMongoClient();
                mongoDatabase = getMongoDatabase();
            }
        }
        return this;
    }

    /**
     * Retrieves whether the {@code Connection} (or when mongoClient is null) object has been
     * closed or not.
     *
     * @return {@code false} if the {@code Connection} (or when mongoClient is null) object
     * is closed; {@code true} if it is still open
     */
    public boolean isConnected() {
        if (!type.equals(DatabaseType.MONGO))
            try {
                return connection != null & !connection.isClosed();
            } catch (SQLException ignored) {
                return false;
            }
        else
            return mongoClient != null & mongoDatabase != null;
    }

    /**
     * Insert data into the database (SQL or MongoDB)
     *
     * @param data       Data to be inserted
     * @param mongoQuery Mongo query string (eg "uuid")
     * @return The {@link DatabaseWrapper} instance.
     */
    public DatabaseWrapper insertData(@NotNull ObjectMap.Pair<String, ObjectMap<String, String>> data, @Nullable ObjectMap.Pair<String, String> mongoQuery) {
        if (!isConnected()) {
            throw new DatabaseException("Database is not connected!");
        }
        if (!type.equals(DatabaseType.MONGO)) {
            StringBuilder completeStringBuilder = new StringBuilder();
            StringBuilder stringBuilderKeys = new StringBuilder();
            StringBuilder stringBuilderValues = new StringBuilder();
            Iterator<Map.Entry<String, String>> dataIterator = notNull("data value", data.value()).entrySet().iterator();
            while (dataIterator.hasNext()) {
                Map.Entry<String, String> entry = dataIterator.next();
                stringBuilderKeys.append("`").append(entry.getKey()).append("`");
                stringBuilderValues.append("'").append(entry.getValue()).append("'");
                if (dataIterator.hasNext()) {
                    stringBuilderKeys.append(", ");
                    stringBuilderValues.append(", ");
                }
            }
            completeStringBuilder
                    .append("INSERT INTO `")
                    .append(notNull("data key", data.key()))
                    .append("` (")
                    .append(stringBuilderKeys)
                    .append(") VALUES (")
                    .append(stringBuilderValues)
                    .append(");");
            try {
                database.updateSQL(completeStringBuilder.toString());
            } catch (SQLException | ClassNotFoundException exception) {
                throw new DatabaseException("Unable to insert data into the database", exception);
            }
        } else {
            if (mongoQuery != null) {
                BasicDBObject query = new BasicDBObject();
                query.append(mongoQuery.key(), mongoQuery.value());

                BasicDBObject updateObject = new BasicDBObject();
                updateObject.append("$set", new BasicDBObject());
                for (Map.Entry<String, String> entry : notNull("data value", data.value()).entrySet()) {
                    updateObject.append(entry.getKey(), entry.getValue());
                }
                getCollection(notNull("data key", data.key())).updateOne(query, updateObject);
            }
        }
        return this;
    }

    /**
     * Update the data on the database (SQL or MongoDB)
     *
     * @param data       Data to be updated
     * @param mongoQuery Mongo query string (eg "uuid")
     * @return The {@link DatabaseWrapper} instance.
     */
    public DatabaseWrapper updateData(@NotNull ObjectMap.Pair<String, ObjectMap<String, String>> data, @Nullable ObjectMap.Pair<String, String> mongoQuery) {
        if (!isConnected()) {
            throw new DatabaseException("Database is not connected!");
        }
        if (!type.equals(DatabaseType.MONGO)) {
            StringBuilder stringBuilder = new StringBuilder("UPDATE `" + data.key() + "` SET ");
            Iterator<Map.Entry<String, String>> dataIterator = notNull("data value", data.value()).entrySet().iterator();
            while (dataIterator.hasNext()) {
                Map.Entry<String, String> entry = dataIterator.next();
                stringBuilder.append("`").append(entry.getKey()).append("` = '").append(entry.getValue()).append("'");
                if (dataIterator.hasNext()) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(");");
            try {
                database.updateSQL(stringBuilder.toString());
            } catch (SQLException | ClassNotFoundException exception) {
                throw new DatabaseException("Unable to update the data on the database", exception);
            }
        } else {
            return insertData(data, mongoQuery);
        }
        return this;
    }

    /**
     * Selects everything (WHERE = "your key")
     * from a specific table and returns a result set with the data
     *
     * @param select select data (table name, (key to search, value of the key))
     * @return A {@link ResultSet} with the data.
     */
    private ResultSet resultSet(ObjectMap.Pair<String, ObjectMap.Pair<String, Object>> select) {
        if (!isConnected()) {
            throw new DatabaseException("Database is not connected!");
        }
        try {
            return database.querySQL("SELECT * FROM `" + select.key() + "` WHERE '" + select.value().key() + "' = '" + select.value().value() + "');");
        } catch (SQLException | ClassNotFoundException exception) {
            throw new DatabaseException("Unable to select data from the database", exception);
        }
    }


    //TODO JAVADOCS
    private Document getDocument(ObjectMap.Pair<String, ObjectMap.Pair<String, Object>> select) {
        if (!isConnected()) {
            throw new DatabaseException("Database is not connected!");
        }
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.append(select.value().key(), select.value().value());
        FindIterable<Document> findIterable = getCollection(select.key()).find(searchQuery);
        return findIterable.first();
    }

    /**
     * Retrieves the data from the database and returns them in an {@link ObjectMap}
     *
     * @param select Data to retrieve
     * @return an {@link ObjectMap} with the retrieved data.
     */
    public ObjectMap<String, Object> selectData(ObjectMap.Pair<String, ObjectMap.Pair<String, Object>> select) {
        ObjectMap<String, Object> objectMap = new HashObjectMap<>();
        if (!type.equals(DatabaseType.MONGO)) {
            try {
                if (!select.value().value().getClass().equals(String.class))
                    throw new DatabaseException("Pair value must be a string");
                ResultSet resultSet = resultSet(select);
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    objectMap.append(resultSetMetaData.getColumnName(i), resultSet.getString(i));
                }
            } catch (SQLException exception) {
                throw new DatabaseException("Unable to select data from the database", exception);
            }
        } else {
            Document document = getDocument(select);
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                objectMap.append(entry.getKey(), entry.getValue());
            }
        }
        return objectMap;
    }

    /**
     * Returns the SQL {@link Connection} or null
     *
     * @return the SQL {@link Connection} or null
     */
    public @Nullable Connection getSQLConnection() {
        return connection;
    }

    /**
     * Returns the MongoDB Client or null
     *
     * @return the MongoDB Client or null
     */
    public @Nullable MongoClient getMongoClient() {
        return mongoClient;
    }

    /**
     * Returns the MongoDB Database or null
     *
     * @return the MongoDB Database or null
     */
    public @Nullable MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * Returns a MongoDB collection.
     *
     * @param collectionName the name of the collection to return
     * @return the collection
     * @throws IllegalArgumentException if collectionName is invalid
     * @see com.mongodb.MongoNamespace#checkCollectionNameValidity(String)
     */
    public MongoCollection<Document> getCollection(String collectionName) {
        return mongoDatabase.getCollection(collectionName);
    }
}
