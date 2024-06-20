package com.georgev22.library.utilities;

import com.georgev22.library.database.sql.Database;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.exceptions.NoSuchConstructorException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple repository manager for MySQL entities requiring setters for each column and a constructor without varargs.
 *
 * @param <V> The type of the entity.
 */
public class MySQLEntityRepository<V extends Entity> implements EntityRepository<V> {

    private final ObjectMap<String, V> loadedEntities = new HashObjectMap<>();
    private final Database database;
    private final Logger logger;
    private final Class<V> entityClass;
    private final String tableName;

    /**
     * Constructs a MySQLEntityRepository with the specified database, logger, and entity class.
     *
     * @param database    The database to be used.
     * @param logger      The logger for handling log messages.
     * @param entityClass The class type of the entity managed by this repository.
     */
    public MySQLEntityRepository(Database database, Logger logger, Class<V> entityClass) {
        this(database, logger, entityClass, entityClass.getSimpleName());
    }

    /**
     * Constructs a MySQLEntityRepository with the specified database, logger, and entity class.
     *
     * @param database    The database to be used.
     * @param logger      The logger for handling log messages.
     * @param entityClass The class type of the entity managed by this repository.
     * @param tableName   The name of the table in the database.
     */
    public MySQLEntityRepository(Database database, Logger logger, Class<V> entityClass, String tableName) {
        this.database = database;
        this.logger = logger;
        this.entityClass = entityClass;
        this.tableName = tableName;
    }

    /**
     * Saves the given entity to the database. Generates and executes an SQL statement based on entity changes.
     *
     * @param entity The entity to be saved.
     */
    @Override
    public CompletableFuture<V> save(@NotNull V entity) {
        return exists(entity._id(), true, false).thenApplyAsync(exists -> {
            ObjectMap<String, Object> values = getValuesMap(entity);
            String statement;
            String entityId = escapeSql(entity._id());
            if (exists) {
                statement = this.database.buildUpdateStatement(this.tableName, values, "_id = '" + entityId + "'");
            } else {
                statement = this.database.buildInsertStatement(this.tableName, new HashObjectMap<String, Object>().append("_id", entityId).append(values));
            }

            if (statement.isEmpty()) {
                return null;
            }

            this.executeStatement(statement);

            return entity;
        });
    }

    /**
     * Executes the provided SQL statement using the repository's database connection.
     *
     * @param statement The SQL statement to be executed.
     */
    private void executeStatement(String statement) {
        try (Connection connection = this.database.getConnection()) {
            if (connection == null || connection.isClosed()) {
                try (Connection newConnection = this.database.openConnection()) {
                    newConnection.prepareStatement(statement).executeUpdate();
                }
            } else {
                connection.prepareStatement(statement).executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException e) {
            this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
        }
    }

    private @Nullable PreparedStatement querySQL(String statement) {
        try {
            Connection connection = this.database.getConnection();
            if (connection == null || connection.isClosed()) {
                connection = this.database.openConnection();
            }
            return connection.prepareStatement(statement);
        } catch (SQLException | ClassNotFoundException e) {
            this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            return null;
        }
    }

    /**
     * Loads an entity from the database based on the specified entity ID.
     *
     * @param entityId The ID of the entity to be loaded.
     */
    @Override
    public CompletableFuture<V> load(@NotNull String entityId) {
        if (loadedEntities.containsKey(entityId)) {
            this.logger.log(Level.FINE, "Entity with ID " + entityId + " already loaded.");
            return CompletableFuture.completedFuture(loadedEntities.get(entityId));
        }
        return CompletableFuture.supplyAsync(() -> {
            String statement = "SELECT * FROM " + this.tableName + " WHERE _id = '" + escapeSql(entityId) + "'";
            try (PreparedStatement stmt = querySQL(statement)) {
                if (stmt == null) {
                    this.logger.log(Level.SEVERE, "Failed to create statement for entity ID: " + entityId);
                    return null;
                }
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet == null) {
                        this.logger.log(Level.SEVERE, "Failed to load entity with ID: " + entityId + " because the result set was null.");
                        return null;
                    }
                    if (resultSet.next()) {
                        this.checkForConstructorWithSingleString(this.entityClass);
                        V entity = this.entityClass.getConstructor(String.class).newInstance(entityId);
                        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                            String columnName = resultSet.getMetaData().getColumnName(i + 1);
                            Object columnValue = resultSet.getObject(i + 1);
                            entity.setValue(columnName, columnValue);
                        }
                        this.loadedEntities.append(entityId, entity);
                        return entity;
                    }
                }
            } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchConstructorException e) {
                this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            }

            return null;
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
     * @param checkDb   Check if the entity exists in the database
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
                String statement = "SELECT COUNT(*) FROM " + this.tableName + " WHERE _id = '" + escapeSql(entityId) + "'";
                try (PreparedStatement stmt = querySQL(statement)) {
                    if (stmt == null) {
                        this.logger.log(Level.SEVERE, "Failed to create statement for entity ID: " + entityId);
                        return false;
                    }
                    try (ResultSet resultSet = stmt.executeQuery()) {
                        if (resultSet == null) {
                            this.logger.log(Level.SEVERE, "Failed to check if entity with ID: " + entityId + " exists because the result set was null.");
                            return false;
                        }
                        resultSet.next();
                        int count = resultSet.getInt(1);
                        return forceLoad ? this.load(entityId) != null : count > 0;
                    }
                } catch (SQLException e) {
                    this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
                    return false;
                }
            }
            return false;
        });
    }

    /**
     * Deletes the entity with the specified ID from the database and removes it from the loaded entities.
     *
     * @param entityId The ID of the entity to be deleted.
     */
    @Override
    public CompletableFuture<Void> delete(@NotNull String entityId) {
        return exists(entityId, true, false).thenComposeAsync(exists -> CompletableFuture.runAsync(() -> {
            if (!exists) {
                this.logger.log(Level.WARNING, "[EntityRepository]: Entity with ID " + entityId + " does not exist.");
                return;
            }
            String statement = this.database.buildDeleteStatement(
                    this.tableName,
                    "_id = '" + escapeSql(entityId) + "'"
            );

            this.executeStatement(statement);
            this.loadedEntities.remove(entityId);
        }));
    }

    /**
     * Loads all entities from the database.
     */
    @Override
    public void loadAll() {
        String statement = "SELECT * FROM " + this.tableName;
        try (PreparedStatement preparedStatement = this.querySQL(statement)) {
            if (preparedStatement == null) {
                this.logger.log(Level.SEVERE, "Failed to create statement for loading all entities.");
                return;
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet == null) {
                    this.logger.log(Level.SEVERE, "Failed to load all entities because the result set was null.");
                    return;
                }
                while (resultSet.next()) {
                    this.load(resultSet.getString("_id"));
                }
            }
        } catch (SQLException e) {
            this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
        }
    }

    /**
     * Saves all loaded entities to the database.
     */
    @Override
    public void saveAll() {
        for (V entity : this.loadedEntities.values()) {
            this.save(entity);
        }
    }

    /**
     * Returns the database instance.
     *
     * @return The database instance.
     */
    public Database getDatabase() {
        return database;
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
     * Returns a list of all loaded entities
     *
     * @return The list of loaded entities
     */
    @UnmodifiableView @Override public List<V> getLoadedEntities() {
        return this.loadedEntities.values().stream().toList();
    }

    private String escapeSql(String input) {
        return input.replace("'", "''");
    }
}
