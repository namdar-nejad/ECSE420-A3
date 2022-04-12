package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Structure inspired by Chapter 10.3 of The Art of Multicore Programming
 **/
public class BoundedQueue<T> {

    private final T[] queue;
    private volatile int head;
    private volatile int tail;

    private final Lock deqLock = new ReentrantLock();
    private final Lock enqLock = new ReentrantLock();
    private final Condition notEmpty = deqLock.newCondition();
    private final Condition notFull  = enqLock.newCondition();

    public BoundedQueue(int capacity){
        queue = (T[]) new Object[capacity];
        head = 0;
        tail = 0;
    }

    public void enq(T x) {
        boolean mustWakeDequeuers = false;

        enqLock.lock();
        try {
            while (tail - head == queue.length) {
                notFull.await();
            }

            if (tail == head) {
                mustWakeDequeuers = true;
            }

            queue[tail % queue.length] = x;
            tail++;
        } catch (InterruptedException ignored) {
        } finally {
            enqLock.unlock();
        }

        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                notEmpty.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

    public T deq() {
        boolean mustWakeEnqueuers = false;
        T result = null;

        deqLock.lock();
        try {
            while (tail == head) {
                notEmpty.await();
            }

            if (tail - head == queue.length) {
                mustWakeEnqueuers = true;
            }

            result = queue[head % queue.length];
            head++;
        } catch (InterruptedException ignored) {
        } finally {
            deqLock.unlock();
        }

        if (mustWakeEnqueuers) {
            enqLock.lock();
            try {
                notFull.signalAll();
            } finally {
                enqLock.unlock();
            }
        }
        return result;
    }
}
