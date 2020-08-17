package met.helper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Priority queue to organize at set of items with priorities.
 * Implemented as a binary heap.
 */
public class IndexPriorityQueue {

    /**
     * Auxiliary class.
     */
    private class PQElement {

        int index;
        double priority;

        PQElement(int index, double priority) {
            this.index = index;
            this.priority = priority;
        }
    }

    // stores PQ elements
    private ArrayList<PQElement> elements;

    // current position of each index, or -1 if not included in the pq
    private ArrayList<Integer> position;

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private boolean hasLeft(int i) {
        return left(i) < elements.size();
    }

    private boolean hasRight(int i) {
        return right(i) < elements.size();
    }

    private boolean hasParent(int i) {
        return parent(i) >= 0;
    }

    private void siftUp(int i) {

        while (hasParent(i)) {

            PQElement me = elements.get(i);
            PQElement mother = elements.get(parent(i));

            if (me.priority >= mother.priority)
                break;

            swap(i, parent(i));
            i = parent(i);
        }

    }

    private void siftDown(int i) {

        while (hasLeft(i)) {

            PQElement me = elements.get(i);

            // find child with smallest priority
            int childPosition = left(i);
            PQElement child = elements.get(childPosition);
            if (hasRight(i)) {
                int rightPosition = right(i);
                PQElement rightChild = elements.get(rightPosition);
                if (child.priority > rightChild.priority) {
                    child = rightChild;
                    childPosition = rightPosition;
                }
            }

            if (me.priority <= child.priority)
                break;

            swap(i, childPosition);
            i = childPosition;
        }
    }

    /**
     * Swap the PQ elements at position i and j.
     *
     * @param i Position.
     * @param j Position.
     */
    private void swap(int i, int j) {
        PQElement pi = elements.get(i);
        PQElement pj = elements.get(j);
        Collections.swap(elements, i, j);
        position.set(pi.index, j);
        position.set(pj.index, i);
    }


    /**
     * Create an empty priority queue.
     *
     * @param maxCapacity Maximal number of stored elements.
     */
    public IndexPriorityQueue(int maxCapacity) {
        elements = new ArrayList<>(maxCapacity);
        position = new ArrayList<>(maxCapacity);

        // initially no element is included in the priority queue
        for (int i = 0; i < maxCapacity; i++) {
            position.add(-1);
        }
    }


    /**
     * Return the index with smallest priority, or -1 if empty.
     *
     * @return
     */
    public int peek() {
        if (isEmpty())
            return -1;
        return elements.get(0).index;
    }

    /**
     * Return and remove the element of smallest priority, or return -1 if empty.
     *
     * @return
     */
    public Integer poll() {

        if (isEmpty())
            return -1;

        if (elements.size() == 1) {
            PQElement el = elements.get(0);
            elements.clear();
            position.set(el.index, -1);
            return el.index;
        }

        PQElement firstElement = elements.get(0);
        PQElement lastElement = elements.get(elements.size() - 1);

        // move the last element to the front, thereby overwriting the first element
        elements.set(0, lastElement);
        position.set(lastElement.index, 0);
        elements.remove(elements.size() - 1);
        position.set(firstElement.index, -1);

        // re-establish heap-order
        siftDown(0);

        return firstElement.index;
    }

    /**
     * Add an index to the priority queue.
     *
     * @param index    Index to be added.
     * @param priority Priority.
     */
    public void add(int index, double priority) {

        // index already included?
        if (position.get(index) != -1) {
            System.out.println("Warning! Element " + index + " already in priority queue!");
            changePriority(index, priority);
            return;
        }

        // create a new pq element
        position.set(index, elements.size());
        elements.add(new PQElement(index, priority));

        // establish heap-order
        siftUp(elements.size() - 1);
    }

    /**
     * Return the number of stored elements.
     *
     * @return
     */
    public int size() {
        return elements.size();
    }


    /**
     * Change the priority of an element.
     *
     * @param index       Index whose priority is to be changed.
     * @param newPriority New priority.
     * @return Old priority.
     */
    public double changePriority(int index, double newPriority) {

        // if index is not included in the priority queue
        if (position.get(index) == -1) {
            System.out.println("Warning: Try to change priority of unknown element " + index + "!");
            return Double.NaN;
        }

        int pos = position.get(index);
        PQElement el = elements.get(pos);

        // change priority
        double oldPriority = el.priority;
        el.priority = newPriority;

        // re-establish heap order
        if (oldPriority > newPriority) {
            siftUp(pos);
        } else if (oldPriority < newPriority) {
            siftDown(pos);
        }

        return oldPriority;
    }

    /**
     * Is the priority queue isEmpty?
     *
     * @return
     */
    public boolean isEmpty() {
        return size() == 0;
    }


    /**
     * Remove an index from the priority queue
     *
     * @param index
     */
    public void remove(int index) {

        // if the index is not included in the priority queue
        if (position.get(index) == -1) {
            System.out.println("Warning: Try to remove unknown element " + index + "!");
            return;
        }

        // remove the element
        int pos = position.get(index);
        PQElement removed = elements.get(pos);
        position.set(index, -1);

        // replace the removed element by the rightmost element
        PQElement rightmost = elements.get(elements.size() - 1);
        elements.set(pos, rightmost);
        position.set(rightmost.index, pos);

        // repair heap
        if (rightmost.priority < removed.priority) {
            siftUp(pos);
        } else if (rightmost.priority > removed.priority) {
            siftDown(pos);
        }
    }


    /**
     * Return a string representation.
     *
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PQElement pi : elements) {
            sb.append(pi.index).append(" ").append(pi.priority).append("\n");
        }
        return sb.toString();
    }

}
