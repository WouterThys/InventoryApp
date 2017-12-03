package com.waldo.inventory.database.classes;

import com.waldo.inventory.Main;

import java.util.LinkedList;
import java.util.Queue;

public class DbQueue<T extends DbQueueObject> {

    private Queue<T> queue = new LinkedList<>();
    private int capacity;
    private volatile boolean stopped = false;

    public DbQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T element) throws InterruptedException {
        while (queue.size() >= capacity) {
            if (Main.DEBUG_MODE) System.out.println("DB QUEUE -> MAX CAPACITY");
            wait();
        }

        if (!stopped) {
            if (Main.DEBUG_MODE) System.out.println("DB QUEUE -> ADD ELEMENT ID=" + element.getObject().getId());
            queue.add(element);
            notifyAll();
        }
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            if (Main.DEBUG_MODE) System.out.println("DB QUEUE -> EMPTY");
            wait();
        }

        if (!stopped) {
            T item = queue.remove();
            if (Main.DEBUG_MODE) System.out.println("DB QUEUE -> REMOVE ELEMENT ID=" + item.getObject().getId());
            notifyAll();
            return item;
        }
        return null;
    }

    public synchronized void stop() {
        stopped = true;
        notifyAll();
    }

    public synchronized int size() {
        return queue.size();
    }
}
