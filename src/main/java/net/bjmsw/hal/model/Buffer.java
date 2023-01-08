package net.bjmsw.hal.model;

public class Buffer {
    private boolean available = false;
    private float data;

    public synchronized void put(float x) {
        while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        data = x;
        available = true;
        notify();
    }

    public synchronized float get() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        available = false;
        notify();
        return data;
    }
}
