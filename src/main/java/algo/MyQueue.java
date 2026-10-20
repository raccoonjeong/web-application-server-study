package algo;

import java.util.NoSuchElementException;

// https://sixth-glazer-336.notion.site/Week4-Bonus-3-2ee3d707de248063a607ee8a4c6e514d#2ee3d707de24808d930fdf4eb6d8999b
class MyQueue {

    public static void main(String[] args) {
        MyQueue mq = new MyQueue(5);
        mq.offer(1);
        mq.offer(2);
        System.out.println(mq.poll());
        mq.offer(3);
        System.out.println(mq.poll());
        System.out.println(mq.poll());

    }
    private final int[] arr;
    private int front; // 현재 맨 앞 원소 위치
    private int rear;  // 다음에 삽입할 위치
    private int size;
    private int capacity;

    public MyQueue(int capacity) {
        this.arr = new int[capacity];
        this.capacity = capacity;
    }

    public void offer(int x) {
        // TODO
        if (capacity <= size) {
            throw new IllegalStateException();
        }
        arr[rear] = x;
        rear = (rear + 1) % capacity;
        size ++;
    }

    public int poll() {
        // TODO
        if (size == 0) {
            throw new NoSuchElementException();
        }
        int result = arr[front];
        arr[front] = 0;
        if (size > 1) {
            front = (front + 1) % capacity;
        } else {
            front = 0;
        }
        size --;
        return result;
    }

    public int peek() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return arr[front];
    }

    public boolean isEmpty() {
        // TODO
        return size <= 0;
    }

    public int size() {
        // TODO
        return size;
    }
}
