package com.georgev22.library.utilities;

import com.georgev22.library.maps.ObservableObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A utility class for managing CompletableFuture instances.
 */
public class CompletableFutureManager {
    private final ObservableObjectMap<String, CompletableFuture<?>> futures = new ObservableObjectMap<>();
    private final ObservableObjectMap<String, Object> resultValues = new ObservableObjectMap<>();

    /**
     * Registers a CompletableFuture with the given key in the manager.
     *
     * @param key    The key to use for the CompletableFuture.
     * @param future The CompletableFuture to register.
     * @param <T>    The type of the CompletableFuture.
     * @return The same CompletableFuture instance that was passed as a parameter.
     */
    public synchronized <T> CompletableFuture<T> addCompletableFuture(String key, CompletableFuture<T> future) {
        futures.append(key, future);
        future.whenCompleteAsync((result, exception) -> {
            resultValues.append(key, result);
            futures.remove(key);
        });
        return future;
    }

    /**
     * Checks if a CompletableFuture with the given key is currently running.
     *
     * @param key The key of the CompletableFuture to check.
     * @return True if the CompletableFuture is running, false otherwise.
     */
    public synchronized boolean isTaskInProgress(String key) {
        return futures.containsKey(key) && !futures.get(key).isDone();
    }

    /**
     * Waits for the CompletableFuture with the given key to complete and returns its result.
     *
     * @param key The key of the CompletableFuture to wait for.
     * @param <T> The type of the CompletableFuture.
     * @return The result of the CompletableFuture.
     * @throws ExecutionException   If the CompletableFuture completed exceptionally.
     * @throws InterruptedException If the current thread was interrupted while waiting for the CompletableFuture to complete.
     */
    public synchronized <T> T getResultFromTask(String key) throws ExecutionException, InterruptedException {
        CompletableFuture<?> future = futures.get(key);
        if (future == null) {
            return (T) resultValues.get(key);
        }
        future.join();
        Object result = future.get();
        resultValues.put(key, result);
        return (T) result;
    }

    /**
     * Returns the CompletableFutureManager instance with the given key.
     *
     * @param key The key of the CompletableFutureManager instance to return.
     * @return The CompletableFutureManager instance.
     * @throws IllegalArgumentException If no CompletableFuture is registered with the given key.
     */
    public synchronized CompletableFutureManager getInstanceForKey(String key) {
        if (!futures.containsKey(key)) {
            throw new IllegalArgumentException(String.format("No CompletableFuture found with key '%s'", key));
        }
        return this;
    }

    /**
     * Registers listeners for changes to the ObservableObjectMap containing the CompletableFuture instances.
     *
     * @param listeners The listeners to register.
     */
    @SafeVarargs
    public final void registerListenerForFutures(ObservableObjectMap.MapChangeListener<String, CompletableFuture<?>> @NotNull ... listeners) {
        for (ObservableObjectMap.MapChangeListener<String, CompletableFuture<?>> listener : listeners) {
            futures.addListener(listener);
        }
    }

    /**
     * Registers listeners for changes to the ObservableObjectMap containing the CompletableFuture result values.
     *
     * @param listeners The listeners to register.
     */
    @SafeVarargs
    public final void registerListenerForValues(ObservableObjectMap.MapChangeListener<String, Object> @NotNull ... listeners) {
        for (ObservableObjectMap.MapChangeListener<String, Object> listener : listeners) {
            resultValues.addListener(listener);
        }
    }

    /**
     * Removes the specified MapChangeListener from the ObservableObjectMap containing the CompletableFuture instances.
     *
     * @param mapChangeListener The MapChangeListener to remove.
     */
    public void removeFutureListener(ObservableObjectMap.MapChangeListener<String, CompletableFuture<?>> mapChangeListener) {
        futures.removeListener(mapChangeListener);
    }

    /**
     * Removes the specified MapChangeListener from the ObservableObjectMap containing the CompletableFuture result values.
     *
     * @param mapChangeListener The MapChangeListener to remove.
     */
    public void removeValueListener(ObservableObjectMap.MapChangeListener<String, Object> mapChangeListener) {
        resultValues.removeListener(mapChangeListener);
    }

    /**
     * Removes all registered MapChangeListeners from both ObservableObjectMap instances.
     */
    public void removeListeners() {
        // Create separate lists to avoid concurrent modification or unmodifiable exceptions
        List<ObservableObjectMap.MapChangeListener<String, CompletableFuture<?>>> futureListeners = new ArrayList<>(futures.getListeners());
        futureListeners.forEach(this::removeFutureListener);
        List<ObservableObjectMap.MapChangeListener<String, Object>> resultListeners = new ArrayList<>(resultValues.getListeners());
        resultListeners.forEach(this::removeValueListener);
    }
}