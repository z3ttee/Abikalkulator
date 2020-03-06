package de.zitzmanncedric.abicalc.adapter;

import java.util.ArrayList;

public interface DatasetInterface<T> {
    void add(T object);
    void remove(T object);
    void set(ArrayList<T> list);
    void update(T old, T updated);
}
