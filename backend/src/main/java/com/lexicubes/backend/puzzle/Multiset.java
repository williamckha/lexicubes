package com.lexicubes.backend.puzzle;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A multiset (a.k.a. "bag") is a set that allows for multiple instances for each
 * of its elements. The number of instances for each element is called the
 * multiplicity of that element in the multiset.
 *
 * @param <T> the type of elements in this multiset
 */
public class Multiset<T> extends AbstractCollection<T> {

    private final Map<T, Integer> counter = new HashMap<>();

    /**
     * Adds a single occurrence of the specified element to this multiset,
     * increasing its multiplicity by one.
     *
     * @param element element to add one occurrence of
     * @return true always, since the given element will always be added to the
     * multiset unlike other Collection types
     */
    @Override
    public boolean add(T element) {
        counter.put(element, counter.getOrDefault(element, 0) + 1);
        return true;
    }

    /**
     * Removes an occurrence of the specified from this multiset, decreasing its
     * multiplicity by one.
     *
     * @param element element to remove an occurrence of
     * @return true if an occurrence was found and removed
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object element) {
        try {
            if (element != null && counter.containsKey((T) element)) {
                counter.put((T) element, counter.get((T) element) - 1);
                if (counter.get((T) element) <= 0) {
                    counter.remove(element);
                }
                return true;
            }
        } catch (ClassCastException ignored) {}
        return false;
    }

    /**
     * Returns true if this multiset contains at least once occurrence of each
     * element in the given collection.
     *
     * @param collection collection to be checked for containment in this multiset
     * @return true if this multiset contains at least once occurrence of each element
     * contained in {@code collection}
     */
    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return counter.keySet().containsAll(collection);
    }

    /**
     * For each occurrence of an element in the given collection, adds an occurrence
     * of that element to this multiset.
     *
     * @param collection collection containing elements to be added to this multiset
     * @return true if this multiset changed as a result of the call
     */
    @Override
    public boolean addAll(@NotNull Collection<? extends T> collection) {
        boolean changed = false;
        for (final T element : collection) {
            changed = changed || add(element);
        }
        return changed;
    }

    /**
     * For each occurrence of an element in the given collection, removes an occurrence
     * of that element from this multiset.
     *
     * @param collection collection containing elements to be removed from this multiset
     * @return true if this multiset changed as a result of the call
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (final Object element : collection) {
            changed = changed || remove(element);
        }
        return changed;
    }

    @Override
    public void clear() {
        counter.clear();
    }

    /**
     * Returns the number of distinct elements in the multiset.
     * Note that this does not return the cardinality (sum of multiplicities)
     * of the multiset.
     *
     * @return the number of distinct elements in the multiset
     */
    @Override
    public int size() {
        return counter.size();
    }

    /**
     * Returns true if this multiset contains at least once occurrence of the
     * specified element.
     *
     * @param element element whose presence in this multiset is to be tested
     * @return true if this multiset contains at least once occurrence of the
     * specified element
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object element) {
        if (element == null) {
            return false;
        }
        try {
            return counter.containsKey((T) element);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * Only one occurrence of each element in the multiset will appear in this iterator.
     */
    @Override
    public @NotNull Iterator<T> iterator() {
        return counter.keySet().iterator();
    }

    /**
     * {@inheritDoc}
     * Only one occurrence of each element in the multiset will appear in the array.
     */
    @Override
    public Object @NotNull [] toArray() {
        return counter.keySet().toArray();
    }

    /**
     * {@inheritDoc}
     * Only one occurrence of each element in the multiset will appear in the array.
     */
    @Override
    public <T1> T1 @NotNull [] toArray(T1 @NotNull [] arr) {
        return counter.keySet().toArray(arr);
    }
}
