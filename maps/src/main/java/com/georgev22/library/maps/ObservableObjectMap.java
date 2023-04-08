package com.georgev22.library.maps;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link ObjectMap} interface that provides an easy way to add listeners
 * that get notified when a new entry is added to the map.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class ObservableObjectMap<K, V> extends ConcurrentObjectMap<K, V> implements ObjectMap<K, V> {

    private List<MapChangeListener<K, V>> listeners = new ArrayList<>();

    /**
     * Adds a {@link MapChangeListener} to this map.
     *
     * @param listener the listener to be added
     */
    public void addListener(MapChangeListener<K, V> listener) {
        listeners.add(listener);
    }

    /**
     * Associates the specified value with the specified key in this map. If the map previously contained a mapping
     * for the key, the old value is replaced by the specified value. Notifies all registered listeners with the
     * added key-value pair.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     */
    @Override
    public V put(@NotNull K key, @NotNull V value) {
        V oldValue = super.put(key, value);
        fireEntryAddedEvent(key, value);
        return oldValue;
    }

    /**
     * Notifies all registered listeners that a new entry has been added to the map.
     *
     * @param key   the key of the added entry
     * @param value the value of the added entry
     */
    private void fireEntryAddedEvent(K key, V value) {
        for (MapChangeListener<K, V> listener : listeners) {
            listener.entryAdded(key, value);
        }
    }

    /**
     * A listener interface for receiving notifications when a new entry is added to an {@link ObservableObjectMap}.
     *
     * @param <K> the type of keys maintained by the map
     * @param <V> the type of mapped values
     */
    public interface MapChangeListener<K, V> {
        /**
         * Called when a new entry is added to the map.
         *
         * @param key   the key of the added entry
         * @param value the value of the added entry
         */
        void entryAdded(K key, V value);
    }

}
