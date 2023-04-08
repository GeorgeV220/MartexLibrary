package com.georgev22.library.maps;

import com.georgev22.library.exceptions.PairDocumentException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;

public interface ObjectMap<K, V> extends Map<K, V> {

    /**
     * Creates a new empty {@link LinkedObjectMap} instance.
     *
     * @return a new empty {@link LinkedObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull LinkedObjectMap newLinkedObjectMap() {
        return new LinkedObjectMap();
    }

    /**
     * Creates a new empty {@link ConcurrentObjectMap} instance.
     *
     * @return a new empty {@link ConcurrentObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull ConcurrentObjectMap newConcurrentObjectMap() {
        return new ConcurrentObjectMap();
    }

    /**
     * Creates a new empty {@link HashObjectMap} instance.
     *
     * @return a new empty {@link HashObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull HashObjectMap newHashObjectMap() {
        return new HashObjectMap();
    }

    /**
     * Creates a new empty {@link TreeObjectMap} instance.
     *
     * @return a new empty {@link TreeObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull TreeObjectMap newTreeObjectMap() {
        return new TreeObjectMap();
    }

    /**
     * Creates a {@link LinkedObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link LinkedObjectMap#LinkedObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull LinkedObjectMap newLinkedObjectMap(ObjectMap map) {
        return new LinkedObjectMap(map);
    }

    /**
     * Creates a {@link LinkedObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link LinkedObjectMap#LinkedObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull LinkedObjectMap newLinkedObjectMap(Map map) {
        return new LinkedObjectMap(map);
    }

    /**
     * Creates a {@link ConcurrentObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link ConcurrentObjectMap#ConcurrentObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull ConcurrentObjectMap newConcurrentObjectMap(ObjectMap map) {
        return new ConcurrentObjectMap(map);
    }

    /**
     * Creates a {@link ConcurrentObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link ConcurrentObjectMap#ConcurrentObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull ConcurrentObjectMap newConcurrentObjectMap(Map map) {
        return new ConcurrentObjectMap(map);
    }

    /**
     * Creates a {@link HashObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link HashObjectMap#HashObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull HashObjectMap newHashObjectMap(ObjectMap map) {
        return new HashObjectMap(map);
    }

    /**
     * Creates a {@link HashObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link HashObjectMap#HashObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull HashObjectMap newHashObjectMap(Map map) {
        return new HashObjectMap(map);
    }

    /**
     * Creates a {@link TreeObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link TreeObjectMap#TreeObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull TreeObjectMap newTreeObjectMap(ObjectMap map) {
        return new TreeObjectMap(map);
    }

    /**
     * Creates a {@link TreeObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link TreeObjectMap#TreeObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull TreeObjectMap newTreeObjectMap(Map map) {
        return new TreeObjectMap(map);
    }


    /**
     * Put/replace the given key/value pair into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append("b", 2)}
     * </pre>
     *
     * @param key   key
     * @param value value
     * @return this
     */
    ObjectMap<K, V> append(final K key, final V value);

    /**
     * Put/replace a given map into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append(map)}
     * </pre>
     *
     * @param map the map to append to the current one
     * @return this
     */
    ObjectMap<K, V> append(final Map<K, V> map);

    /**
     * Put/replace a given map into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append(map)}
     * </pre>
     *
     * @param map the map to append to the current one
     * @return this
     */
    ObjectMap<K, V> append(final ObjectMap<K, V> map);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue("b", 2, check2)}
     * </pre>
     *
     * @param key    key
     * @param value  value
     * @param ifTrue ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final K key, final V value, boolean ifTrue);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue("b", 3, 4, check2)}
     * </pre>
     *
     * @param key          key
     * @param valueIfTrue  the value if the ifTrue is true
     * @param valueIfFalse the value if the ifTrue is false
     * @param ifTrue       ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final K key, final V valueIfTrue, final V valueIfFalse, boolean ifTrue);

    /**
     * Put/replace a given map into this ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue(map, check2)}
     * </pre>
     *
     * @param map    key
     * @param ifTrue ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final Map<K, V> map, boolean ifTrue);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue(map1, map2, check2)}
     * </pre>
     *
     * @param mapIfTrue  the map if the ifTrue is true
     * @param mapIfFalse the map if the ifTrue is false
     * @param ifTrue     ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final Map<K, V> mapIfTrue, final Map<K, V> mapIfFalse, boolean ifTrue);

    /**
     * Put/replace a given map into this ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue(map, check2)}
     * </pre>
     *
     * @param map    key
     * @param ifTrue ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final ObjectMap<K, V> map, boolean ifTrue);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue(map1, map2, check2)}
     * </pre>
     *
     * @param mapIfTrue  the map if the ifTrue is true
     * @param mapIfFalse the map if the ifTrue is false
     * @param ifTrue     ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final ObjectMap<K, V> mapIfTrue, final Map<K, V> mapIfFalse, boolean ifTrue);

    /**
     * Gets the value of the given key as an Integer.
     *
     * @param key the key
     * @return the value as an integer, which may be null
     * @throws ClassCastException if the value is not an integer
     */
    Integer getInteger(final Object key);

    /**
     * Gets the value of the given key as a primitive int.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as an integer, which may be null
     * @throws ClassCastException if the value is not an integer
     */
    int getInteger(final Object key, final int defaultValue);

    /**
     * Gets the value of the given key as a Long.
     *
     * @param key the key
     * @return the value as a long, which may be null
     * @throws ClassCastException if the value is not an long
     */
    Long getLong(final Object key);

    /**
     * Gets the value of the given key as a Long.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a long, which may be null
     * @throws ClassCastException if the value is not an long
     */
    Long getLong(final Object key, final long defaultValue);

    /**
     * Gets the value of the given key as a Double.
     *
     * @param key the key
     * @return the value as a double, which may be null
     * @throws ClassCastException if the value is not an double
     */
    Double getDouble(final Object key);

    /**
     * Gets the value of the given key as a Double.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a double, which may be null
     * @throws ClassCastException if the value is not an double
     */
    Double getDouble(final Object key, final double defaultValue);

    /**
     * Gets the value of the given key as a String.
     *
     * @param key the key
     * @return the value as a String, which may be null
     * @throws ClassCastException if the value is not a String
     */
    String getString(final Object key);

    /**
     * Gets the value of the given key as a String.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a String, which may be null
     * @throws ClassCastException if the value is not a String
     */
    String getString(final Object key, final String defaultValue);

    /**
     * Gets the value of the given key as a Boolean.
     *
     * @param key the key
     * @return the value as a Boolean, which may be null
     * @throws ClassCastException if the value is not an boolean
     */
    Boolean getBoolean(final Object key);

    /**
     * Gets the value of the given key as a primitive boolean.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a primitive boolean
     * @throws ClassCastException if the value is not a boolean
     */
    boolean getBoolean(final Object key, final boolean defaultValue);

    /**
     * Gets the value of the given key as a Date.
     *
     * @param key the key
     * @return the value as a Date, which may be null
     * @throws ClassCastException if the value is not a Date
     */
    Date getDate(final Object key);

    /**
     * Gets the value of the given key as a Date.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a Date, which may be null
     * @throws ClassCastException if the value is not a Date
     */
    Date getDate(final Object key, final Date defaultValue);

    /**
     * Gets the list value of the given key, casting the list elements to the given {@code Class<T>}.  This is useful to avoid having
     * casts in client code, though the effect is the same.
     *
     * @param key   the key
     * @param clazz the non-null class to cast the list value to
     * @param <T>   the type of the class
     * @return the list value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the elements in the list value of the given key is not of type T or the value is not a list
     */
    <T> List<T> getList(Object key, Class<T> clazz);

    /**
     * Gets the list value of the given key, casting the list elements to {@code Class<T>} or returning the default list value if null.
     * This is useful to avoid having casts in client code, though the effect is the same.
     *
     * @param key          the key
     * @param clazz        the non-null class to cast the list value to
     * @param defaultValue what to return if the value is null
     * @param <T>          the type of the class
     * @return the list value of the given key, or the default list value if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    <T> List<T> getList(final Object key, final Class<T> clazz, final List<T> defaultValue);

    /**
     * Gets the value of the given key, casting it to the given {@code Class<T>}.  This is useful to avoid having casts in client code,
     * though the effect is the same.  So to get the value of a key that is of type String, you would write {@code String name =
     * doc.get("name", String.class)} instead of {@code String name = (String) doc.get("x") }.
     *
     * @param key   the key
     * @param clazz the non-null class to cast the value to
     * @param <T>   the type of the class
     * @return the value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    <T> T get(final Object key, final Class<T> clazz);

    /**
     * Gets the value of the given key, casting it to {@code Class<T>} or returning the default value if null.
     * This is useful to avoid having casts in client code, though the effect is the same.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @param <T>          the type of the class
     * @return the value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    <T> T get(final Object key, final T defaultValue);

    record Pair<K, V>(K key, V value) implements Serializable {

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair<?, ?>)) {
                return false;
            }

            Pair<K, V> p = (Pair<K, V>) o;

            return Objects.equals(p.key, key) && Objects.equals(p.value, value);
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "Pair{" +
                    "key=" + key + ", " +
                    "value=" + value + "}";
        }

        @Contract(value = "_, _ -> new", pure = true)
        public static <K, V> @NotNull Pair<K, V> create(K key, V value) {
            return new Pair<>(key, value);
        }
    }

    record PairDocument<K, V>(ObjectMap.Pair<K, V>... objectPairs) implements Serializable {


        @SafeVarargs
        public PairDocument {
            if (objectPairs.length == 0) {
                throw new PairDocumentException("PairDocument is empty");
            }
        }


        /**
         * Gets the value of the given key as an Integer.
         *
         * @param key the key
         * @return the value as an integer, which may be null
         * @throws ClassCastException if the value is not an integer
         */
        public Integer getInteger(final Object key) {
            return getInteger(key, 0);
        }

        /**
         * Gets the value of the given key as a primitive int.
         *
         * @param key          the key
         * @param defaultValue what to return if the value is null
         * @return the value as an integer, which may be null
         * @throws ClassCastException if the value is not an integer
         */
        public int getInteger(final Object key, final int defaultValue) {
            return get(key, defaultValue);
        }

        /**
         * Gets the value of the given key as a Long.
         *
         * @param key the key
         * @return the value as a long, which may be null
         * @throws ClassCastException if the value is not an long
         */
        public Long getLong(final Object key) {
            return getLong(key, 0L);
        }

        /**
         * Gets the value of the given key as a Long.
         *
         * @param key          the key
         * @param defaultValue what to return if the value is null
         * @return the value as a long, which may be null
         * @throws ClassCastException if the value is not an long
         */
        public Long getLong(final Object key, final long defaultValue) {
            return get(key, defaultValue);
        }

        /**
         * Gets the value of the given key as a Double.
         *
         * @param key the key
         * @return the value as a double, which may be null
         * @throws ClassCastException if the value is not an double
         */
        public Double getDouble(final Object key) {
            return getDouble(key, 0D);
        }

        /**
         * Gets the value of the given key as a Double.
         *
         * @param key          the key
         * @param defaultValue what to return if the value is null
         * @return the value as a double, which may be null
         * @throws ClassCastException if the value is not an double
         */
        public Double getDouble(final Object key, final double defaultValue) {
            return get(key, defaultValue);
        }

        /**
         * Gets the value of the given key as a String.
         *
         * @param key the key
         * @return the value as a String, which may be null
         * @throws ClassCastException if the value is not a String
         */
        public String getString(final Object key) {
            return getString(key, "");
        }

        /**
         * Gets the value of the given key as a String.
         *
         * @param key          the key
         * @param defaultValue what to return if the value is null
         * @return the value as a String, which may be null
         * @throws ClassCastException if the value is not a String
         */
        public String getString(final Object key, final String defaultValue) {
            return get(key, defaultValue);
        }

        /**
         * Gets the value of the given key as a Boolean.
         *
         * @param key the key
         * @return the value as a Boolean, which may be null
         * @throws ClassCastException if the value is not an boolean
         */
        public Boolean getBoolean(final Object key) {
            return getBoolean(key, false);
        }

        /**
         * Gets the value of the given key as a primitive boolean.
         *
         * @param key          the key
         * @param defaultValue what to return if the value is null
         * @return the value as a primitive boolean
         * @throws ClassCastException if the value is not a boolean
         */
        public boolean getBoolean(final Object key, final boolean defaultValue) {
            return get(key, defaultValue);
        }

        /**
         * Gets the value of the given key as a Date.
         *
         * @param key the key
         * @return the value as a Date, which may be null
         * @throws ClassCastException if the value is not a Date
         */
        public Date getDate(final Object key) {
            return getDate(key, new Date());
        }

        /**
         * Gets the value of the given key as a Date.
         *
         * @param key          the key
         * @param defaultValue what to return if the value is null
         * @return the value as a Date, which may be null
         * @throws ClassCastException if the value is not a Date
         */
        public Date getDate(final Object key, final Date defaultValue) {
            return get(key, defaultValue);
        }

        /**
         * Gets the list value of the given key, casting the list elements to the given {@code Class<T>}.  This is useful to avoid having
         * casts in client code, though the effect is the same.
         *
         * @param key   the key
         * @param clazz the non-null class to cast the list value to
         * @param <T>   the type of the class
         * @return the list value of the given key, or null if the instance does not contain this key.
         * @throws ClassCastException if the elements in the list value of the given key is not of type T or the value is not a list
         */
        public <T> List<T> getList(Object key, Class<T> clazz) {
            return getList(key, clazz, null);
        }

        /**
         * Gets the list value of the given key, casting the list elements to {@code Class<T>} or returning the default list value if null.
         * This is useful to avoid having casts in client code, though the effect is the same.
         *
         * @param key          the key
         * @param clazz        the non-null class to cast the list value to
         * @param defaultValue what to return if the value is null
         * @param <T>          the type of the class
         * @return the list value of the given key, or the default list value if the instance does not contain this key.
         * @throws ClassCastException if the value of the given key is not of type T
         */
        public <T> List<T> getList(final Object key, final Class<T> clazz, final List<T> defaultValue) {
            List<T> value = get(key, List.class);
            if (value == null) {
                return defaultValue;
            }

            for (Object item : value) {
                if (!clazz.isAssignableFrom(item.getClass())) {
                    throw new ClassCastException(format("List element cannot be cast to %s", clazz.getName()));
                }
            }
            return value;
        }

        public <T> T get(final Object key, final Class<T> clazz) {
            return clazz.cast(get(key));
        }

        public <T> T get(final Object key, final T defaultValue) {
            Object value = get(key);
            return value == null ? defaultValue : (T) value;
        }

        public <T> @Nullable T get(final Object key) {
            AtomicReference<Object> value = new AtomicReference<>();
            Arrays.stream(objectPairs).forEach(objectPair -> {
                if (objectPair.key().equals(key)) {
                    value.set(objectPair.value());
                }
            });
            return value.get() == null ? null : (T) value.get();
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "PairDocument{"
                    + "pairs=" + Arrays.toString(objectPairs)
                    + "}";
        }
    }
}
