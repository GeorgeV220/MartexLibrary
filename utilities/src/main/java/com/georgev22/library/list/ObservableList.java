package com.georgev22.library.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A custom list that notifies listeners about add, remove, and set operations.
 *
 * @param <E> the type of elements in this list
 */
public class ObservableList<E> extends ArrayList<E> {
    private final List<ListChangeListener<E>> listeners = new ArrayList<>();

    /**
     * Adds a listener to this list.
     *
     * @param listener the listener to be added
     */
    public void addListChangeListener(ListChangeListener<E> listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from this list.
     *
     * @param listener the listener to be removed
     */
    public void removeListChangeListener(ListChangeListener<E> listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies listeners about an addition of an element.
     *
     * @param element the element that was added
     */
    private void notifyAdd(E element) {
        for (ListChangeListener<E> listener : listeners) {
            listener.onAdd(element);
        }
    }

    /**
     * Notifies listeners about a removal of an element.
     *
     * @param element the element that was removed
     */
    private void notifyRemove(E element) {
        for (ListChangeListener<E> listener : listeners) {
            listener.onRemove(element);
        }
    }

    /**
     * Notifies listeners about a set operation on an element.
     *
     * @param index      the index of the element that was set
     * @param oldElement the old element that was replaced
     * @param newElement the new element that replaced the old element
     */
    private void notifySet(int index, E oldElement, E newElement) {
        for (ListChangeListener<E> listener : listeners) {
            listener.onSet(index, oldElement, newElement);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E element) {
        boolean result = super.add(element);
        if (result) {
            notifyAdd(element);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object element) {
        boolean result = super.remove(element);
        if (result) {
            //noinspection unchecked
            notifyRemove((E) element);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        E oldElement = super.set(index, element);
        notifySet(index, oldElement, element);
        return oldElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        super.add(index, element);
        notifyAdd(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        E element = super.remove(index);
        notifyRemove(element);
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = super.addAll(c);
        if (result) {
            for (E element : c) {
                notifyAdd(element);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean result = super.addAll(index, c);
        if (result) {
            for (E element : c) {
                notifyAdd(element);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        for (E element : this) {
            notifyRemove(element);
        }
        super.clear();
    }
}
