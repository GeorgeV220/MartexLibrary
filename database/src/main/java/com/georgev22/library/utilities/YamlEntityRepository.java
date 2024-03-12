package com.georgev22.library.utilities;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.exceptions.NoSuchConstructorException;
import com.georgev22.library.yaml.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A repository manager for entities using YAML files for storage.
 *
 * @param <V> The type of the entity.
 */
public class YamlEntityRepository<V extends Entity> implements EntityRepository<V> {

    private final ObjectMap<String, V> loadedEntities = new HashObjectMap<>();
    private final File dataFolder;
    private final Logger logger;
    private final Class<V> entityClass;

    /**
     * Constructs a YAML entity repository.
     *
     * @param dataFolder  The folder where YAML files will be stored.
     * @param logger      The logger for handling log messages.
     * @param entityClass The class type of the entity managed by this repository.
     */
    public YamlEntityRepository(File dataFolder, Logger logger, Class<V> entityClass) {
        this.dataFolder = dataFolder;
        this.logger = logger;
        this.entityClass = entityClass;
    }

    /**
     * Saves the entity to a YAML file.
     *
     * @param entity The entity to be saved.
     * @return The saved entity, or null if an error occurred.
     */
    @Override
    public V save(@NotNull V entity) {
        File file = new File(dataFolder, entity._id() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (Map.Entry<String, Object> entry : getValuesMap(entity).entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            return null;
        }

        return entity;
    }

    /**
     * Loads the entity from a YAML file.
     *
     * @param entityId The ID of the entity to be loaded.
     * @return The loaded entity, or null if the entity does not exist or an error occurred.
     */
    @Override
    public V load(String entityId) {
        File file = new File(dataFolder, entityId + ".yml");
        if (!file.exists()) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config == null) {
            return null;
        }

        try {
            this.checkForConstructorWithSingleVarargString(this.entityClass);
            V entity = this.entityClass.getConstructor(String.class).newInstance(entityId);
            for (String key : config.getKeys(false)) {
                entity.setValue(key, config.get(key));
            }
            loadedEntities.put(entityId, entity);
            return entity;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchConstructorException e) {
            this.logger.log(Level.SEVERE, "[EntityRepository]:", e);
            return null;
        }
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
     * @param entityId  The ID of the entity to check for existence.
     * @param checkDb   Check in the folder for the entity.
     * @param forceLoad Force load the entity
     * @return True if the entity is loaded, false otherwise.
     */
    @Override
    public boolean exists(String entityId, boolean checkDb, boolean forceLoad) {
        if (loadedEntities.containsKey(entityId)) {
            return true;
        }

        if (checkDb) {
            File file = new File(dataFolder, entityId + ".yml");
            return forceLoad ? this.load(entityId) != null : file.exists();
        }
        return false;
    }

    /**
     * Deletes the entity with the specified ID.
     *
     * @param entityId The ID of the entity to be deleted.
     */
    @Override
    public void delete(String entityId) {
        File file = new File(dataFolder, entityId + ".yml");
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        loadedEntities.remove(entityId);
    }

    /**
     * Loads all entities from the data folder.
     */
    @Override
    public void loadAll() {
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String entityId = file.getName().replace(".yml", "");
                load(entityId);
            }
        }
    }

    /**
     * Saves all entities to the data folder.
     */
    @Override
    public void saveAll() {
        for (V entity : loadedEntities.values()) {
            save(entity);
        }
    }

    /**
     * Gets the data folder where YAML files are stored.
     *
     * @return The data folder.
     */
    public File getDataFolder() {
        return dataFolder;
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
