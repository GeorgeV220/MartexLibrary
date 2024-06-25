package com.georgev22.library.list;

/**
 * Interface for listening to changes in the ObservableList.
 *
 * @param <E> the type of elements in the list
 */
public interface ListChangeListener<E> {
    /**
     * Called when an element is added to the list.
     *
     * @param element the element that was added
     */
    void onAdd(E element);

    /**
     * Called when an element is removed from the list.
     *
     * @param element the element that was removed
     */
    void onRemove(E element);

    /**
     * Called when an element is set in the list.
     *
     * @param index      the index of the element that was set
     * @param oldElement the old element that was replaced
     * @param newElement the new element that replaced the old element
     */
    void onSet(int index, E oldElement, E newElement);
}
