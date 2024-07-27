package com.georgev22.library.utilities;

import com.georgev22.library.database.sql.Database;
import com.georgev22.library.maps.ObservableObjectMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple repository manager for MySQL entities requiring setters for each column and a constructor without varargs.
 *
 * @param <V> The type of the entity.
 */
public class MySQLEntityRepository<V extends Entity> implements EntityRepository<V> {

    private final ObservableObjectMap<String, V> loadedEntities = new ObservableObjectMap<>();
    private final Database database;
    private final Logger logger;
    private final Class<V> entityClass;
    private final String tableName;
    private final Gson gson;

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
     * Constructs a MySQLEntityRepository with the specified database, logger, entity class, and table name.
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
        this.gson = new GsonBuilder().create();
    }

    /**
     * Saves the given entity to the database. Generates and executes an SQL statement based on entity changes.
     *
     * @param entity The entity to be saved.
     */
    @Override
    public CompletableFuture<V> save(@NotNull V entity) {
        return exists(entity._id(), true, false).thenApplyAsync(exists -> {
            String entityId = escapeSql(entity._id());
            String json = gson.toJson(entity);
            String statement;
            if (exists) {
                statement = "UPDATE " + tableName + " SET data = ? WHERE _id = ?";
            } else {
                statement = "INSERT INTO " + tableName + " (_id, data) VALUES (?, ?)";
            }

            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, json);
                preparedStatement.setString(2, entityId);
                preparedStatement.executeUpdate();
            } catch (SQLException | ClassNotFoundException e) {
                this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
                return null;
            }

            this.loadedEntities.append(entity._id(), entity);
            return entity;
        });
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
            String statement = "SELECT data FROM " + this.tableName + " WHERE _id = '" + escapeSql(entityId) + "'";
            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String json = resultSet.getString("data");
                        V entity = gson.fromJson(json, entityClass);
                        this.loadedEntities.append(entityId, entity);
                        return entity;
                    }
                }
            } catch (SQLException | ClassNotFoundException e) {
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
                try (Connection connection = this.database.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(statement);
                     ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    return forceLoad ? this.load(entityId) != null : count > 0;
                } catch (SQLException | ClassNotFoundException e) {
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
            String statement = "DELETE FROM " + this.tableName + " WHERE _id = '" + escapeSql(entityId) + "'";

            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            } catch (SQLException | ClassNotFoundException e) {
                this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            }
            this.loadedEntities.remove(entityId);
        }));
    }

    /**
     * Loads all entities from the database.
     */
    @Override
    public CompletableFuture<BigInteger> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            String statement = "SELECT _id, data FROM " + this.tableName;
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            AtomicReference<BigInteger> count = new AtomicReference<>(BigInteger.ZERO);

            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(statement);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("_id");
                    String json = resultSet.getString("data");
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        V entity = gson.fromJson(json, entityClass);
                        if (entity != null) {
                            this.loadedEntities.append(id, entity);
                            count.updateAndGet(current -> current.add(BigInteger.ONE));
                        }
                    });
                    futures.add(future);
                }
            } catch (SQLException | ClassNotFoundException e) {
                this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            return allOf.thenApply(v -> count.get());
        }).thenCompose(countFuture -> countFuture);
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
     * Returns an observable map of all loaded entities.
     *
     * @return an ObservableObjectMap containing all loaded entities
     */
    @Override
    public ObservableObjectMap<String, V> getLoadedEntities() {
        return this.loadedEntities;
    }

    private String escapeSql(String input) {
        return input.replace("'", "''");
    }
}
