package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreeBoundedQueue<T> {
    private final AtomicReferenceArray<T> queue;

    private final AtomicInteger head, tail, size;
    private final int capacity;

    public LockFreeBoundedQueue(int capacity) {
        this.capacity = capacity;
        queue = new AtomicReferenceArray<>(capacity);

        head = new AtomicInteger(0);
        tail = new AtomicInteger(0);
        size = new AtomicInteger(0);
    }

    public void enq(T x) {
        int curSize = size.get();
        while (curSize == capacity || !size.compareAndSet(curSize, curSize + 1))
            curSize = size.get();

        queue.set(tail.getAndIncrement(), x);

        if (tail.get() == capacity) {
            tail.set(0);
        }
    }

    public T deq() {
        int curSize = size.get();
        while (curSize == 0 || !size.compareAndSet(curSize, curSize - 1))
            curSize = size.get();

        T result = queue.getAndSet(head.getAndIncrement(), null);

        if (head.get() == capacity) {
            head.set(0);
        }

        return result;
    }
}
