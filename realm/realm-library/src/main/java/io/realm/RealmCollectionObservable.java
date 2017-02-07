package io.realm;

public interface RealmCollectionObservable<T, S> extends RealmObservable<T> {
    void addChangeListener(S listener);
    void removeChangeListener(S listener);
}
