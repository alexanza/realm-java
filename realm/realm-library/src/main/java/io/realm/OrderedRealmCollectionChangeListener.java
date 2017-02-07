package io.realm;

public interface OrderedRealmCollectionChangeListener<T> {
    void onChange(T collection, OrderedCollectionChange changes);
}
