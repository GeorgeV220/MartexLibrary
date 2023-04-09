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
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link UserManager} class is responsible for managing {@link User} objects in a persistence storage.
 * It supports multiple storage types including MySQL, SQLite, PostgreSQL, MongoDB, and JSON.
 * The class provides methods for checking if a {@link User} exists, loading a {@link User}, and creating a {@link User}.
 *
 * @author <a href="https://github.com/GeorgeV220">GeorgeV220</a>
 */
public class UserManager {

    private GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson;
    private final File usersDirectory;
    private final Connection connection;
    private final MongoDB mongoDB;
    private final String collection;
    private final Type type;
    private final ObservableObjectMap<UUID, User> loadedUsers = new ObservableObjectMap<>();

    /**
     * Constructor for the UserManager class
     *
     * @param type           the type of storage system to be used (JSON, SQL or MONGODB)
     * @param obj            the object to be used for storage (File for JSON, Connection for SQL and MongoDB for MONGODB)
     * @param collectionName the name of the collection to be used for MONGODB, null for other types
     */
    public UserManager(@NotNull Type type, Object obj, @Nullable String collectionName) {
        this.type = type;
        this.collection = collectionName;
        if (type.equals(Type.JSON)) {
            this.usersDirectory = (File) obj;
            this.connection = null;
            this.mongoDB = null;
            if (!this.usersDirectory.exists()) {
                this.usersDirectory.mkdirs();
            }
        } else if (type.equals(Type.SQL)) {
            this.usersDirectory = null;
            this.connection = (Connection) obj;
            this.mongoDB = null;
        } else if (type.equals(Type.MONGODB)) {
            this.usersDirectory = null;
            this.connection = null;
            this.mongoDB = (MongoDB) obj;
        } else {
            this.usersDirectory = null;
            this.connection = null;
            this.mongoDB = null;
        }
    }

    /**
     * Adds a type adapter for ObjectMap to the GsonBuilder. Deprecated; use {@link #registerTypeAdaptersByClass} or {@link #registerTypeAdaptersByTypeToken} instead.
     * <p>
     * This method will not be removed, but it is recommended to use the newer methods for registering type adapters.
     *
     * @return this UserManager
     * @deprecated Use {@link #registerTypeAdaptersByClass(ObjectMap.PairDocument)} or {@link #registerTypeAdaptersByTypeToken(ObjectMap.PairDocument)} instead.
     */
    @Deprecated
    public UserManager registerObjectMapSerializer() {
        gsonBuilder.registerTypeAdapter(ObjectMap.class, new ObjectMapSerializerDeserializer());
        return this;
    }

    /**
     * Registers type adapters for the specified classes using the GsonBuilder.
     *
     * @param pairs a PairDocument containing the Class and type adapter pairs to register
     * @return this UserManager
     */
    public UserManager registerTypeAdaptersByClass(@NotNull ObjectMap.PairDocument<Class<?>, Object> pairs) {
        for (ObjectMap.Pair<Class<?>, Object> pair : pairs.objectPairs()) {
            gsonBuilder.registerTypeAdapter(pair.key(), pair.value());
        }
        return this;
    }

    /**
     * Registers type adapters for the specified TypeTokens using the GsonBuilder.
     *
     * @param pairs a PairDocument containing the TypeToken and type adapter pairs to register
     * @return this UserManager
     */
    public UserManager registerTypeAdaptersByTypeToken(@NotNull ObjectMap.PairDocument<TypeToken<?>, Object> pairs) {
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
     * Loads the {@link User} with the specified ID
     *
     * @param userId the {@link UUID} of the user to be loaded
     * @return a {@link CompletableFuture} containing the loaded {@link User} object
     */
    public CompletableFuture<User> load(UUID userId) {
        return exists(userId)
                .thenCompose(exists -> {
                    if (exists) {
                        return CompletableFuture.supplyAsync(() -> {
                            switch (type) {
                                case JSON -> {
                                    try (FileReader reader = new FileReader(new File(usersDirectory, userId + ".json"))) {
                                        User user = getGson().fromJson(reader, User.class);
                                        loadedUsers.put(userId, user);
                                        return user;
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case SQL -> {
                                    String query = "SELECT user_json FROM " + collection + " WHERE user_id = ?";
                                    try {
                                        PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
                                        statement.setString(1, userId.toString());
                                        ResultSet resultSet = statement.executeQuery();
                                        if (resultSet.next()) {
                                            String userJson = resultSet.getString("user_json");
                                            statement.close();
                                            User user = getGson().fromJson(userJson, User.class);
                                            loadedUsers.put(userId, user);
                                            return user;
                                        } else {
                                            throw new RuntimeException("No user found with id: " + userId);
                                        }
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case MONGODB -> {
                                    Document document = mongoDB.getCollection(collection).find(Filters.eq("userId", userId.toString())).first();
                                    if (document != null) {
                                        User user = getGson().fromJson(document.toJson(), User.class);
                                        loadedUsers.put(userId, user);
                                        return user;
                                    } else {
                                        throw new RuntimeException("No user found with id: " + userId);
                                    }
                                }
                                default -> {
                                    return new User(userId);
                                }
                            }
                        });
                    } else {
                        return createUser(userId);
                    }
                });
    }

    /**
     * Saves the specified {@link User}.
     *
     * @param user the {@link User} to save
     * @return a {@link CompletableFuture} that completes when the {@link User} is saved
     */
    public CompletableFuture<Void> save(User user) {
        return CompletableFuture.runAsync(() -> {
            switch (type) {
                case JSON -> {
                    try (FileWriter writer = new FileWriter(new File(usersDirectory, user.getId() + ".json"))) {
                        getGson().toJson(user, writer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case SQL -> exists(user.getId()).thenAccept(result -> {
                    String query = result ? "UPDATE " + collection + " SET user_json = ? WHERE user_id =  ?;" : "INSERT INTO " + collection + " (user_id, user_json) VALUES (?, ?)";
                    try {
                        PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
                        String userJson = getGson().toJson(user);
                        statement.setString(1, userJson);
                        statement.setString(2, user.getId().toString());
                        statement.executeUpdate();
                        statement.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                case MONGODB -> {
                    MongoCollection<Document> mongoCollection = mongoDB.getCollection(collection);
                    Document document = Document.parse(getGson().toJson(user));
                    mongoCollection.insertOne(document);
                }
            }
        });
    }

    /**
     * Creates a new {@link User} with the specified user ID.
     *
     * @param userId the {@link UUID} of the user to create
     * @return a {@link CompletableFuture} that returns the newly created {@link User}
     */
    public CompletableFuture<User> createUser(UUID userId) {
        User user = new User(userId);
        return save(user)
                .thenApply(aVoid -> {
                    loadedUsers.put(userId, user);
                    return user;
                });
    }

    /**
     * Determines if a {@link User} with the specified user ID exists.
     *
     * @param userId the {@link UUID} of the user to check
     * @return a {@link CompletableFuture} that returns true if a {@link User} with the specified ID exists, false otherwise
     */
    public CompletableFuture<Boolean> exists(UUID userId) {
        return CompletableFuture.supplyAsync(() -> {
            switch (type) {
                case JSON -> {
                    return new File(usersDirectory, userId + ".json").exists();
                }
                case SQL -> {
                    return executeSQLQuery(userId);
                }
                case MONGODB -> {
                    Document user = mongoDB.getCollection(collection).find(Filters.eq("userId", userId)).first();
                    return user != null;
                }
                default -> {
                    return false;
                }
            }
        });
    }

    private @NotNull Boolean executeSQLQuery(@NotNull UUID userId) {
        String query = "SELECT count(*) FROM " + collection + " WHERE user_id = ?";
        try {
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, userId.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            } else {
                throw new RuntimeException("No user found with id: " + userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the {@link User} with the given {@link UUID}.
     * <p>
     * If the user is already loaded, it is returned immediately. If not, it is loaded
     * asynchronously and returned in a {@link CompletableFuture}.
     *
     * @param userId the {@link UUID} of the user to retrieve
     * @return a {@link CompletableFuture} that will contain the {@link User} with the given id
     */
    public CompletableFuture<User> getUser(UUID userId) {
        if (loadedUsers.containsKey(userId)) {
            return CompletableFuture.completedFuture(loadedUsers.get(userId));
        }

        return load(userId);
    }

    /**
     * Saves all the loaded {@link User}s in the {@link #loadedUsers} map.
     * For each {@link User} in the map, this method calls the {@link #save(User)} method to persist the {@link User}.
     */
    public void saveAll() {
        loadedUsers.forEach((uuid, user) -> save(user));
    }

    @Beta
    @Deprecated
    public void loadAll() {
        File[] files = this.usersDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                UUID uuid = UUID.fromString(file.getName().replace(".json", ""));
                load(uuid);
            });
        }
    }

    /**
     * Retrieves the current map of loaded users.
     *
     * @return the map of loaded users with UUID as the key and User object as the value
     */
    public ObservableObjectMap<UUID, User> getLoadedUsers() {
        return loadedUsers;
    }

    /**
     * A class representing a user in the system.
     */
    public static class User {
        private final UUID userId;
        private ObjectMap<String, Object> customData;

        /**
         * Constructs a new user with a random UUID.
         */
        public User() {
            this(UUID.randomUUID());
        }

        /**
         * Constructs a new user with the specified UUID and name.
         *
         * @param userId the UUID of the user
         */
        public User(UUID userId) {
            this.userId = userId;
            this.customData = new ConcurrentObjectMap<>();
        }

        /**
         * Returns the userId of this `User` object.
         *
         * @return the userId of this `User` object.
         */
        public UUID getId() {
            return userId;
        }

        /**
         * Adds a key-value pair to the custom data map.
         *
         * @param key   the key of the data
         * @param value the value of the data
         */
        public User addCustomData(String key, Object value) {
            customData.append(key, value);
            return this;
        }

        /**
         * Adds a key-value pair to the custom data map if the key does not already exist.
         *
         * @param key   the key of the data
         * @param value the value of the data
         */
        public User addCustomDataIfNotExists(String key, Object value) {
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
            return "User{" +
                    "userId=" + userId +
                    ", customData=" + customData +
                    '}';
        }
    }

    /**
     * Represents the type of storage to use for user data.
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
