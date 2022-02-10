package com.vanzay.aom.game;

import java.util.ArrayList;
import java.util.List;

// use custom PriorityQueue to run on Android versions less than 24
class SimplePriorityQueue<T> {

    static class Item<T> {
        T data;
        int priority;

        Item(T data, int priority) {
            this.data = data;
            this.priority = priority;
        }
    }

    private final List<Item<T>> items = new ArrayList<>();

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void add(T item, int priority) {
        items.add(new Item<>(item, priority));
    }

    public T poll() {
        if (isEmpty()) {
            return null;
        }

        int minPriority = items.get(0).priority;
        int index = 0;
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i).priority < minPriority) {
                minPriority = items.get(i).priority;
                index = i;
            }
        }

        return items.remove(index).data;
    }
}
