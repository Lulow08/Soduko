package com.bifasico.soduko.model;

/**
 * A generic stack data structure implemented from scratch without external libraries.
 * Used during Sudoku board generation to support backtracking when the constraint
 * propagation algorithm reaches a dead end.
 *
 * <p>The stack grows dynamically by doubling its internal capacity when full.
 *
 * @param <T> the type of elements held in this stack
 */
public class SudokuStack<T> {

    private static final int INITIAL_CAPACITY = 16;

    private Object[] elements;
    private int size;

    /**
     * Constructs an empty stack with the default initial capacity.
     */
    public SudokuStack() {
        this.elements = new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Pushes an element onto the top of the stack.
     *
     * @param element the element to push
     */
    public void push(T element) {
        ensureCapacity();
        elements[size++] = element;
    }

    /**
     * Removes and returns the element at the top of the stack.
     *
     * @return the top element
     * @throws IllegalStateException if the stack is empty
     */
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía");
        }
        T element = (T) elements[--size];
        elements[size] = null;
        return element;
    }

    /**
     * Returns the element at the top of the stack without removing it.
     *
     * @return the top element
     * @throws IllegalStateException if the stack is empty
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía");
        }
        return (T) elements[size - 1];
    }

    /**
     * Returns whether the stack contains no elements.
     *
     * @return true if the stack is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently in the stack.
     *
     * @return the element count
     */
    public int size() {
        return size;
    }

    /**
     * Removes all elements from the stack.
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            Object[] enlarged = new Object[elements.length * 2];
            System.arraycopy(elements, 0, enlarged, 0, size);
            elements = enlarged;
        }
    }
}