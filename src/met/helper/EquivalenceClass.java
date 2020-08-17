package met.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EquivalenceClass<T> implements Iterable<T> {

    // all items that pairwise equivalent
    private List<T> equivalentItems;

    /**
     * Return an empty equivalence class
     */
    public EquivalenceClass() {
        equivalentItems = new ArrayList<>();
    }

    /**
     * Create a new equivalence class with at least one item.
     *
     * @param representative An item representing this class.
     */
    public EquivalenceClass(T representative) {
        equivalentItems = new ArrayList<>();
        equivalentItems.add(representative);
    }

    /**
     * Add an item to this class.
     *
     * @param item
     */
    public void add(T item) {
        equivalentItems.add(item);
    }

    /**
     * Return the representative of this class.
     *
     * @return
     */
    public T getRepresentative() {
        if (equivalentItems.isEmpty())
            return null;
        return equivalentItems.get(0);
    }

    /**
     * Return the set of molecules in this class.
     *
     * @return
     */
    public List<T> getItems() {
        return equivalentItems;
    }

    /**
     * Return the number of elements in this class.
     *
     * @return
     */
    public int size() {
        return equivalentItems.size();
    }

    @Override
    public Iterator<T> iterator() {
        return equivalentItems.iterator();
    }

}