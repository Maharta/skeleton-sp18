import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A Generic heap class. Unlike Java's priority queue, this heap doesn't just
 * store Comparable objects. Instead, it can store any type of object
 * (represented by type T), along with a priority value. Why do it this way? It
 * will be useful later on in the class...
 */
public class ArrayHeap<T> implements ExtrinsicPQ<T> {
    private Node[] contents;
    private int size;

    public ArrayHeap() {
        contents = new ArrayHeap.Node[16];

        /* Add a dummy item at the front of the ArrayHeap so that the getLeft,
         * getRight, and parent methods are nicer.
         * This means that the highest priority item is in index 1. */
        contents[0] = null;

        /* Even though there is an empty spot at the front, we still consider
         * the size to be 0 since nothing has been inserted yet. */
        size = 0;
    }

    /**
     * Returns the index of the node to the left of the node at i.
     */
    private static int leftIndex(int i) {
        return i * 2;
    }

    /**
     * Returns the index of the node to the right of the node at i.
     */
    private static int rightIndex(int i) {
        return (i * 2) + 1;
    }

    /**
     * Returns the index of the node that is the parent of the node at i.
     */
    private static int parentIndex(int i) {
        return i / 2;
    }

    /**
     * Gets the node at the ith index, or returns null if the index is out of
     * bounds.
     */
    private Node getNode(int index) {
        if (!inBounds(index)) {
            return null;
        }
        return contents[index];
    }

    /**
     * Returns true if the index corresponds to a valid item. For example, if
     * we have 5 items, then the valid indices are 1, 2, 3, 4, 5. Index 0 is
     * invalid because we leave the 0th entry blank.
     */
    private boolean inBounds(int index) {
        if ((index > size) || (index < 1)) {
            return false;
        }
        return true;
    }


    /** Returns true if the provided index is out of bounds of the current priority queue. */
    private boolean outOfBounds(int index) {
        return index > size || index < 1;
    }

    /**
     * Swap the nodes at the two indices.
     */
    private void swap(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        contents[index1] = node2;
        contents[index2] = node1;
    }


    /**
     * Returns the index of the node with smaller priority. Precondition: not
     * both nodes are null.
     */
    private int min(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        if (node1 == null) {
            return index2;
        } else if (node2 == null) {
            return index1;
        } else if (node1.myPriority < node2.myPriority) {
            return index1;
        } else {
            return index2;
        }
    }


    /**
     * Bubbles up the node currently at the given index.
     */
    private void swim(int index) {
        // Throws an exception if index is invalid. DON'T CHANGE THIS LINE.
        validateSinkSwimArg(index);

        if (index == 1) {
            return;
        }

        int parentIndex = parentIndex(index);
        int smallerOne = min(index, parentIndex);

        if (smallerOne == index) {
            swap(index, parentIndex);
            swim(parentIndex);
        }
    }

    /**
     * Bubbles down the node currently at the given index.
     */
    private void sink(int index) {
        // Throws an exception if index is invalid. DON'T CHANGE THIS LINE.
        validateSinkSwimArg(index);

        int leftChildIndex = leftIndex(index);
        int rightChildIndex = rightIndex(index);

        if (outOfBounds(leftChildIndex) && outOfBounds(rightChildIndex)) {
            return;
        }

        int minChild = min(leftChildIndex, rightChildIndex);

        int smallerOne = min(index, minChild);

        if (smallerOne == minChild) {
            swap(index, minChild);
            sink(minChild);
        }
    }

    /**
     * Inserts an item with the given priority value. This is enqueue, or offer.
     * To implement this method, add it to the end of the ArrayList, then swim it.
     */
    @Override
    public void insert(T item, double priority) {
        /* If the array is totally full, resize. */
        if (size + 1 == contents.length) {
            resize(contents.length * 2);
        }
        int insertIndex = ++size;
        contents[insertIndex] = new Node(item, priority);
        swim(insertIndex);
    }

    /**
     * Returns the Node with the smallest priority value, but does not remove it
     * from the heap. To implement this, return the item in the 1st position of the ArrayList.
     */
    @Override
    public T peek() {
        int rootIndex = 1;
        Node firstNode = getNode(rootIndex);
        if (firstNode != null) {
            return firstNode.item();
        }
        return null;
    }

    /**
     * Returns the Node with the smallest priority value, and removes it from
     * the heap. This is dequeue, or poll. To implement this, swap the last
     * item from the heap into the root position, then sink the root. This is
     * equivalent to firing the president of the company, taking the last
     * person on the list on payroll, making them president, and then demoting
     * them repeatedly. Make sure to avoid loitering by nulling out the dead
     * item.
     */
    @Override
    public T removeMin() {
        if (size == 0) {
            return null;
        }

        int rootIndex = 1;

        T minItem = peek();

        swap(rootIndex, size);
        contents[size--] = null;
        sink(rootIndex);

        return minItem;
    }

    /**
     * Returns the number of items in the PQ. This is one less than the size
     * of the backing ArrayList because we leave the 0th element empty. This
     * method has been implemented for you.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Change the node in this heap with the given item to have the given
     * priority. You can assume the heap will not have two nodes with the same
     * item. Check item equality with .equals(), not ==. This is a challenging
     * bonus problem, but shouldn't be too hard if you really understand heaps
     * and think about the algorithm before you start to code.
     */
    @Override
    public void changePriority(T item, double priority) {
        int firstIndex = 1;
        int secondIndex = 1; // checking two children at the same time to have faster runtime. On first iteration this will be 1 (also the root)
        changePriority(item, priority, firstIndex, secondIndex);
    }

    /**
     * Implemented changePriority with recursion.
     * this algorithm check 2 children at the same time. Also works when there is multiple same item in the heap. */
    private void changePriority(T item, double priority, int firstIndex, int secondIndex) {
        Node firstNode = getNode(firstIndex);
        Node secondNode = getNode(secondIndex);

        if (firstNode == null && secondNode == null) {
            return;
        }

        if (firstNode != null && firstNode.item().equals(item)) {
            double oldPriority = firstNode.priority();
            firstNode.myPriority = priority;
            sinkOrSwim(firstIndex, oldPriority);
        }

        if (secondNode != null && secondNode.item().equals(item)) {
            double oldPriority = secondNode.priority();
            secondNode.myPriority = priority;
            sinkOrSwim(secondIndex, oldPriority);
        }

        int[] firstNodeChildren = getChildrenIndices(firstIndex);
        changePriority(item, priority, firstNodeChildren[0], firstNodeChildren[1]);

        if (firstIndex != secondIndex) {
            int[] secondNodeChildren = getChildrenIndices(secondIndex);
            changePriority(item, priority, secondNodeChildren[0], secondNodeChildren[1]);
        }
    }

    private int[] getChildrenIndices(int index) {
        int left = leftIndex(index);
        int right = rightIndex(index);
        return new int[]{left, right};
    }

    /** simple / clean version of change priority. The recursive version that I did was for practice. */
    private void changePrioritySimple(T item, double priority) {
        for (int i = 1; i <= size; i++) {
            Node currNode = getNode(i);
            if (currNode == null) continue;
            if (currNode.item().equals(item)) {
                double oldPriority = currNode.priority();
                currNode.myPriority = priority;
                sinkOrSwim(i, oldPriority);
            }
        }
    }

    /** helper method to determine whether a changed item need to swim or sink after it's priority changed. */
    private void sinkOrSwim(int index, double oldPriority) {
        validateSinkSwimArg(index);
        Node node = getNode(index);
        if (node == null) {
            return;
        }
        if (node.priority() > oldPriority) {
            sink(index);
        } else {
            swim(index);
        }
    }

    /**
     * Prints out the heap sideways. Provided for you.
     */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getNode(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = rightIndex(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getNode(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getNode(index) + "\n";
            int leftChild = leftIndex(index);
            if (getNode(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }


    /**
     * Throws an exception if the index is invalid for sinking or swimming.
     */
    private void validateSinkSwimArg(int index) {
        if (index < 1) {
            throw new IllegalArgumentException("Cannot sink or swim nodes with index 0 or less");
        }
        if (index > size) {
            throw new IllegalArgumentException("Cannot sink or swim nodes with index greater than current size.");
        }
        if (contents[index] == null) {
            throw new IllegalArgumentException("Cannot sink or swim a null node.");
        }
    }

    private class Node {
        private T myItem;
        private double myPriority;

        private Node(T item, double priority) {
            myItem = item;
            myPriority = priority;
        }

        public T item(){
            return myItem;
        }

        public double priority() {
            return myPriority;
        }

        @Override
        public String toString() {
            return myItem.toString() + ", " + myPriority;
        }
    }


    /** Helper function to resize the backing array when necessary. */
    private void resize(int capacity) {
        Node[] temp = new ArrayHeap.Node[capacity];
        for (int i = 1; i < this.contents.length; i++) {
            temp[i] = this.contents[i];
        }
        this.contents = temp;
    }

    @Test
    public void testIndexing() {
        assertEquals(6, leftIndex(3));
        assertEquals(10, leftIndex(5));
        assertEquals(7, rightIndex(3));
        assertEquals(11, rightIndex(5));

        assertEquals(3, parentIndex(6));
        assertEquals(5, parentIndex(10));
        assertEquals(3, parentIndex(7));
        assertEquals(5, parentIndex(11));
    }

    @Test
    public void testSwim() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.size = 7;
        for (int i = 1; i <= 7; i += 1) {
            pq.contents[i] = new ArrayHeap<String>.Node("x" + i, i);
        }
        // Change item x6's priority to a low value.

        pq.contents[6].myPriority = 0;
        System.out.println("PQ before swimming:");
        System.out.println(pq);

        // Swim x6 upwards. It should reach the root.

        pq.swim(6);
        System.out.println("PQ after swimming:");
        System.out.println(pq);
        assertEquals("x6", pq.contents[1].myItem);
        assertEquals("x2", pq.contents[2].myItem);
        assertEquals("x1", pq.contents[3].myItem);
        assertEquals("x4", pq.contents[4].myItem);
        assertEquals("x5", pq.contents[5].myItem);
        assertEquals("x3", pq.contents[6].myItem);
        assertEquals("x7", pq.contents[7].myItem);
    }

    @Test
    public void testSink() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.size = 7;
        for (int i = 1; i <= 7; i += 1) {
            pq.contents[i] = new ArrayHeap<String>.Node("x" + i, i);
        }
        // Change root's priority to a large value.
        pq.contents[1].myPriority = 10;
        System.out.println("PQ before sinking:");
        System.out.println(pq);

        // Sink the root.
        pq.sink(1);
        System.out.println("PQ after sinking:");
        System.out.println(pq);
        assertEquals("x2", pq.contents[1].myItem);
        assertEquals("x4", pq.contents[2].myItem);
        assertEquals("x3", pq.contents[3].myItem);
        assertEquals("x1", pq.contents[4].myItem);
        assertEquals("x5", pq.contents[5].myItem);
        assertEquals("x6", pq.contents[6].myItem);
        assertEquals("x7", pq.contents[7].myItem);
    }


    @Test
    public void testInsert() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.insert("c", 3);
        assertEquals("c", pq.contents[1].myItem);

        pq.insert("i", 9);
        assertEquals("i", pq.contents[2].myItem);

        pq.insert("g", 7);
        pq.insert("d", 4);
        assertEquals("d", pq.contents[2].myItem);

        pq.insert("a", 1);
        assertEquals("a", pq.contents[1].myItem);

        pq.insert("h", 8);
        pq.insert("e", 5);
        pq.insert("b", 2);
        pq.insert("c", 3);
        pq.insert("d", 4);
        System.out.println("pq after inserting 10 items: ");
        System.out.println(pq);
        assertEquals(10, pq.size());
        assertEquals("a", pq.contents[1].myItem);
        assertEquals("b", pq.contents[2].myItem);
        assertEquals("e", pq.contents[3].myItem);
        assertEquals("c", pq.contents[4].myItem);
        assertEquals("d", pq.contents[5].myItem);
        assertEquals("h", pq.contents[6].myItem);
        assertEquals("g", pq.contents[7].myItem);
        assertEquals("i", pq.contents[8].myItem);
        assertEquals("c", pq.contents[9].myItem);
        assertEquals("d", pq.contents[10].myItem);
    }


    @Test
    public void testInsertAndRemoveOnce() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.insert("c", 3);
        pq.insert("i", 9);
        pq.insert("g", 7);
        pq.insert("d", 4);
        pq.insert("a", 1);
        pq.insert("h", 8);
        pq.insert("e", 5);
        pq.insert("b", 2);
        pq.insert("c", 3);
        pq.insert("d", 4);
        String removed = pq.removeMin();
        assertEquals("a", removed);
        assertEquals(9, pq.size());
        assertEquals("b", pq.contents[1].myItem);
        assertEquals("c", pq.contents[2].myItem);
        assertEquals("e", pq.contents[3].myItem);
        assertEquals("c", pq.contents[4].myItem);
        assertEquals("d", pq.contents[5].myItem);
        assertEquals("h", pq.contents[6].myItem);
        assertEquals("g", pq.contents[7].myItem);
        assertEquals("i", pq.contents[8].myItem);
        assertEquals("d", pq.contents[9].myItem);
    }

    @Test
    public void testInsertAndRemoveAllButLast() {
        ExtrinsicPQ<String> pq = new ArrayHeap<>();
        pq.insert("c", 3);
        pq.insert("i", 9);
        pq.insert("g", 7);
        pq.insert("d", 4);
        pq.insert("a", 1);
        pq.insert("h", 8);
        pq.insert("e", 5);
        pq.insert("b", 2);
        pq.insert("c", 3);
        pq.insert("d", 4);

        int i = 0;
        String[] expected = {"a", "b", "c", "c", "d", "d", "e", "g", "h", "i"};
        while (pq.size() > 1) {
            assertEquals(expected[i], pq.removeMin());
            i += 1;
        }
    }

}
