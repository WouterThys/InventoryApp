package com.waldo.inventory.database.classes;

import com.waldo.inventory.Main;

import javax.swing.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class DbQueue<T extends DbQueueObject> {

    private Queue<T> queue = new LinkedList<>();
    private int capacity;
    private volatile boolean stopped = false;

    // Extras
    private int sessionMaxCapacity;
    private double sessionAverageTimeInQueue;
    private long count = 0;

    public DbQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T element) throws InterruptedException {
        while (size() >= capacity) {
            if (Main.DEBUG_MODE) logFull();
            wait();
        }

        if (!stopped) {
            if (Main.DEBUG_MODE) {
                logAdd(element);
            }

            queue.add(element);

            if (Main.DEBUG_MODE) {
                if (size() > sessionMaxCapacity) {
                    sessionMaxCapacity = size();
                }
                element.setInsertTime(Calendar.getInstance().getTimeInMillis());
            }
            notifyAll();
        }
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            if (Main.DEBUG_MODE) logEmpty();
            wait();
        }

        if (!stopped) {
            T item = queue.remove();
            if (Main.DEBUG_MODE) {
                logRemove(item);
                item.setRemoveTime(Calendar.getInstance().getTimeInMillis());
                updateAverageTime(item);
            }
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

    private void updateAverageTime(T element) {
        count++;
        long time = element.getTimeInQueue();

        sessionAverageTimeInQueue = (sessionAverageTimeInQueue + time) / count;
    }

    public int getSessionMaxCapacity() {
        return sessionMaxCapacity;
    }

    public double getSessionAverageTimeInQueue() {
        return sessionAverageTimeInQueue;
    }

    public long getCount() {
        return count;
    }

    private void logFull() {
        SwingUtilities.invokeLater(() -> System.out.println("DB QUEUE -> MAX CAPACITY (" + capacity + ")"));
    }

    private void logEmpty() {
        SwingUtilities.invokeLater(() -> System.out.println("DB QUEUE -> EMPTY"));
    }

    private void logAdd(T element) {
        SwingUtilities.invokeLater(() -> System.out.println("DB QUEUE -> ADD TO QUEUE: " +
                element.getObject().getClass() +
                " ID=" + element.getObject().getId()));
    }

    private void logRemove(T item) {
        SwingUtilities.invokeLater(() -> System.out.println("DB QUEUE -> REMOVE FROM QUEUE: " +
                item.getObject().getClass().getSimpleName() +
                " ID=" + item.getObject().getId()));
    }
}
