package com.georgev22.library.database;

import com.georgev22.library.database.mongo.MongoDB;
import com.georgev22.library.database.sql.mysql.MySQL;
import com.georgev22.library.database.sql.postgresql.PostgreSQL;
import com.georgev22.library.database.sql.sqlite.SQLite;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObjectMap.Pair;
import com.georgev22.library.utilities.Utils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper class for interacting with different types of databases.
 */
public class DatabaseWrapper {
    private DatabaseType dbType;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;
    private final Logger logger;
    private Connection sqlConnection;
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private Database sqlDatabase;

    /**
     * Constructs a DatabaseWrapper object.
     *
     * @param dbType   the type of the database
     * @param host     the host of the database (or file path for SQLite)
     * @param port     the port of the database
     * @param username the username for the database connection
     * @param password the password for the database connection
     * @param database the name of the database (or file name for SQLite without the file extension .db)
     * @param logger   the logger
     */
    public DatabaseWrapper(DatabaseType dbType, String host, int port, String username, String password, String database, Logger logger) {
        this.dbType = dbType;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.logger = logger;
        this.sqlConnection = null;
        this.sqlDatabase = null;
        this.mongoDatabase = null;
    }

    /**
     * Connects to the database.
     */
    public void connect() {
        try {
            switch (dbType) {
                case MYSQL -> {
                    this.sqlDatabase = new MySQL(host, port, username, password, Optional.ofNullable(database));
                    this.sqlConnection = sqlDatabase.openConnection();
                }
                case SQLITE -> {
                    this.sqlDatabase = new SQLite(new File(host), database);
                    this.sqlConnection = sqlDatabase.openConnection();
                }
                case POSTGRESQL -> {
                    this.sqlDatabase = new PostgreSQL(host, port, username, password, Optional.ofNullable(database));
                    this.sqlConnection = sqlDatabase.openConnection();
                }
                case MONGO -> {
                    MongoDB mongoDB = new MongoDB(host, port, username, password, database);
                    this.mongoDatabase = mongoDB.getMongoDatabase();
                    this.mongoClient = mongoDB.getMongoClient();
                }

                default ->
                        this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", new IllegalArgumentException("Invalid database type"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
        }
    }

    /**
     * Adds data to the specified collection based on the database type.
     * Example usage for adding data to the database using the addData method for SQL.
     *
     * <pre>{@code
     * String tableName = "users";
     * String name = "John";
     * int age = 30;
     * ObjectMap<String, Object> columnValues = new HashObjectMap<String, Object>()
     *         .append("name", name)
     *         .append("age", age);
     * Pair<String, ObjectMap<String, Object>> pair = new Pair<>("", columnValues);
     * databaseWrapper.addData(tableName, pair);
     * }</pre>
     * <p>
     * Example usage for adding data to the database using the addData method for MongoDB.
     *
     * <pre>{@code
     * String collectionName = "users";
     * String name = "John";
     * int age = 30;
     * ObjectMap<String, Object> columnValues = new HashObjectMap<String, Object>()
     *         .append("name", name)
     *         .append("age", age);
     * Pair<String, ObjectMap<String, Object>> pair = new Pair<>(uuid.toString(), columnValues);
     * databaseWrapper.addData(collectionName, pair);
     * }</pre>
     *
     * @param collectionName the name of the collection or table
     * @param pair           the Pair object containing the key-value pair for the data
     * @throws IllegalArgumentException if an invalid database type is specified
     */
    public void addData(String collectionName, Pair<String, ObjectMap<String, Object>> pair) {
        switch (dbType) {
            case MYSQL, POSTGRESQL, SQLITE -> {
                ObjectMap<String, Object> columnValues = pair.value();
                String insertQuery = this.sqlDatabase.buildInsertStatement(collectionName, columnValues);
                try (PreparedStatement statement = Objects.requireNonNull(this.getSQLConnection()).prepareStatement(insertQuery)) {
                    int parameterIndex = 1;
                    for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        int columnType = this.sqlDatabase.getColumnDataType(collectionName, key, sqlConnection);
                        if (columnType == Types.BLOB || columnType == Types.LONGVARBINARY) {
                            statement.setObject(parameterIndex, Utils.serializeObjectToString(value));
                        } else {
                            statement.setString(parameterIndex, String.valueOf(value));
                        }
                        parameterIndex++;
                    }

                    statement.executeUpdate();
                } catch (Exception e) {
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
                }
            }
            case MONGO -> {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                Document document = new Document(pair.key(), new Document(pair.value()));
                collection.insertOne(document);
            }
            default ->
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", new IllegalArgumentException("Invalid database type"));
        }
    }

    /**
     * Removes data from the specified collection based on the database type and condition.
     * Example usage for removing data from the database using the removeData method for SQL.
     *
     * <pre>{@code
     * String tableName = "users";
     * Pair<String, Object> pair = new Pair<>("name", "John");
     * String condition = "="; // Optional condition
     * databaseWrapper.removeData(tableName, pair, condition);
     * }</pre>
     * <p>
     * Example usage for removing data from the database using the removeData method for MongoDB.
     *
     * <pre>{@code
     * String collectionName = "users";
     * Pair<String, Object> pair = new Pair<>("name", "John");
     * String condition = "$eq"; // Optional condition
     * databaseWrapper.removeData(collectionName, pair, condition);
     * }</pre>
     *
     * @param collectionName the name of the collection or table
     * @param pair           the Pair object containing the key-value pair for the condition
     * @param condition      the condition for removing data (e.g., "=" for SQL, "$eq" for MongoDB. optional, can be null)
     * @throws IllegalArgumentException if an invalid database type is specified
     */
    public void removeData(String collectionName, Pair<String, Object> pair, @Nullable String condition) {
        if (condition == null || condition.equals("")) {
            condition = dbType.equals(DatabaseType.MONGO) ? "$eq" : "=";
        }
        switch (dbType) {
            case MYSQL, POSTGRESQL, SQLITE -> {
                String conditionQuery = pair.key() + " " + condition + " ?";
                String deleteQuery = this.sqlDatabase.buildDeleteStatement(collectionName, conditionQuery);
                try {
                    try (PreparedStatement statement = sqlConnection.prepareStatement(deleteQuery)) {
                        statement.setObject(1, pair.value());
                        statement.executeUpdate();
                    }
                } catch (SQLException e) {
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
                }
            }
            case MONGO -> {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                Document filter = new Document(pair.key(), new Document(condition, pair.value()));
                collection.deleteMany(filter);
            }
            default ->
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", new IllegalArgumentException("Invalid database type"));
        }
    }

    /**
     * Updates data in the specified collection based on the filter and update pairs and the condition.
     * <p>
     * Example usage for updating data in the database using the updateData method for SQL.
     *
     * <pre>{@code
     * String tableName = "users";
     * Pair<String, Object> filterPair = new Pair<>("name", "John");
     * Pair<String, ObjectMap<String, Object>> updatePair = new Pair<>("", new ObjectMap<String, Object>()
     *     .append("age", 10));
     * String condition = "="; // Optional condition
     * databaseWrapper.updateData(tableName, filterPair, updatePair, condition);
     * }</pre>
     * <p>
     * Example usage for updating data in the database using the updateData method for MongoDB.
     *
     * <pre>{@code
     * String collectionName = "users";
     * Pair<String, Object> filterPair = new Pair<>("name", "John");
     * Pair<String, ObjectMap<String, Object>> updatePair = new Pair<>("$inc", new ObjectMap<String, Object>()
     *     .append("age", 10));
     * String condition = "$eq"; // Optional condition
     * databaseWrapper.updateData(collectionName, filterPair, updatePair, condition);
     * }</pre>
     *
     * @param collectionName the name of the collection or table
     * @param filterPair     the Pair object containing the key-value pair for filtering data
     * @param updatePair     the Pair object containing the key-value pairs for updating data
     * @param condition      the condition for updating data (e.g., "=" for SQL, "$eq" for MongoDB. optional, can be null)
     * @throws IllegalArgumentException if an invalid database type is specified
     */
    public void updateData(String collectionName, Pair<String, Object> filterPair, Pair<String, ObjectMap<String, Object>> updatePair, @Nullable String condition) {
        if (condition == null || condition.equals("")) {
            condition = dbType.equals(DatabaseType.MONGO) ? "$eq" : "=";
        }
        switch (dbType) {
            case MYSQL, POSTGRESQL, SQLITE -> {
                String filterCondition = filterPair.key() + " " + condition + " '" + filterPair.value() + "'";
                Map<String, Object> updateColumnValues = updatePair.value();
                String updateQuery = this.sqlDatabase.buildUpdateStatement(collectionName, updateColumnValues, filterCondition);
                this.logger.info(updateQuery);
                try (PreparedStatement statement = sqlConnection.prepareStatement(updateQuery)) {
                    int parameterIndex = 1;
                    for (Map.Entry<String, Object> entry : updatePair.value().entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        int columnType = this.sqlDatabase.getColumnDataType(collectionName, key, sqlConnection);
                        if (columnType == Types.BLOB || columnType == Types.LONGVARBINARY) {
                            statement.setObject(parameterIndex, Utils.serializeObjectToString(value));
                        } else {
                            statement.setString(parameterIndex, String.valueOf(value));
                        }
                        parameterIndex++;
                    }
                    statement.executeUpdate();
                } catch (SQLException | IOException e) {
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
                }
            }
            case MONGO -> {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                Document filter = new Document(filterPair.key(), new Document(condition, filterPair.value()));
                Document update = new Document("$set", new Document(updatePair.key(), new Document(updatePair.value())));
                collection.updateMany(filter, update);
            }
            default ->
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", new IllegalArgumentException("Invalid database type"));
        }
    }

    /**
     * Retrieves data from the specified collection based on the condition pair and condition.
     * <p>
     * Example usage for retrieving data from the database using the retrieveData method for SQL databases.
     * <pre>{@code
     * String collectionName = "users";
     * Pair<String, Object> conditionPair = new Pair<>("name", "John");
     * Pair<String, List<DatabaseObject>> result = databaseWrapper.retrieveData(collectionName, conditionPair);
     * }</pre>
     * <p>
     * Example usage for retrieving data from the database using the retrieveData method for MongoDB.
     * <pre>{@code
     * String collectionName = "users";
     * Pair<String, Object> conditionPair = new Pair<>("name", "John");
     * Pair<String, List<DatabaseObject>> result = databaseWrapper.retrieveData(collectionName, conditionPair);
     * }</pre>
     *
     * @param collectionName the name of the collection or table
     * @param conditionPair  the Pair object containing the key-value pair for the condition
     * @return a Pair object containing the collection name and a List of DatabaseObject
     * @throws IllegalArgumentException if an invalid database type is specified
     */
    public Pair<String, List<DatabaseObject>> retrieveData(String collectionName, Pair<String, @Nullable Object> conditionPair) {
        final Pair<String, List<DatabaseObject>> stringDatabaseObjectPair = new Pair<>(collectionName, new ArrayList<>());
        switch (dbType) {
            case MYSQL, POSTGRESQL, SQLITE -> {
                String selectQuery;
                if (conditionPair.value() == null) {
                    selectQuery = "SELECT " + conditionPair.key() + " FROM " + collectionName;
                } else {
                    selectQuery = "SELECT * FROM " + collectionName + " WHERE " + conditionPair.key() + " = ?";
                }
                try (PreparedStatement statement = Objects.requireNonNull(this.getSQLConnection()).prepareStatement(selectQuery)) {
                    if (conditionPair.value() != null)
                        statement.setObject(1, conditionPair.value());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        List<DatabaseObject> databaseObjects = new ArrayList<>();
                        while (resultSet.next()) {
                            ObjectMap<String, Object> results = new HashObjectMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                String columnName = metaData.getColumnName(i);
                                int columnType = this.sqlDatabase.getColumnDataType(collectionName, columnName, sqlConnection);
                                Object value = resultSet.getObject(i);
                                if (columnType == Types.BLOB || columnType == Types.LONGVARBINARY) {
                                    results.append(columnName, Utils.deserializeObjectFromString(String.valueOf(value)));
                                } else {
                                    results.append(columnName, value);
                                }
                            }
                            databaseObjects.add(new DatabaseObject(results));
                        }
                        return new Pair<>(collectionName, databaseObjects);
                    } catch (SQLException | IOException | ClassNotFoundException e) {
                        logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
                        return stringDatabaseObjectPair;
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
                    return stringDatabaseObjectPair;
                }
            }
            case MONGO -> {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                FindIterable<Document> result;
                if (conditionPair.value() == null) {
                    result = collection.find(Filters.all(conditionPair.key()));
                } else {
                    result = collection.find(Filters.eq(conditionPair.key(), conditionPair.value()));
                }
                List<DatabaseObject> databaseObjects = new ArrayList<>();
                result.forEach((Consumer<? super Document>) document -> {
                    ObjectMap<String, Object> results = new HashObjectMap<>();
                    document.forEach(results::append);
                    databaseObjects.add(new DatabaseObject(results));
                });
                return new Pair<>(collectionName, databaseObjects);
            }
            default -> {
                logger.log(Level.SEVERE, "[DatabaseWrapper]:", new IllegalArgumentException("Invalid database type"));
                return stringDatabaseObjectPair;
            }
        }
    }

    /**
     * Checks if data exists in the specified collection based on the pair and condition.
     * <p>
     * Example usage for checking data existence in the database using the exists method for SQL databases.
     * <pre>{@code
     * String collectionName = "users";
     * Pair<String, Object> pair = new Pair<>("name", "John");
     * String condition = "="; // Optional condition
     * boolean exists = databaseWrapper.exists(collectionName, pair, condition);
     * }</pre>
     * <p>
     * Example usage for checking data existence in the database using the exists method for MongoDB.
     * <pre>{@code
     * String collectionName = "users";
     * Pair<String, Object> pair = new Pair<>("name", "John");
     * String condition = "$eq"; // Optional condition
     * boolean exists = databaseWrapper.exists(collectionName, pair, condition);
     * }</pre>
     *
     * @param collectionName the name of the collection or table
     * @param pair           the Pair object containing the key-value pair to check
     * @param condition      the condition for checking data existence (e.g., "=" for SQL, "$eq" for MongoDB. optional, can be null)
     * @return true if data exists, false otherwise
     * @throws IllegalArgumentException if an invalid database type is specified
     */
    public boolean exists(String collectionName, Pair<String, Object> pair, @Nullable String condition) {
        if (condition == null || condition.equals("")) {
            condition = dbType.equals(DatabaseType.MONGO) ? "$eq" : "=";
        }
        switch (dbType) {
            case MYSQL, POSTGRESQL, SQLITE -> {
                String conditionQuery = pair.key() + " " + condition + " '" + pair.value() + "';";
                String selectQuery = "SELECT COUNT(*) FROM " + collectionName + " WHERE " + conditionQuery;
                try (Statement statement = sqlConnection.createStatement();
                     ResultSet resultSet = statement.executeQuery(selectQuery)) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                } catch (SQLException e) {
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
                }
                return false;
            }
            case MONGO -> {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                Document filter = new Document(condition, new Document(pair.key(), pair.value()));
                long count = collection.countDocuments(filter);
                return count > 0;
            }
            default -> {
                this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", new IllegalArgumentException("Invalid database type"));
                return false;
            }
        }
    }


    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        switch (dbType) {
            case MYSQL, POSTGRESQL, SQLITE -> {
                if (sqlConnection != null) {
                    try {
                        sqlConnection.close();
                    } catch (SQLException e) {
                        this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
                    }
                    sqlConnection = null;
                }
            }
            case MONGO -> {
                if (mongoDatabase != null) {
                    mongoClient.close();
                    mongoDatabase = null;
                    mongoClient = null;
                }
            }
            default ->
                    this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", new IllegalArgumentException("Invalid database type"));
        }
    }

    public boolean isConnected() {
        switch (dbType) {
            case SQLITE, MYSQL, POSTGRESQL -> {
                try {
                    return (getSQLDatabase() != null && getSQLConnection() != null) && getSQLDatabase().isClosed();
                } catch (SQLException e) {
                    return false;
                }
            }
            case MONGO -> {
                return getMongoDatabase() != null;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Returns the SQL database.
     *
     * @return the SQL database
     */
    @Nullable
    public Database getSQLDatabase() {
        return sqlDatabase;
    }

    /**
     * Returns the SQL connection.
     *
     * @return the SQL connection
     */
    @Nullable
    public Connection getSQLConnection() {
        return sqlConnection;
    }

    /**
     * Returns the MongoDB.
     *
     * @return the MongoDB
     */
    @Nullable
    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * Returns the Mongo collection with the specified name.
     *
     * @param collectionName the name of the collection
     * @return the Mongo collection
     */
    @Nullable
    public MongoCollection<Document> getCollection(String collectionName) {
        if (mongoDatabase == null) {
            return null;
        }
        try {
            return mongoDatabase.getCollection(collectionName);
        } catch (IllegalArgumentException e) {
            this.logger.log(Level.SEVERE, "[DatabaseWrapper]:", e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "DatabaseWrapper{" +
                "dbType=" + dbType.getName() +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", database='" + database + '\'' +
                ", sqlConnection=" + sqlConnection.toString() +
                ", mongoDatabase=" + mongoDatabase.toString() +
                '}';
    }

    public record DatabaseObject(ObjectMap<String, Object> data) {

        @Override
        public String toString() {
            return "DatabaseObject{" +
                    "data=" +
                    data +
                    '}';
        }
    }
}
