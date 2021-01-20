package com.optimus.eds.utils;

import java.util.Collection;

public class CollectionUtil {

    public static <T> T find(final Collection<T> collection, final Predicate<T> predicate){
        for (T item : collection){
            if (predicate.contains(item)){
                return item;
            }
        }
        return null;
    }
    // and many more methods to deal with collection

    public interface Predicate<T> {
        boolean contains(T item);
    }
}

