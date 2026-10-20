package algo;

import java.util.NoSuchElementException;

class MyStack {

    public static void main(String[] args) {
        MyStack ms = new MyStack(5);
        ms.push(1);
        ms.push(2);
        System.out.println(ms.peek());
        System.out.println(ms.pop());
        System.out.println(ms.pop());
    }
    private final int[] arr;
    private int top; // 다음에 들어갈 위치(=size)

    public MyStack(int capacity) {
        this.arr = new int[capacity];
    }

    public void push(int x) {
        if (this.arr.length <= top) {
            throw new IllegalStateException();
        }
        this.arr[top] = x;
        top ++;
    }

    public int pop() {

        if (top == 0) {
            throw new NoSuchElementException();
        }
        int result = this.arr[top - 1];
        this.arr[top] = 0;
        top --;
        return result;
    }

    public int peek() {
        if (top == 0) {
            throw new NoSuchElementException();
        }
        return this.arr[top - 1];
    }

    public boolean isEmpty() {
        // TODO
        return top == 0;
    }

    public int size() {
        // TODO
        return top;
    }
}

