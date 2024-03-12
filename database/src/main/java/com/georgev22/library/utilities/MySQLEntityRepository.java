package com.georgev22.library.utilities;

import com.georgev22.library.database.Database;
import com.georgev22.library.utilities.annotations.Column;
import com.georgev22.library.utilities.exceptions.NoSuchConstructorException;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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

    /**
     * Constructs a MySQLEntityRepository with the specified database, logger, and entity class.
     *
     * @param database    The database to be used.
     * @param logger      The logger for handling log messages.
     * @param entityClass The class type of the entity managed by this repository.
     */
    public MySQLEntityRepository(Database database, Logger logger, Class<V> entityClass) {
        this.database = database;
        this.logger = logger;
        this.entityClass = entityClass;
    }

    /**
     * Saves the given entity to the database. Generates and executes an SQL statement based on entity changes.
     *
     * @param entity The entity to be saved.
     */
    @Override
    public V save(@NotNull V entity) {
        ObjectMap<String, Object> values = new HashObjectMap<>();
        List<Method> methods = Arrays.stream(entity.getClass().getDeclaredMethods()).filter(
                // Condition 1: Filter methods annotated with @Column
                method -> method.getAnnotation(Column.class) != null
                        // Condition 2: Exclude methods with parameters
                        && method.getParameterCount() == 0
                        // Condition 3: Exclude methods with a return type of void
                        && method.getReturnType() != void.class
                        // Condition 4: Exclude methods starting with "set"
                        && !method.getName().startsWith("set")
                        // Condition 5: Exclude methods with the name "_id" (case-insensitive)
                        && !method.getName().equalsIgnoreCase("_id")
        ).toList();

        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields()).filter(
                // Condition 1: Filter fields annotated with @Column
                field -> field.getAnnotation(Column.class) != null
                        // Condition 2: Exclude fields with the name "_id" (case-insensitive)
                        && !field.getName().equalsIgnoreCase("_id")
        ).toList();

        for (Method method : methods) {
            Column columnAnnotation = method.getAnnotation(Column.class);

            if (columnAnnotation != null) {
                Object result;
                try {
                    result = method.invoke(entity);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
                    return null;
                }
                values.append(columnAnnotation.name(), result);
                entity.setValue(columnAnnotation.name(), result);
            }
        }

        for (Field field : fields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                Object result;
                try {
                    result = field.get(entity);
                } catch (IllegalAccessException e) {
                    this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
                    return null;
                }
                values.append(columnAnnotation.name(), result);
                entity.setValue(columnAnnotation.name(), result);
            }
        }
        String statement;
        if (exists(entity._id(), true, false)) {
            statement = this.database.buildUpdateStatement(this.entityClass.getSimpleName(), values, "_id = " + entity._id());
        } else {
            statement = this.database.buildInsertStatement(this.entityClass.getSimpleName(), new HashObjectMap<String, Object>().append("_id", entity._id()).append(values));
        }

        if (statement.isEmpty()) {
            return null;
        }

        this.executeStatement(statement);

        return entity;
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

    private @Nullable ResultSet querySQL(String statement) {
        try (Connection connection = this.database.getConnection()) {
            if (connection == null || connection.isClosed()) {
                try (Connection newConnection = this.database.openConnection()) {
                    return newConnection.createStatement().executeQuery(statement);
                }
            } else {
                return connection.createStatement().executeQuery(statement);
            }
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
    public V load(String entityId) {
        if (loadedEntities.containsKey(entityId)) {
            this.logger.log(Level.FINE, "Entity with ID " + entityId + " already loaded.");
            return loadedEntities.get(entityId);
        }
        String statement = "SELECT * FROM " + this.entityClass.getSimpleName() + " WHERE _id = " + entityId;

        try (ResultSet resultSet = this.querySQL(statement)) {
            if (resultSet == null) {
                this.logger.log(Level.SEVERE, "Failed to load entity with ID: " + entityId + " because the result set was null.");
                return null;
            }
            if (resultSet.next()) {
                this.checkForConstructorWithSingleVarargString(this.entityClass);
                V entity = this.entityClass.getConstructor(String.class).newInstance(entityId);
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i + 1);
                    Object columnValue = resultSet.getObject(i + 1);
                    entity.setValue(columnName, columnValue);
                }
                loadedEntities.append(entityId, entity);
                return entity;
            }
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchConstructorException e) {
            this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
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
     *
     * @param entityId The ID of the entity to check for existence.
     * @return True if the entity is loaded, false otherwise.
     */
    @Override
    public boolean exists(String entityId, boolean checkDb, boolean forceLoad) {
        if (loadedEntities.containsKey(entityId)) {
            return true;
        }
        if (checkDb) {
            String statement = "SELECT COUNT(*) FROM " + this.entityClass.getSimpleName() + " WHERE _id = " + entityId;
            try (ResultSet resultSet = this.querySQL(statement)) {
                if (resultSet == null) {
                    this.logger.log(Level.SEVERE, "Failed to check if entity with ID: " + entityId + " exists because the result set was null.");
                    return false;
                }
                resultSet.next();
                int count = resultSet.getInt(1);
                return forceLoad ? this.load(entityId) != null : count > 0;
            } catch (SQLException e) {
                this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            }
        }
        return false;
    }

    /**
     * Deletes the entity with the specified ID from the database and removes it from the loaded entities.
     *
     * @param entityId The ID of the entity to be deleted.
     */
    @Override
    public void delete(String entityId) {
        if (!exists(entityId, true, false)) {
            this.logger.log(Level.WARNING, "[EntityRepository]: Entity with ID " + entityId + " does not exist.");
            return;
        }

        String statement = this.database.buildDeleteStatement(
                this.entityClass.getSimpleName(),
                "_id = " + entityId
        );

        this.executeStatement(statement);
        loadedEntities.remove(entityId);
    }

    /**
     * Loads all entities from the database.
     */
    @Override
    public void loadAll() {
        String statement = "SELECT * FROM " + this.entityClass.getSimpleName();
        try (ResultSet resultSet = this.querySQL(statement)) {
            if (resultSet == null) {
                this.logger.log(Level.SEVERE, "Failed to load all entities because the result set was null.");
                return;
            }
            while (resultSet.next()) {
                this.load(resultSet.getString("_id"));
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
        for (V entity : loadedEntities.values()) {
            this.save(entity);
        }
    }

    /**
     * Returns the database instance.
     *
     * @return
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Returns the logger instance.
     *
     * @return
     */
    public Logger getLogger() {
        return logger;
    }
}
