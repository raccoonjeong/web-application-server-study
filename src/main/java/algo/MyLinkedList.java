package algo;

import java.util.NoSuchElementException;

class MyLinkedList {
    private static class Node {
        int val;
        Node next;
        public void setNext(Node n) {
            next = n;
        }
        public Node getNext() {
            return next;
        }
        public int getVal() {
            return val;
        }
        Node(int val) { this.val = val; }
    }

    private Node head;
    private Node tail;
    private int size;

    public void addFirst(int x) {
        Node newNode = new Node(x);
        if (head != null) {
            newNode.setNext(head);
        }
        head = newNode;
        size ++;
    }

    public void addLast(int x) {
        Node newNode = new Node(x);
        if (tail != null) {
            tail.setNext(newNode);
        }
        tail = newNode;
        size ++;
    }

    public int removeFirst() {
        if (head == null) {
            throw new NoSuchElementException();
        }
        Node next = head.getNext();
        head = next;
        size --;
        return -1;
    }

    public int removeLast() {
        Node temp = head;
        if (temp == null) {
            throw new NoSuchElementException();
        }
        Node prev = null;
        while (true) {
            if (temp.getNext() == null) {
                if (prev == null) {
                    head = null;
                    break;
                }
                prev.setNext(null);
                break;
            } else {
                prev = temp;
                temp = temp.getNext();
            }
        }
        size --;
        return -1;
    }

    public int get(int index) {
        // TODO
        Node temp = head;
        if (temp == null) {
            throw new IndexOutOfBoundsException();
        }
        int i = 0;
        while (true) {
            if (temp == null) {
                throw new IndexOutOfBoundsException();
            }
            if (i == index) {
                return temp.getVal();
            }
            temp = temp.getNext();
            i ++;
        }
    }

    public int size() {
        // TODO
        return size;
    }


}
