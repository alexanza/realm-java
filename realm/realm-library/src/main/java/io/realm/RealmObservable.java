package io.realm;

public interface RealmObservable<T> {
    void addChangeListener(RealmChangeListener<T> listener);

    /**
     * Removes the specified change listener.
     *
     * @param listener the change listener to be removed.
     * @throws IllegalArgumentException if the change listener is {@code null}.
     * @throws IllegalStateException if you try to remove a listener from a non-Looper Thread.
     * @see io.realm.RealmChangeListener
     */
    void removeChangeListener(RealmChangeListener<T> listener);

    /**
     * Removes all user-defined change listeners.
     *
     * @throws IllegalStateException if you try to remove listeners from a non-Looper Thread.
     * @see io.realm.RealmChangeListener
     */
    void removeAllChangeListeners();
}
