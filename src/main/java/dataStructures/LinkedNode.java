package dataStructures;

import java.io.Serializable;

public class LinkedNode<E> implements Serializable {
    public E data;
    public LinkedNode<E> next;

    public LinkedNode() {
        this(null);
    }

    public LinkedNode(E data) {
        this(data, null);
    }

    public LinkedNode(E data, LinkedNode<E> next) {
        this.data = data;
        this.next = next;
    }
}
