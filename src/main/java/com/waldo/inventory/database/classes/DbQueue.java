package com.waldo.inventory.database.classes;

import java.util.LinkedList;
import java.util.Queue;

public class DbQueue<T extends DbQueueObject> {

    private Queue<T> queue = new LinkedList<>();
    private int capacity;

    public DbQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T element) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }

        queue.add(element);
        notify(); // notifyAll??
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }

        T item = queue.remove();
        notify();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}
