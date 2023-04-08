package com.georgev22.library.maps;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ObservableObjectMap<K, V> extends ConcurrentObjectMap<K, V> implements ObjectMap<K, V> {

    private List<MapChangeListener<K, V>> listeners = new ArrayList<>();

    public void addListener(MapChangeListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public V put(@NotNull K key, @NotNull V value) {
        V oldValue = super.put(key, value);
        fireEntryAddedEvent(key, value);
        return oldValue;
    }

    private void fireEntryAddedEvent(K key, V value) {
        for (MapChangeListener<K, V> listener : listeners) {
            listener.entryAdded(key, value);
        }
    }

    public interface MapChangeListener<K, V> {
        void entryAdded(K key, V value);
    }

}
